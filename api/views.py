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

class UserCreate(APIView):
    def post(self, request, format='json'):
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            if user:
                token = Token.objects.create(user=user)
                json = serializer.data
                json['token'] = token.key
                return Response(json, status=status.HTTP_201_CREATED)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

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

#This one works now
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def GetAvailability(request, *args, **kwargs):
    availability = Availability.objects.filter(employee=request.user)
    serializer = AvailabilitySerializer(availability,many = True)
    return Response(serializer.data, status=status.HTTP_200_OK)
    
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsManager])
def TestManagerRole(request, *args, **kwargs):
    return Response(status=status.HTTP_200_OK)
