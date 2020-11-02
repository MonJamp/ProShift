from rest_framework.authtoken.models import Token

from rest_framework.views import APIView

from django.contrib.auth.models import User
from dashboard.models import Shift, Requested_Time_Off

from .serializers import *

from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework import status
from rest_framework.response import Response
from django.http import HttpResponse, JsonResponse

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
    requested_time_off = Requested_Time_Off.objects.filter(employee=request.user)
    serializer = Requested_Time_OffSerializer(requested_time_off)
    return Response(serializer.data, status=status.HTTP_200_OK)
