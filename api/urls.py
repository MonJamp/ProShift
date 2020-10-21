from django.urls import path

from . import views

urlpatterns = [
    path('register', views.UserCreate.as_view(), name='register')
]