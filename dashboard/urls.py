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

from django.urls import path

from . import views

urlpatterns = [
    path('', views.home_view, name='d-home'),
    path('create_shift/', views.create_shift_view, name='d-create_shift'),

    path('login/', views.login_view, name='d-login'),
    path('register/', views.register_view, name='d-register'),
    path('logout/', views.logout_view, name='d-logout')
]