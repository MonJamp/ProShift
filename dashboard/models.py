from django.db import models
from django.contrib.auth.models import AbstractUser
from django.contrib.auth.base_user import BaseUserManager

# Create your models here.

class User(AbstractUser):
    email = models.EmailField(verbose_name='email', max_length=48, unique=True)
    phone = models.CharField(null=True, blank=True, max_length=14)
    company_code = models.CharField(null=True, blank=True, max_length=8)
    REQUIRED_FIELDS = ['username', 'first_name', 'last_name', 'phone', 'company_code']
    USERNAME_FIELD = 'email'

    def get_username(self):
        return self.email
    
    def __str__(self):
        return str(self.first_name) + " " + str(self.last_name)

class Shift(models.Model):
    employee = models.ForeignKey(User, on_delete=models.CASCADE, null=True, blank=True)
    is_open = models.BooleanField(default=False)
    is_dropped = models.BooleanField(default=False)
    date = models.DateField()
    time_start = models.TimeField()
    time_end = models.TimeField()

    REQUIRED_FIELDS = ['data', 'time_start', 'time_end']

    def __str__(self):
        return ( str(self.date) + " | " + str(self.time_start) + " - " + str(self.time_end) + " | " + self.employee.__str__())

class RequestedTimeOff(models.Model):
    company = models.CharField(null=True, blank=True, max_length=60)
    employee = models.ForeignKey(User,on_delete=models.CASCADE, null=True, blank=True)
    is_approved = models.BooleanField(default=False)
    start_date = models.DateField()
    end_date =  models.DateField()
    time_start = models.TimeField()
    time_end = models.TimeField()

    REQUIRED_FIELDS = ['start_date', 'end_date', 'time_start', 'time_end']

    def __str__(self):
        return(str(self.start_date) + " - " + str(self.end_date) + " | " + str(self.time_start) + " - " + str(self.time_end) + " | " + self.employee.__str__())


class Availability(models.Model):
    company = models.CharField(null=True, blank=True, max_length=60)
    employee = models.ForeignKey(User,on_delete=models.CASCADE, null=True, blank=True)
    start_date = models.DateField()
    is_approved = models.BooleanField(default=False)
    is_current = models.BooleanField(default=False)
    mon_earliest = models.TimeField()
    mon_latest = models.TimeField()
    tues_earliest = models.TimeField()
    tues_latest = models.TimeField()
    wed_earliest = models.TimeField()
    wed_latest = models.TimeField()
    thur_earliest = models.TimeField()
    thur_latest = models.TimeField()
    fri_earliest = models.TimeField()
    fri_latest = models.TimeField()
    sat_earliest = models.TimeField()
    sat_latest = models.TimeField()
    sun_earliest = models.TimeField()
    sun_latest = models.TimeField()

    def __str__(self):
        return (str(self.employee) + " | " + str(self.start_date))