from django.urls import path, include

from . import views

# Place employee related APIs here
employee_urls = [
    path('get_shifts', views.GetAssignedShifts, name='api_shifts'),
    path('get_requested_time_off', views.GetRequestedTimeOff, name='api_requested_time_off'),
    path('get_availability', views.GetAvailability, name='api_get_availability'),
]

# Place manager related APIs here
manager_urls = [
    path('test', views.TestManagerRole, name='api_test_manager'),
    path('get_all_shifts', views.GetValidShifts, name='api_all_shifts'),
    path('create_shift', views.CreateNewShift, name='api_create_shift'),
]

urlpatterns = [
    path('employee/', include(employee_urls)),
    path('manager/', include(manager_urls)),

    path('', include('djoser.urls')),
    path('', include('djoser.urls.authtoken')),
]