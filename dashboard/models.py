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