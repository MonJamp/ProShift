from django.contrib import admin

from .models import *

# Register your models here.

class UserAdmin(admin.ModelAdmin):
    list_display = ('email', 'username', 'first_name', 'last_name', 'company_code', 'phone')

admin.site.register(User, UserAdmin)
admin.site.register(Shift)