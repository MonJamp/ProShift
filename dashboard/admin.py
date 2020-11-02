from django.contrib import admin

from .models import *

# Register your models here.

class UserAdmin(admin.ModelAdmin):
    list_display = ('email', 'username', 'first_name', 'last_name', 'company_code', 'phone')


class AvailabilityAdmin(admin.ModelAdmin):
    list_display = ('employee' , 'company' , 'start_date', 'is_approved', 'is_current')


admin.site.register(User, UserAdmin)
admin.site.register(Shift)
admin.site.register(RequestedTimeOff)
admin.site.register(Availability, AvailabilityAdmin)
