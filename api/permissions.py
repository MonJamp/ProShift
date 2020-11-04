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
