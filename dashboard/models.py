from django.db import models
from django.contrib.auth.models import AbstractUser
from django.contrib.auth.base_user import BaseUserManager
from django.db.models.signals import post_save
from django.dispatch import receiver
from smart_selects.db_fields import ChainedForeignKey

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

class Company(models.Model):
    name = models.CharField(max_length=32)

    REQUIRED_FIELDS = ['name']

    def __str__(self):
        return self.name

class Position(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    name = models.CharField(max_length=32)
    is_manager = models.BooleanField()

    REQUIRED_FIELDS = ['company', 'name', 'is_manager']

    def __str__(self):
        return self.name

class EmployeeRole(models.Model):
    user = models.ForeignKey(User, on_delete=models.SET_NULL, null=True)
    company = models.ForeignKey(Company, on_delete=models.SET_NULL, null=True, blank=True)
    # ChainedForeignKey allows to filter positions based on the company chosen in the form, only relevant for the admin panel
    position = ChainedForeignKey(
        Position,
        chained_field="company",
        chained_model_field="company",
        on_delete=models.SET_NULL,
        null=True,
        blank=True) #models.ForeignKey(Position, on_delete=models.SET_NULL, null=True, blank=True)

    def __str__(self):
        return str(self.user) + " | " + str(self.position)

@receiver(post_save, sender=User)
def create_or_update_user_profile(sender, instance, created, **kwargs):
    if created:
        EmployeeRole.objects.create(user=instance)

@receiver(post_save, sender=Company)
def create_default_company_positions(sender, instance, created, **kwargs):
    """
    Create a manager and employee position when a company is created
    """
    if created:
        Position.objects.create(company=instance, name='Manager', is_manager=True)
        Position.objects.create(company=instance, name='Employee', is_manager=False)

class Shift(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    employee = ChainedForeignKey(EmployeeRole, chained_field='company', chained_model_field='company')
    is_open = models.BooleanField(default=False)
    is_dropped = models.BooleanField(default=False)
    date = models.DateField()
    time_start = models.TimeField()
    time_end = models.TimeField()

    REQUIRED_FIELDS = ['date', 'time_start', 'time_end']

    def __str__(self):
        return ( str(self.date) + " | " + str(self.time_start) + " - " + str(self.time_end) + " | " + self.employee.__str__())

class RequestedTimeOff(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    employee = ChainedForeignKey(EmployeeRole, chained_field='company', chained_model_field='company')
    is_approved = models.BooleanField(default=False)
    start_date = models.DateField()
    end_date =  models.DateField()
    time_start = models.TimeField()
    time_end = models.TimeField()

    REQUIRED_FIELDS = ['start_date', 'end_date', 'time_start', 'time_end']

    def __str__(self):
        return(str(self.start_date) + " - " + str(self.end_date) + " | " + str(self.time_start) + " - " + str(self.time_end) + " | " + self.employee.__str__())


class Availability(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    employee = ChainedForeignKey(EmployeeRole, chained_field='company', chained_model_field='company')
    #employee = models.ForeignKey(User,on_delete=models.CASCADE, null=True, blank=True)
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