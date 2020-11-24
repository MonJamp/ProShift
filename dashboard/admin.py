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

from django.contrib import admin

from .models import *

# Register your models here.

class EmployeeStatusInline(admin.StackedInline):
    model = EmployeeRole
    can_delete = False
    verbose_name_plural = 'Employement'
    fk_name = 'user'
    extra = 0
    fields = ('get_id', 'company', 'position')
    readonly_fields = ('get_id', )

class UserAdmin(admin.ModelAdmin):
    list_display = ('email', 'username', 'first_name', 'last_name', 'phone', 'id')
    inlines = (EmployeeStatusInline, )

    def get_inline_instances(self, request, obj=None):
        if not obj:
            return list()
        return super(UserAdmin, self).get_inline_instances(request, obj)

class PositionAdmin(admin.ModelAdmin):
    list_display = ('name', 'company', 'is_manager', 'id')

class EmployeeRoleAdmin(admin.ModelAdmin):
    list_display = ('user', 'company', 'position', 'id')

class ShiftRequestAdmin(admin.ModelAdmin):
    list_display = ('employee', 'company', 'shift', 'is_approved', 'id')

class CompanyAdmin(admin.ModelAdmin):
    list_display = ('name', 'id')

class RequestedTimeOffAdmin(admin.ModelAdmin):
    list_display = ('employee', 'company', 'is_approved', 'start_date', 'end_date', 'time_start', 'time_end', 'id')

class ShiftAdmin(admin.ModelAdmin):
    list_display = ('employee', 'company', 'is_open', 'is_dropped', 'date', 'time_start', 'time_end', 'id')

class CompanyCodeAdmin(admin.ModelAdmin):
    list_display = ('code', 'company', 'position', 'email')

admin.site.register(User, UserAdmin)
admin.site.register(Shift, ShiftAdmin)
admin.site.register(Company, CompanyAdmin)
admin.site.register(Position, PositionAdmin)
admin.site.register(RequestedTimeOff, RequestedTimeOffAdmin)
admin.site.register(ShiftRequest, ShiftRequestAdmin)
admin.site.register(CompanyCode, CompanyCodeAdmin)
