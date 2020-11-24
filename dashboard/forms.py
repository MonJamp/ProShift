from django.forms import ModelForm
from django.contrib.auth.forms import UserCreationForm

from .models import User, Shift

class RegistrationForm(UserCreationForm):
    class Meta:
        model = User
        fields = ['username', 'email', 'first_name', 'last_name', 'phone', 'password1', 'password2']

class NewShiftForm(ModelForm):
    class Meta:
        model = Shift
        fields = ['employee', 'is_open', 'date', 'time_start', 'time_end']