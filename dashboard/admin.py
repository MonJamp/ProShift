from django.contrib import admin

from .models import *

# Register your models here.

class EmployeeInline(admin.StackedInline):
    model = Employee
    can_delete = False
    verbose_name_plural = 'Employee'
    fk_name = 'user'
    extra = 0

class UserAdmin(admin.ModelAdmin):
    list_display = ('email', 'username', 'first_name', 'last_name', 'company_code', 'phone')


class AvailabilityAdmin(admin.ModelAdmin):
    list_display = ('employee' , 'company' , 'start_date', 'is_approved', 'is_current')
    inlines = (EmployeeInline, )

    def get_inline_instances(self, request, obj=None):
        if not obj:
            return list()
        return super(UserAdmin, self).get_inline_instances(request, obj)

class PositionAdmin(admin.ModelAdmin):
    list_display = ('name', 'company', 'is_manager')

class EmployeeAdmin(admin.ModelAdmin):
    list_display = ('user', 'company', 'position')

admin.site.register(User, UserAdmin)
admin.site.register(Shift)
admin.site.register(Company)
admin.site.register(Position, PositionAdmin)
admin.site.register(Employee, EmployeeAdmin)
admin.site.register(RequestedTimeOff)
admin.site.register(Availability, AvailabilityAdmin)
