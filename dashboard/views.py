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

from django.shortcuts import render, redirect

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required

from django.contrib import messages

from .forms import RegistrationForm, NewShiftForm
from .models import *


def login_view(request):
    if request.user.is_authenticated:
        return redirect('d-home')

    if request.method == 'POST':
        email = request.POST.get('email')
        password = request.POST.get('password')

        user = authenticate(request, email=email, password=password)

        if user is not None:
            login(request, user)
            return redirect('d-home')
        else:
            messages.info(request, 'Username or password is incorrect')

    context = {}
    return render(request, 'login.html', context)

def register_view(request):
    if request.user.is_authenticated:
        return redirect('d-home')
    
    form = RegistrationForm()
    if request.method == 'POST':
        form = RegistrationForm(request.POST)
        if form.is_valid():
            form.save()
            name = form.cleaned_data.get('first_name')
            messages.success(request, 'Account was created for ' + name)
            return redirect('d-login')

    context = {'form': form}
    return render(request, 'register.html', context)

def logout_view(request):
    logout(request)
    return redirect('d-login')

@login_required(login_url='d-login')
def home_view(request):
    employee = EmployeeRole(user=request.user)
    shifts = Shift.objects.filter(employee=employee)

    context = {'shifts': shifts}
    return render(request, 'home.html', context)

@login_required(login_url='d-login')
def create_shift_view(request):
    form = NewShiftForm()
    if request.method == 'POST':
        form = NewShiftForm(request.POST)
        if form.is_valid():
            form.save()
    
    context = {'form': form}
    return render(request, 'create_shift.html', context)