from rest_framework.authtoken.models import Token

from rest_framework.views import APIView

from django.contrib.auth.models import User
from dashboard.models import Shift, RequestedTimeOff, Availability

from .serializers import *

from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework import status
from rest_framework.response import Response
from django.http import HttpResponse, JsonResponse
from .permissions import IsManager

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

@api_view(['POST'])
@permission_classes([IsAuthenticated, IsManager])
def CreateNewShift(request, *args, **kwargs):
    serializer = ShiftSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    serializer.save()
    return Response(status=status.HTTP_201_CREATED)