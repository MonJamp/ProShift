from django.urls import path, include, re_path

from rest_framework import permissions
from drf_yasg.views import get_schema_view
from drf_yasg import openapi

from . import views

schema_view = get_schema_view(
   openapi.Info(
      title="ProShift API",
      default_version='v1',
      description="Documentation of ProShift APIs",
   ),
   public=True,
   permission_classes=(permissions.AllowAny,),
)

# Place employee related APIs here
employee_urls = [
    path('get_shifts', views.GetAssignedShifts, name='api_shifts'),
    path('get_requested_time_off', views.GetRequestedTimeOff, name='api_requested_time_off'),
    path('get_availability', views.GetAvailability, name='api_get_availability'),
    path('shift_request', views.RequestShift, name='api_request_shift'),
    path('get_open_shifts', views.GetOpenShifts, name='api_open_shifts'),
    path('request_time_off', views.SendTimeOffRequest, name='api_request_time_off'),
    path('shift_request2', views.RequestShift2, name='api_request_shift2'),
    path('toggle_drop_shift', views.ToggleDropShift, name='api_toggle_drop_shift'),
    path('reedem_code', views.RedeemCode, name='api_redeem_code'),
    path('get_shift_requests', views.GetShiftRequests, name='api_get_shift_requests'),
]

# Place manager related APIs here
manager_urls = [
    path('test', views.TestManagerRole, name='api_test_manager'),
    path('get_all_shifts', views.GetValidShifts, name='api_all_shifts'),
    path('create_shift', views.CreateNewShift, name='api_create_shift'),
    path('get_employees', views.GetValidEmployees, name='api_get_employees'),
    path('get_shift_requests', views.GetUnapprovedShiftRequests, name='api_get_shift_requests'),
    path('approve_shift_request', views.ApproveShiftRequest, name='api_approve_shift_request'),
    path('deny_shift_request', views.DenyShiftRequest, name='api_deny_shift_request'),
    path('get_time_off_requests', views.GetUnapprovedTimeOff, name='api_time_off_requests'),
    path('approve_time_off', views.ApproveTimeOff, name='api_approve_time_off'),
    path('deny_time_off', views.DenyTimeOff, name='api_deny_time_off'),
    path('update_shift', views.UpdateShift, name='api_update_shift'),
    path('generate_code', views.GenerateCode, name='api_generate_code'),
    path('get_codes',views.GetListOfCodes, name='api_get_codes'),
    path('get_positions',views.GetPositionsFromCompany, name='api_get_positions'),
    path('get_shift', views.GetShift, name='api_get_shift'),
]

urlpatterns = [
    path('employee/', include(employee_urls)),
    path('manager/', include(manager_urls)),

    # djoser
    path('', include('djoser.urls')),
    path('', include('djoser.urls.authtoken')),

    # swagger
    re_path(r'^swagger(?P<format>\.json|\.yaml)$', schema_view.without_ui(cache_timeout=0), name='schema-json'),
    re_path(r'^swagger/$', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    re_path(r'^redoc/$', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
]