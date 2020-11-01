from django.urls import path, include

from . import views

urlpatterns = [
    path('register', views.UserCreate.as_view(), name='api_register'),
    path('get_shifts', views.GetAssignedShifts, name='api_shifts'),
    path('get_requested_time_off', views.GetRequestedTimeOff, name='api_requested_time_off'),
    path('', include('djoser.urls')),
    path('', include('djoser.urls.authtoken')),
]