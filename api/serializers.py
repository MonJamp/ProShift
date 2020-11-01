from djoser.serializers import UserCreateSerializer, UserSerializer
from rest_framework import serializers
from rest_framework.validators import UniqueValidator

from dashboard.models import User, Shift, Requested_Time_Off


class UserCreateSerializer(UserCreateSerializer):
    class Meta(UserCreateSerializer.Meta):
        model = User
        fields = ('id', 'username', 'email', 'password', 'first_name', 'last_name', 'phone', 'company_code')

class ShiftSerializer(serializers.ModelSerializer):
    class Meta:
        model = Shift
        fields = ('employee', 'is_open', 'is_dropped', 'date', 'time_start', 'time_end')

class Requested_Time_OffSerializer(serializers.ModelSerializer):
    class Meta:
        model = Requested_Time_Off
        fields = ('company', 'employee', 'is_approved', 'start_date', 'end_date', 'start_time', 'end_time')