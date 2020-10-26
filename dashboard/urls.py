from django.urls import path

from . import views

urlpatterns = [
    path('', views.home_view, name='d-home'),
    path('create_shift/', views.create_shift_view, name='d-create_shift'),

    path('login/', views.login_view, name='d-login'),
    path('register/', views.register_view, name='d-register'),
    path('logout/', views.logout_view, name='d-logout')
]