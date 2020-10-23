from django.shortcuts import render, redirect

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required

from django.contrib import messages

from .forms import RegistrationForm


def login_view(request):
    if request.user.is_authenticated:
        return redirect('d-home')

    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')

        user = authenticate(request, username=username, password=password)

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
            user = form.cleaned_data.get('username')
            messages.success(request, 'Account was created for ' + user)
            return redirect('d-login')

    context = {'form': form}
    return render(request, 'register.html', context)

def logout_view(request):
    logout(request)
    return redirect('d-login')

@login_required(login_url='d-login')
def home_view(request):
    return render(request, 'home.html')