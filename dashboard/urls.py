from django.urls import path

from . import views

urlpatterns = [
    path('', views.home_view, name='d-home'),

    path('login/', views.login_view, name='d-login'),
    path('register/', views.register_view, name='d-register'),
    path('logout/', views.logout_view, name='d-logout')
]