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

from django.db import models
from django.db.models.signals import post_save

from django.contrib.auth.models import AbstractUser
from django.dispatch import receiver

from smart_selects.db_fields import ChainedForeignKey

# Create your models here.

class User(AbstractUser):
    email = models.EmailField(verbose_name='email', max_length=48, unique=True)
    phone = models.CharField(null=True, blank=True, max_length=14)
    REQUIRED_FIELDS = ['username', 'first_name', 'last_name', 'phone']
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
    
    class Meta:
        verbose_name_plural = 'companies'

class Position(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    name = models.CharField(max_length=32)
    is_manager = models.BooleanField()

    REQUIRED_FIELDS = ['company', 'name', 'is_manager']

    def __str__(self):
        return self.name

class EmployeeRole(models.Model):
    user = models.OneToOneField(User, on_delete=models.SET_NULL, null=True)
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
    
    def get_id(self):
        return self.id

@receiver(post_save, sender=User)
def create_or_update_user_profile(sender, instance, created, **kwargs):
    if created:
        company = Company.objects.get(name='None')
        EmployeeRole.objects.create(user=instance, company=company)


@receiver(post_save, sender=Company)
def create_default_company_positions(sender, instance, created, **kwargs):
    """
    Create a manager and employee position when a company is created
    """
    if created:
        Position.objects.create(company=instance, name='Manager', is_manager=True)
        Position.objects.create(company=instance, name='Employee', is_manager=False)

@receiver(post_save, sender=EmployeeRole)
def create_default_employee_position(sender, instance, created, **kwargs):
    """
    Set the default position for an employee
    """
    if instance.position == None:
        position = Position.objects.get(company=instance.company, name='Employee')
        instance.position = position
        instance.save()

class Shift(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    employee = ChainedForeignKey(EmployeeRole, chained_field='company', chained_model_field='company', null=True, blank=True)
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
    is_denied = models.BooleanField(default=False)
    start_date = models.DateField()
    end_date =  models.DateField()
    time_start = models.TimeField()
    time_end = models.TimeField()

    REQUIRED_FIELDS = ['start_date', 'end_date', 'time_start', 'time_end']

    def __str__(self):
        return(str(self.start_date) + " - " + str(self.end_date) + " | " + str(self.time_start) + " - " + str(self.time_end) + " | " + self.employee.__str__())

class ShiftRequest(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    employee = ChainedForeignKey(EmployeeRole, chained_field='company', chained_model_field='company')
    shift = ChainedForeignKey(Shift, chained_field='company', chained_model_field='company')
    is_approved = models.BooleanField(default = False)
    is_denied = models.BooleanField(default = False)

    REQUIRED_FIELDS = ['company', 'employee', 'shift']

    def __str__(self):
        return str(self.employee) + " | Shift ID: " + str(self.shift.id)

class CompanyCode(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    position = ChainedForeignKey(
        Position,
        chained_field="company",
        chained_model_field="company",
        on_delete=models.SET_NULL,
        null=True,
        blank=True)
    code = models.IntegerField()
    email = models.EmailField()

    REQUIRED_FIELDS = ['email', ]

    def __str__(self):
        return str(self.email)
