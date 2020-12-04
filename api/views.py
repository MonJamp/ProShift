# Copyright 2020 ProShift Team
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

import datetime
import hashlib

# Decorators
from rest_framework.decorators import api_view, permission_classes
from drf_yasg.utils import swagger_auto_schema

from drf_yasg import openapi
from rest_framework.fields import empty

# Permissions
from rest_framework.permissions import IsAuthenticated
from .permissions import IsManager

from rest_framework.response import Response
from rest_framework import status

from django.db.models import Q

from .serializers import *

# JSON request body with only an entry for a key of 'id'
id_body = openapi.Schema(
    type=openapi.TYPE_OBJECT,
    properties={
        'id': openapi.Schema(type=openapi.TYPE_INTEGER, description='primary key of entry')
    }
)

# Employee APIs
@swagger_auto_schema(method='get', responses={200: UserInfoSerializer})
@api_view(["GET"])
@permission_classes([IsAuthenticated])
def GetUserInfo(request, *args, **kwargs):
    """
    Returns id, company name, position name, and manager status of user
    """
    employee = EmployeeRole.objects.get(user=request.user)
    serializer = UserInfoSerializer(employee)
    return Response(serializer.data, status=status.HTTP_200_OK)


@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAssignedShifts(request, *args, **kwargs):
    """
    Retrieve assigned shifts for the current user. This API filters out outdated shifts
    """
    employee = EmployeeRole.objects.get(user=request.user)
    shifts = Shift.objects.filter(employee=employee, date__gte=datetime.date.today()).order_by('date')
    serializer = ShiftSerializer(shifts, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAssignedShiftsDebug(request, *args, **kwargs):
    """
    Retrieve assigned shifts for the current user
    """
    employee = EmployeeRole.objects.get(user=request.user)
    shifts = Shift.objects.filter(employee=employee).order_by('date')
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
    requested_time_off = RequestedTimeOff.objects.filter(employee=employee).order_by('start_date')
    serializer = RequestedTimeOffSerializer(requested_time_off, many = True)
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
    requests = ShiftRequest.objects.filter(shift=request.data['shift'], employee=employee)
    if not requests:
        shift_request.save()
        return Response(shift_request, status=status.HTTP_201_CREATED)
    else:
        message = "User already sent request" # For debugging
        return Response(data=message, status=status.HTTP_208_ALREADY_REPORTED)

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
    requests = ShiftRequest.objects.filter(shift=shift, employee=employee)
    if not requests:
        shift_request = ShiftRequest.objects.create(company=company, employee=employee, shift=shift, is_approved=False)
        serializer = ShiftRequestSerializer(shift_request)
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    else:
        return Response(status=status.HTTP_208_ALREADY_REPORTED)


@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetOpenShifts(request, *args, **kwargs):
    """
    Retrieves open and dropped shifts from the user's company
    Shifts requested by the user are filtered out
    """
    employee = EmployeeRole.objects.get(user=request.user)
    company = employee.company
    requested_shifts = ShiftRequest.objects.filter(employee=employee)
    shifts = Shift.objects.filter(Q(company=company, is_open=True) | Q(company=company, is_dropped=True)).order_by('date')
    # Exclude requested shifts from being returned
    for s in requested_shifts:
        shifts = shifts.exclude(id=s.shift.id)
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
    except Shift.DoesNotExist as e:
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
    except CompanyCode.DoesNotExist as e:
        return Response(status=status.HTTP_404_NOT_FOUND)
    if company_code.email == request.user.email:
        employee = EmployeeRole.objects.get(user=request.user)
        employee.company = company_code.company
        employee.position = company_code.position
        employee.save()

        cc_serializer = CompanyCodeSerializer(instance=company_code)

        company_code.delete()
        return Response(data=cc_serializer.data, status=status.HTTP_202_ACCEPTED)
    else:
        return Response(status=status.HTTP_401_UNAUTHORIZED)

@swagger_auto_schema(method='get', responses={200: ShiftRequestSerializer})
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetShiftRequests(request, *args, **kwargs):
    """
    Get shift requests made by user
    """
    employee = EmployeeRole.objects.get(user=request.user)
    requested_time_off = ShiftRequest.objects.filter(employee=employee).order_by('shift__date')
    serializer = ShiftRequestSerializer(requested_time_off, many = True)
    return Response(serializer.data, status=status.HTTP_200_OK)


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
    shifts = Shift.objects.filter(company=company, date__gte=datetime.date.today()).order_by('date')
    ss = ShiftSerializer(shifts, many=True)
    return Response(ss.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: ShiftSerializer(many=True)})
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetValidShiftsDebug(request, *args, **kwargs):
    """
    Gets shifts from the requested manager's company and filters the shifts
    from today onward. Shifts before the current day is not returned.
    """
    company = EmployeeRole.objects.filter(user=request.user).first().company
    shifts = Shift.objects.filter(company=company).order_by('date')
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
    serializer.is_valid(raise_exception=False)

    try:
        employee = EmployeeRole.objects.get(id=request.data['employee'])
    except KeyError as e:
        employee = None
    except EmployeeRole.DoesNotExist as e:
        employee = None
        manager = EmployeeRole.objects.get(user=request.user)
        serializer.save(company=manager.company, employee=None, is_open=True)
        return Response(serializer.data, status=status.HTTP_201_CREATED)

    date = request.data['date']
    if IsConflictWithTimeOff(employee, date):
        message = str(date)
        return Response(data=message, status=status.HTTP_409_CONFLICT)

    manager = EmployeeRole.objects.get(user=request.user)
    serializer.save(company=manager.company)
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
    shift_requests = ShiftRequest.objects.filter(company=company, is_approved=False, is_denied=False).order_by('shift__date')
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
    except Shift.DoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_404_NOT_FOUND)
    
    updated_shift = ShiftSerializer(shift)
    # Everything went alright :)
    return Response(updated_shift.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=id_body, responses={200: "Denied shift request", 404: 'shift or shift request not found'})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def DenyShiftRequest(request, *args, **kwargs):
    """
    Deny shift request by id
    """
    try:
        shift_request = ShiftRequest.objects.get(id=request.data['id'])
        shift_request.is_denied = True
        shift_request.save()
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except ShiftRequest.DoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_404_NOT_FOUND)
    
    # Everything went alright :)
    return Response(status=status.HTTP_200_OK)

@swagger_auto_schema(method='get', responses={200: RequestedTimeOffSerializer})
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetUnapprovedTimeOff(request, *args, **kwargs):
    """
    Retrieves unapproved time off requests for the company
    """
    company = EmployeeRole.objects.get(user=request.user).company
    time_off_requests = RequestedTimeOff.objects.filter(company=company, is_approved=False, is_denied=False).order_by('start_date')
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
    except RequestedTimeOff.DoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_404_NOT_FOUND)
    
    updated_time_off = RequestedTimeOffSerializer(time_off)
    return Response(updated_time_off.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=id_body, responses={200: RequestedTimeOffSerializer, 404: 'Time off request not found'})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def DenyTimeOff(request, *args, **kwargs):
    """
    Deny a time off request by id
    """
    try:
        time_off = RequestedTimeOff.objects.get(id=request.data['id'])
        time_off.is_denied = True
        time_off.save()
    except KeyError as e:
        data = {'id': 'field is missing'}
        return Response(data=data, status=status.HTTP_400_BAD_REQUEST)
    except RequestedTimeOff.DoesNotExist as e:
        data = {'error': str(e)}
        return Response(data=data, status=status.HTTP_404_NOT_FOUND)
    
    updated_time_off = RequestedTimeOffSerializer(time_off)
    return Response(updated_time_off.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=ShiftSerializerWithID, responses={202: ShiftSerializerWithID, 404: 'Shift not found'})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def UpdateShift(request, *args, **kwargs):
    """
    Update any field for an already created shift
    """
    serializer = ShiftSerializerWithID(data=request.data)
    serializer.is_valid(raise_exception=True)
    shift = Shift.objects.get(pk=serializer.validated_data['shift_id'])
    serializer.update(instance=shift, validated_data=serializer.validated_data)
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

    codes = CompanyCode.objects.filter(email=email)
    if not codes:
        code = int(hashlib.sha1(email.encode('utf-8')).hexdigest(), 16) % (10**8)
        serializer.save(company=company, code=code)
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    else:
        return Response(status=status.HTTP_208_ALREADY_REPORTED)

    

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

@swagger_auto_schema(method='get', responses={200: PositionSerializer})
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def GetPositionsFromCompany(request, *args, **kwargs):
    """
    Returns list of positions name and id
    """
    company = EmployeeRole.objects.get(user=request.user).company
    position = Position.objects.filter(company=company)
    serializer = PositionSerializer(position, many=True) 
    return Response(serializer.data, status=status.HTTP_200_OK)

@swagger_auto_schema(method='post', request_body=id_body, responses={200: ShiftSerializer})
@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def GetShift(request, *args, **kwargs):
    """
    Return details for specific shift by id
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
    return Response(serializer.data, status=status.HTTP_202_ACCEPTED)