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

from rest_framework import permissions
from dashboard.models import EmployeeRole

class IsManager(permissions.BasePermission):
    """
    Checks if the user has manager role permissions
    """

    message = 'User does not have manager permission'

    def has_permission(self, request, view):
        if request.user.is_anonymous == True:
            return False
        
        roles = EmployeeRole.objects.filter(user=request.user)

        for role in roles:
            if role.position.is_manager == True:
                return True
        
        return False
