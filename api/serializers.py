from djoser.serializers import UserCreateSerializer, UserSerializer
from rest_framework import serializers
from rest_framework.validators import UniqueValidator

from dashboard.models import *


class UserCreateSerializer(UserCreateSerializer):
    class Meta(UserCreateSerializer.Meta):
        model = User
        fields = ('id', 'username', 'email', 'password', 'first_name', 'last_name', 'phone', 'company_code')

class ShiftSerializer(serializers.ModelSerializer):
    employee_name = serializers.SerializerMethodField(read_only=True)
    position = serializers.SerializerMethodField(read_only=True)

    def get_employee_name(self, obj):
        return str(obj.employee.user)
    
    def get_position(self, obj):
        return str(obj.employee.position)

    class Meta:
        model = Shift
        fields = ('employee', 'employee_name', 'position', 'is_open', 'is_dropped', 'date', 'time_start', 'time_end')

class RequestedTimeOffSerializer(serializers.ModelSerializer):
    class Meta:
        model = RequestedTimeOff
        fields = ('company', 'employee', 'is_approved', 'start_date', 'end_date', 'time_start', 'time_end')

class AvailabilitySerializer(serializers.ModelSerializer):
    class Meta:
        model = Availability
        fields = ('company', 'employee', 'is_approved', 'start_date',
            'is_current', 'mon_earliest', 'mon_latest','tues_earliest', 'tues_latest', 'wed_earliest', 'wed_latest',
            'thur_earliest', 'thur_latest', 'fri_earliest', 'fri_latest', 'sat_earliest', 'sat_latest', 'sun_earliest', 'sun_latest')

class CompanySerializer(serializers.ModelSerializer):
    class Meta:
        model = Company
        fields = ('name',)