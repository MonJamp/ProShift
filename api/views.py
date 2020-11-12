from rest_framework.authtoken.models import Token

from rest_framework.views import APIView

from django.contrib.auth.models import User
from dashboard.models import Shift, RequestedTimeOff, Availability
from django.db.models import Q

from .serializers import *

from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework import status
from rest_framework.response import Response
from django.http import HttpResponse, JsonResponse
from .permissions import IsManager

from django.core.exceptions import ObjectDoesNotExist

import sys
import datetime

# Employee APIs
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAssignedShifts(request, *args, **kwargs):
    shifts = Shift.objects.filter(employee=request.user)
    serializer = ShiftSerializer(shifts, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetRequestedTimeOff(request, *args, **kwargs):
    requested_time_off = RequestedTimeOff.objects.filter(employee=request.user)
    serializer = RequestedTimeOffSerializer(requested_time_off, many = True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAvailability(request, *args, **kwargs):
    availability = Availability.objects.filter(employee=request.user)
    serializer = AvailabilitySerializer(availability,many = True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def RequestShift(request, *args, **kwargs):
    # Serialize the received data
    shift_request = ShiftRequestSerializer(data=request.data)
    # Check if the data matches the serializer's format
    shift_request.is_valid(raise_exception=True)
    # Check if there are other shift request for this shift by this user
    employee = EmployeeRole.objects.get(user=request.user)
    is_zero = ShiftRequest.objects.filter(shift=request.data['shift'], employee=employee).count()
    if is_zero > 0:
        message = "User already sent request" # For debugging
        return Response(data=message, status=status.HTTP_208_ALREADY_REPORTED)
    shift_request.save()
    return Response(status=status.HTTP_201_CREATED)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetOpenShifts(request, *args, **kwargs):
    """
    Retrieves open and dropped shifts from the server
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    shifts = Shift.objects.filter(Q(company=company, is_open=True) | Q(company=company, is_dropped=True))
    serializer = ShiftSerializer(shifts, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def SendTimeOffRequest(request, *args, **kwargs):
    """
    Creates time off request
    """
    serializer = RequestedTimeOffSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    serializer.save()
    return Response(status=status.HTTP_201_CREATED)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def ToggleDropShift(request, *args, **kwargs):
    """
    Toggles marking shift as dropped
    """
    try:
        employee = EmployeeRole.objects.get(user=request.user)
        shift = Shift.objects.get(id=request.data['id'], employee=employee)
        shift.is_dropped = not shift.is_dropped
        shift.save()
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except ObjectDoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except:
        data = {'error': 'misc error, use Postman to debug'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)

    return Response(status=status.HTTP_202_ACCEPTED)

# Manager APIs
# Make sure to use pass IsManager to permission classes to ensure the user
# accessing the API has manager permissions
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def TestManagerRole(request, *args, **kwargs):
    return Response(status=status.HTTP_200_OK)

@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetValidShifts(request, *args, **kwargs):
    """
    Gets shifts from the requested manager's company and filters the shifts
    from today onward. Shifts before the current day is not returned.
    """
    company = EmployeeRole.objects.filter(user=request.user).first().company
    shifts = Shift.objects.filter(company=company, date__gte=datetime.date.today())
    ss = ShiftSerializer(shifts, many=True)
    return Response(ss.data, status=status.HTTP_200_OK)

def IsConflictWithTimeOff(employee, date):
    time_off_requests = RequestedTimeOff.objects.filter(employee=employee, is_approved=True)
    shift_date = datetime.datetime.strptime(date, "%Y-%m-%d").date()

    for time_off in time_off_requests:
        start_date = time_off.start_date
        end_date = time_off.end_date

        if start_date <= shift_date <= end_date:
            return True
    
    return False

@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def CreateNewShift(request, *args, **kwargs):
    serializer = ShiftSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)

    employee = EmployeeRole.objects.get(user=request.data['employee'])
    date = request.data['date']
    if IsConflictWithTimeOff(employee, date):
        message = str(date)
        return Response(data=message, status=status.HTTP_409_CONFLICT)

    serializer.save()
    return Response(status=status.HTTP_201_CREATED)

@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetValidEmployees(request, *args, **kwargs):
    """
    Returns list of all employees from the company
    """
    company = EmployeeRole.objects.filter(user=request.user).first().company
    employees = EmployeeRole.objects.filter(company=company)
    serializer = EmployeeSerializer(employees, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetUnapprovedShiftRequests(request, *args, **kwargs):
    """
    Returns list of unapproved shift requests
    """
    company = EmployeeRole.objects.filter(user=request.user).first().company
    shift_requests = ShiftRequest.objects.filter(company=company, is_approved=False)
    serializer = ShiftRequestSerializer(shift_requests, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def ApproveShiftRequest(request, *args, **kwargs):
    """
    Updates shift request and removes all other requests to the same shift
    Only to be used to approve shift requests
    """
    try:
        shift_request = ShiftRequest.objects.get(id=request.data['id'])
        shift_request.is_approved = True
        shift_request.save()
        shift = Shift.objects.get(id=shift_request.shift.id)
        employee = EmployeeRole.objects.get(id=shift_request.employee.id)
        shift.employee = employee
        shift.is_open = False
        shift.is_dropped = False
        shift.save()
        to_remove = ShiftRequest.objects.filter(is_approved=False, shift=shift)
        to_remove.delete()
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except ObjectDoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except:
        data = {'error': 'misc error, use Postman to debug'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    
    # Everything went alright :)
    return Response(status=status.HTTP_200_OK)

@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetUnapprovedTimeOff(request, *args, **kwargs):
    company = EmployeeRole.objects.get(user=request.user).company
    time_off_requests = RequestedTimeOff.objects.filter(company=company, is_approved=False)
    serializer = RequestedTimeOffSerializer(time_off_requests, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def ApproveTimeOff(request, *args, **kwargs):
    try:
        time_off = RequestedTimeOff.objects.get(id=request.data['id'])
        time_off.is_approved = True
        time_off.save()
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except ObjectDoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except:
        data = {'error': 'misc error, use Postman to debug'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    
    return Response(status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def UpdateShift(request, *args, **kwargs):
    try:
        shift = Shift.objects.get(id=request.data['id'])
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except Shift.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)
    
    serializer = ShiftSerializer(shift, data=request.data)
    serializer.is_valid(raise_exception=True)
    serializer.save()
    return Response(status=status.HTTP_202_ACCEPTED)