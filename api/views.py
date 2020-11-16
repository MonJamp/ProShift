from rest_framework.authtoken.models import Token
from rest_framework.status import HTTP_401_UNAUTHORIZED, HTTP_403_FORBIDDEN

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

from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

import sys
import datetime
import hashlib

# JSON request body with only an entry for a key of 'id'
id_body = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='primary key of entry')
    }
)

# Employee APIs
@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAssignedShifts(request, *args, **kwargs):
    """
    Retrieve assigned shifts for the current user
    """
    employee = EmployeeRole.objects.get(user=request.user)
    shifts = Shift.objects.filter(employee=employee)
    serializer = ShiftSerializer(shifts, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: RequestedTimeOffSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetRequestedTimeOff(request, *args, **kwargs):
    """
    Retrieve time off requests made by user
    """
    employee = EmployeeRole.objects.get(user=request.user)
    requested_time_off = RequestedTimeOff.objects.filter(employee=employee)
    serializer = RequestedTimeOffSerializer(requested_time_off, many = True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: AvailabilitySerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAvailability(request, *args, **kwargs):
    """
    Retrieve availability requests (approved or not) for user
    """
    employee = EmployeeRole.objects.get(user=request.user)
    availability = Availability.objects.filter(employee=employee)
    serializer = AvailabilitySerializer(availability,many = True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=ShiftRequestSerializer, responses={200: ShiftRequestSerializer, 208: 'Shift request already exists'})
@api_view(['POST'])
@permission_classes([IsAuthenticated])
def RequestShift(request, *args, **kwargs):
    """
    DEPRECATED!!
    Create a shift request for the user. Only a single shift request can be sent
    per user per shift
    """
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
    return Response(shift_request, status=status.HTTP_201_CREATED)

@swagger_auto_schema(method='post', request_body=id_body, responses={201: ShiftRequestSerializer, 208: 'Shift request already exists'})
@api_view(['POST'])
@permission_classes([IsAuthenticated])
def RequestShift2(request, *args, **kwargs):
    """
    Create a shift request for the user. Only a single shift request can be sent
    per user per shift. This API only needs shift id
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    shift = Shift.objects.get(id=request.data['id'])
    is_zero = ShiftRequest.objects.filter(shift=shift, employee=employee).count()
    if is_zero > 0:
        return Response(status=status.HTTP_208_ALREADY_REPORTED)
    shift_request = ShiftRequest.objects.create(company=company, employee=employee, shift=shift, is_approved=False)
    serializer = ShiftRequestSerializer(shift_request)
    return Response(serializer.data, status=status.HTTP_201_CREATED)


@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetOpenShifts(request, *args, **kwargs):
    """
    Retrieves open and dropped shifts from the user's company
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    shifts = Shift.objects.filter(Q(company=company, is_open=True) | Q(company=company, is_dropped=True))
    serializer = ShiftSerializer(shifts, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=RequestedTimeOffSerializer)
@api_view(['POST'])
@permission_classes([IsAuthenticated])
def SendTimeOffRequest(request, *args, **kwargs):
    """
    Creates time off request
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    serializer = RequestedTimeOffSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    serializer.save(employee=employee, company=company)
    return Response(serializer.data, status=status.HTTP_201_CREATED)

@swagger_auto_schema(method='post', request_body=id_body, responses={202: ShiftSerializer, 404: 'Entry with id does not exist'})
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
        return Response(data=data, status=status.HTTP_404_BAD_REQUEST)

    serializer = ShiftSerializer(shift)
    return Response(serializer.data, status=status.HTTP_202_ACCEPTED)

@swagger_auto_schema(method='post', request_body=CompanyCodeEmployeeSerializer,
    responses={202: "User code is good", 404: "Code not valid", 403: "Unauthorized"})
@api_view(['POST'])
@permission_classes([IsAuthenticated])
def RedeemCode(request, *args, **kwargs):
    """
    Use code to join a company
    """
    serializer = CompanyCodeEmployeeSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    code = serializer.validated_data['code']
    try:
        company_code = CompanyCode.objects.get(code=code)
    except ObjectDoesNotExist as e:
        return Response(status=status.HTTP_404_NOT_FOUND)
    if company_code.email == request.user.email:
        employee = EmployeeRole.objects.get(user=request.user)
        employee.company = company_code.company
        employee.save()
        company_code.delete()
        return Response(status=status.HTTP_202_ACCEPTED)
    else:
        return Response(status=HTTP_401_UNAUTHORIZED)

# Manager APIs
# Make sure to use pass IsManager to permission classes to ensure the user
# accessing the API has manager permissions
@swagger_auto_schema(method='get', responses={200: 'User is a manager'})
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def TestManagerRole(request, *args, **kwargs):
    """
    Tests if the current user is a manager
    """
    return Response(status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
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

@swagger_auto_schema(method='post', request_body=ShiftSerializer, responses={201: ShiftSerializer, 409: 'Conflict with time off request'})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def CreateNewShift(request, *args, **kwargs):
    """
    Creates a shift for the selected employee. Shifts scheduled during time
    off are automatically declined by the server
    """
    serializer = ShiftSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)

    employee = EmployeeRole.objects.get(user=request.data['employee'])
    date = request.data['date']
    if IsConflictWithTimeOff(employee, date):
        message = str(date)
        return Response(data=message, status=status.HTTP_409_CONFLICT)

    serializer.save(company=employee.company)
    return Response(serializer.data, status=status.HTTP_201_CREATED)

@swagger_auto_schema(method='get', responses={200: EmployeeSerializer(many=True)})
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

@swagger_auto_schema(method='get', responses={200: ShiftRequestSerializer})
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

@swagger_auto_schema(method='post', request_body=id_body, responses={200: ShiftSerializer, 404: 'shift or shift request not found'})
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
        return Response(data=data, status=status.HTTP_404_NOT_FOUND)
    
    updated_shift = ShiftSerializer(shift)
    # Everything went alright :)
    return Response(updated_shift.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: RequestedTimeOffSerializer})
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetUnapprovedTimeOff(request, *args, **kwargs):
    """
    Retrieves unapproved time off requests for the company
    """
    company = EmployeeRole.objects.get(user=request.user).company
    time_off_requests = RequestedTimeOff.objects.filter(company=company, is_approved=False)
    serializer = RequestedTimeOffSerializer(time_off_requests, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=id_body, responses={200: RequestedTimeOffSerializer, 404: 'Time off request not found'})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def ApproveTimeOff(request, *args, **kwargs):
    """
    Approve a time off request by id
    """
    try:
        time_off = RequestedTimeOff.objects.get(id=request.data['id'])
        time_off.is_approved = True
        time_off.save()
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except ObjectDoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_404_NOT_FOUND)
    
    updated_time_off = RequestedTimeOffSerializer(time_off)
    return Response(updated_time_off.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=id_body, responses={202: ShiftSerializer, 404: 'Shift not found'})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def UpdateShift(request, *args, **kwargs):
    """
    Update any field for an already created shift
    """
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
    return Response(serializer.data, status=status.HTTP_202_ACCEPTED)

@swagger_auto_schema(method='post', request_body=CompanyCodeSerializer, responses={201: CompanyCodeSerializer, 208: "Company code already generated for email"})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def GenerateCode(request, *args, **kwargs):
    """
    Generates a company code which correlates to a given email
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    serializer = CompanyCodeSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    email = serializer.validated_data['email']

    is_zero = CompanyCode.objects.filter(email=email).count()
    if is_zero > 0:
        return Response(status=status.HTTP_208_ALREADY_REPORTED)

    code = int(hashlib.sha1(email.encode('utf-8')).hexdigest(), 16) % (10**8)
    serializer.save(company=company, code=code)
    return Response(serializer.data, status=status.HTTP_201_CREATED)

@swagger_auto_schema(method='get', responses={200: CompanyCodeSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetListOfCodes(request, *args, **kwargs):
    """
    Returns list of codes which haven't been used
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    codes = CompanyCode.objects.filter(company=company)
    serializer = CompanyCodeSerializer(codes, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)
