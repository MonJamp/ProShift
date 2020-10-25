from django.urls import path, include

from . import views

urlpatterns = [
    path('register', views.UserCreate.as_view(), name='api_register'),
    path('get_shifts', views.GetAssignedShifts, name='api_shifts'),
    path('', include('djoser.urls')),
    path('', include('djoser.urls.authtoken')),
]