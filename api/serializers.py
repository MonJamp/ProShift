from djoser.serializers import UserCreateSerializer
from rest_framework import serializers

from dashboard.models import *


class UserCreateSerializer(UserCreateSerializer):
    class Meta(UserCreateSerializer.Meta):
        model = User
        fields = ('id', 'username', 'email', 'password', 'first_name', 'last_name', 'phone')

class ShiftSerializer(serializers.ModelSerializer):
    company = serializers.PrimaryKeyRelatedField(read_only=True)
    employee_name = serializers.SerializerMethodField(read_only=True)
    company_name = serializers.SerializerMethodField(read_only=True)
    position = serializers.SerializerMethodField(read_only=True)
    is_open = serializers.BooleanField(default=False)
    is_dropped = serializers.BooleanField(read_only=True)

    def get_employee_name(self, obj):
        if obj.employee == None:
            return str('None')
        return str(obj.employee.user)
    
    def get_company_name(self, obj):
        return str(obj.company)

    def get_position(self, obj):
        if obj.employee == None:
            return str('None')
        return str(obj.employee.position)

    class Meta:
        model = Shift
        fields = ('id', 'company', 'company_name', 'employee', 'employee_name', 'position', 'is_open', 'is_dropped', 'date', 'time_start', 'time_end')

class ShiftSerializerWithID(serializers.ModelSerializer):
    shift_id = serializers.IntegerField(write_only=True)
    company = serializers.PrimaryKeyRelatedField(read_only=True)
    is_open = serializers.BooleanField(default=False)
    is_dropped = serializers.BooleanField(read_only=True)

    class Meta:
        model = Shift
        fields = ('id', 'shift_id', 'company', 'employee', 'is_open', 'is_dropped', 'date', 'time_start', 'time_end')

class RequestedTimeOffSerializer(serializers.ModelSerializer):
    employee = serializers.PrimaryKeyRelatedField(read_only=True)
    company = serializers.PrimaryKeyRelatedField(read_only=True)
    company_name = serializers.SerializerMethodField(read_only=True)
    is_approved = serializers.BooleanField(read_only=True)
    is_denied = serializers.BooleanField(read_only=True)
    employee_name = serializers.SerializerMethodField(read_only=True)

    def get_company_name(self, obj):
        return str(obj.company)

    def get_employee_name(self,obj):
        return str(obj.employee.user)

    class Meta:
        model = RequestedTimeOff
        fields = ('id', 'company', 'company_name', 'employee', 'employee_name', 'is_approved', 'is_denied', 'start_date', 'end_date', 'time_start', 'time_end')

class CompanySerializer(serializers.ModelSerializer):
    class Meta:
        model = Company
        fields = ('id', 'name',)

class EmployeeSerializer(serializers.ModelSerializer):
    employee_name = serializers.SerializerMethodField(read_only=True)
    position_name = serializers.SerializerMethodField(read_only=True)

    def get_employee_name(self, obj):
        return str(obj.user)

    def get_position_name(self, obj):
        return str(obj.position)
    
    class Meta:
        model = EmployeeRole
        fields = ('id', 'employee_name', 'position', 'position_name')

class ShiftRequestSerializer(serializers.ModelSerializer):
    company_name = serializers.SerializerMethodField(read_only=True)
    employee_name = serializers.SerializerMethodField(read_only=True)
    is_approved = serializers.BooleanField(read_only=True)
    is_denied = serializers.BooleanField(read_only=True)

    def get_company_name(self, obj):
        return str(obj.company)
    
    def get_employee_name(self, obj):
        return str(obj.employee.user)

    def create(self, validated_data):
        return ShiftRequest.objects.create(**validated_data)
    
    def update(self, instance, validated_data):
        instance.company = validated_data.get('company', instance.company)
        instance.employee = validated_data.get('employee', instance.employee)
        instance.shift = validated_data.get('shift', instance.shift)
        instance.is_approved = validated_data.get('is_approved', instance.is_approved)
        instance.save()
        return instance

    class Meta:
        model = ShiftRequest
        fields = ('id', 'company', 'company_name', 'employee', 'employee_name', 'shift', 'is_approved', 'is_denied')

class CompanyCodeSerializer(serializers.ModelSerializer):
    company = serializers.PrimaryKeyRelatedField(read_only=True)
    company_name = serializers.SerializerMethodField(read_only=True)
    position_name = serializers.SerializerMethodField(read_only=True)
    code = serializers.IntegerField(read_only=True)

    def get_company_name(self, obj):
        return str(obj.company)

    def get_position_name(self, obj):
        return str(obj.position)

    class Meta:
        model = CompanyCode
        fields = ('company', 'company_name', 'position', 'position_name', 'code', 'email')

class CompanyCodeEmployeeSerializer(serializers.ModelSerializer):
    company = serializers.PrimaryKeyRelatedField(read_only=True)
    position = serializers.PrimaryKeyRelatedField(read_only=True)
    email = serializers.EmailField(read_only=True)
    class Meta:
        model = CompanyCode
        fields = ('company', 'position', 'code', 'email')

class PositionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Position
        fields = ('id','name')