package com.supremainc.biostar2.sdk.provider;


import com.google.gson.JsonObject;
import com.supremainc.biostar2.sdk.models.v1.permission.CloudRoles;
import com.supremainc.biostar2.sdk.models.v2.accesscontrol.AccessGroups;
import com.supremainc.biostar2.sdk.models.v2.card.Card;
import com.supremainc.biostar2.sdk.models.v2.card.Cards;
import com.supremainc.biostar2.sdk.models.v2.card.CardsList;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCardRaw;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCards;
import com.supremainc.biostar2.sdk.models.v2.card.SmartCardLayouts;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormat;
import com.supremainc.biostar2.sdk.models.v2.card.WiegandFormats;
import com.supremainc.biostar2.sdk.models.v2.common.BioStarSetting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.UpdateData;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.device.Device;
import com.supremainc.biostar2.sdk.models.v2.device.DeviceTypes;
import com.supremainc.biostar2.sdk.models.v2.device.Devices;
import com.supremainc.biostar2.sdk.models.v2.device.FingerprintVerify;
import com.supremainc.biostar2.sdk.models.v2.door.Door;
import com.supremainc.biostar2.sdk.models.v2.door.Doors;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventLogs;
import com.supremainc.biostar2.sdk.models.v2.eventlog.EventTypes;
import com.supremainc.biostar2.sdk.models.v2.eventlog.Query;
import com.supremainc.biostar2.sdk.models.v2.face.Face;
import com.supremainc.biostar2.sdk.models.v2.face.Faces;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.FingerPrints;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.ScanFingerprintTemplate;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.VerifyFingerprintOption;
import com.supremainc.biostar2.sdk.models.v2.login.Login;
import com.supremainc.biostar2.sdk.models.v2.login.NotificationToken;
import com.supremainc.biostar2.sdk.models.v2.permission.UserPermissions;
import com.supremainc.biostar2.sdk.models.v2.preferrence.Preference;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.models.v2.user.UserGroups;
import com.supremainc.biostar2.sdk.models.v2.user.Users;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    // AUTH
    @GET("{cloudVersion}/references/{subDomain}/biostar_version")
    Call<VersionData> getServerVersion(@Path("cloudVersion") String cloudVersion, @Path("subDomain") String subDomain);
    @POST("{cloudVersion}/login")
    Call<User> login(@Path("cloudVersion") String cloudVersion, @Body Login login);
    @GET("{cloudVersion}/logout")
    Call<ResponseStatus> logout(@Path("cloudVersion") String cloudVersion);

    // users
    @GET("{cloudVersion}/users/my_profile")
    Call<User> simplelogin(@Path("cloudVersion") String cloudVersion);
    @GET("{cloudVersion}/users")
    Call<Users> get_users(@Path("cloudVersion") String cloudVersion, @QueryMap Map<String, String> params);
    @POST("{cloudVersion}/users")
    Call<ResponseStatus> post_users(@Path("cloudVersion") String cloudVersion, @Body User user);
    @GET("{cloudVersion}/users/{id}")
    Call<User> get_users_id(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @GET("{cloudVersion}/users/{id}/photo")
    Call<String> get_users_id_photo(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @PUT("{cloudVersion}/users/{id}")
    Call<ResponseStatus> put_users(@Path("cloudVersion") String cloudVersion, @Path("id") String id,@Body User body);
    @PUT("{cloudVersion}/users/my_profile")
    Call<ResponseStatus> put_my_profile(@Path("cloudVersion") String cloudVersion,@Body User body);
    @PUT("{cloudVersion}/users/{id}/cards")
    Call<ResponseStatus> put_users_id_cards(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body CardsList cardsList);
    @GET("{cloudVersion}/users/{id}/cards")
    Call<CardsList> gut_users_id_cards(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @GET("{cloudVersion}/users/{id}/fingerprint_templates")
    Call<FingerPrints> gut_users_id_fingerprint(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @PUT("{cloudVersion}/users/{id}/fingerprint_templates")
    Call<ResponseStatus> put_users_id_fingerprint(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body FingerPrints cardsList);
    @PUT("{cloudVersion}/users/{id}/face_templates")
    Call<ResponseStatus> put_users_id_face_templates(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body Faces cardsList);
    @GET("{cloudVersion}/users/{id}/face_templates")
    Call<Faces> get_users_id_face_templates(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @DELETE("{cloudVersion}/users/{id}")
    Call<ResponseStatus> delete_users_id(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @HTTP(method = "DELETE", path = "{cloudVersion}/users/delete", hasBody = true)
    Call<ResponseStatus> delete_users(@Path("cloudVersion") String cloudVersion, @Body JsonObject body);
    @GET("{cloudVersion}/users/{id}/mobile_credentials")
    Call<MobileCards> gut_users_id_mobile_credentials(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @GET("{cloudVersion}/users/my_profile/mobile_credentials")
    Call<MobileCards> gut_users_my_profile_mobile_credentials(@Path("cloudVersion") String cloudVersion);
    @POST("{cloudVersion}/users/my_profile/mobile_credentials/{id}/register")
    Call<MobileCardRaw> post_users_my_profile_mobile_credentials(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body JsonObject body);
    @POST("{cloudVersion}/users/{id}/mobile_credentials/issue")
    Call<ResponseStatus> post_users_id_mobile_credentials(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body JsonObject body);


    // User Group
    @GET("{cloudVersion}/user_groups")
    Call<UserGroups> get_user_group(@Path("cloudVersion") String cloudVersion, @QueryMap Map<String, String> params);

    // Access Control
//    @POST("{cloudVersion}/access_groups")
//    Call<ResponseStatus> post_access_groups(@Path("cloudVersion") String cloudVersion, @Body BaseAccessGroup login);
    @GET("{cloudVersion}/access_groups")
    Call<AccessGroups> get_access_groups(@Path("cloudVersion") String cloudVersion,@QueryMap Map<String, String> params);

    // cards
    @POST("{cloudVersion}/cards/csn_card")
    Call<ResponseStatus> post_cards_csn(@Path("cloudVersion") String cloudVersion, @Body JsonObject body);
    @POST("{cloudVersion}/cards/wiegand_card")
    Call<ResponseStatus> post_wiegand_card(@Path("cloudVersion") String cloudVersion, @Body WiegandFormat body);
    @POST("{cloudVersion}/cards/access_on_card")
    Call<ResponseStatus> post_access_on_card(@Path("cloudVersion") String cloudVersion, @Body JsonObject body);
    @POST("{cloudVersion}/cards/secure_credential_card")
    Call<ResponseStatus> post_secure_credential_card(@Path("cloudVersion") String cloudVersion, @Body JsonObject body);
    @GET("{cloudVersion}/cards/unassigned")
    Call<Cards> get_cards_unassigned(@Path("cloudVersion") String cloudVersion, @QueryMap Map<String, String> params);
    @GET("{cloudVersion}/cards/wiegand_cards/formats")
    Call<WiegandFormats> get_cards_wiegand_cards_formats(@Path("cloudVersion") String cloudVersion);
    @GET("{cloudVersion}/cards/smart_cards/layouts")
    Call<SmartCardLayouts> get_cards_smartcards_layouts(@Path("cloudVersion") String cloudVersion, @QueryMap Map<String, String> params);
    @POST("{cloudVersion}/cards/{id}/block")
    Call<ResponseStatus> post_cards_block(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/cards/{id}/unblock")
    Call<ResponseStatus> post_cards_unblock(@Path("cloudVersion") String cloudVersion, @Path("id") String id);

    // setting
    @PUT("{cloudVersion}/setting")
    Call<ResponseStatus> put_setting(@Path("cloudVersion") String cloudVersion, @Body Preference body);
    @GET("{cloudVersion}/setting")
    Call<Preference> get_setting(@Path("cloudVersion") String cloudVersion);
    @GET("{cloudVersion}/setting/biostar_ac")
    Call<BioStarSetting> get_setting_biostar_ac(@Path("cloudVersion") String cloudVersion);
    @GET("{cloudVersion}/setting/permission_list")
    Call<UserPermissions> get_setting_permission_list(@Path("cloudVersion") String cloudVersion);
    @PUT("{cloudVersion}/setting/update_notification_token")
    Call<ResponseStatus> put_update_notification_token(@Path("cloudVersion") String cloudVersion, @Body NotificationToken body);

    // devices
    @GET("{cloudVersion}/devices")
    Call<Devices> get_devices(@Path("cloudVersion") String cloudVersion,@QueryMap Map<String, String> params);
    @GET("{cloudVersion}/devices/{id}")
    Call<Device> get_devices(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @GET("{cloudVersion}/device_types")
    Call<DeviceTypes> get_devices_type(@Path("cloudVersion") String cloudVersion);
    @POST("{cloudVersion}/devices/{id}/scan_fingerprint")
    Call<ScanFingerprintTemplate> post_scan_fingerprint(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body JsonObject body);
    @POST("{cloudVersion}/devices/{id}/verify_fingerprint")
    Call<FingerprintVerify> post_verify_fingerprint(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body VerifyFingerprintOption body);
    @POST("{cloudVersion}/devices/{id}/scan_card")
    Call<Card> post_scan_card(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/devices/{id}/scan_face")
    Call<Face> post_scan_face(@Path("cloudVersion") String cloudVersion, @Path("id") String id, @Body JsonObject body);


    // doors
    @GET("{cloudVersion}/doors")
    Call<Doors> get_doors(@Path("cloudVersion") String cloudVersion, @QueryMap Map<String, String> params);
    @GET("{cloudVersion}/doors/{id}")
    Call<Door> get_door(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/open")
    Call<ResponseStatus> post_doors_open(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/unlock")
    Call<ResponseStatus> post_doors_unlock(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/lock")
    Call<ResponseStatus> post_doors_lock(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/release")
    Call<ResponseStatus> post_doors_release(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/clear_alarm")
    Call<ResponseStatus> post_doors_clear_alaram(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/clear_anti_pass_back")
    Call<ResponseStatus> post_doors_clear_antipassback(@Path("cloudVersion") String cloudVersion, @Path("id") String id);
    @POST("{cloudVersion}/doors/{id}/request_open")
    Call<ResponseStatus> post_doors_request_open(@Path("cloudVersion") String cloudVersion, @Path("id") String id , @Body JsonObject body);

    // monitoring
    @POST("{cloudVersion}/monitoring/event_log/search_more")
    Call<EventLogs> post_monitoring_search(@Path("cloudVersion") String cloudVersion, @Body Query body);

    // refrence
    @GET("{cloudVersion}/references/event_types")
    Call<EventTypes> get_reference_event_type(@Path("cloudVersion") String cloudVersion);
    @GET("{cloudVersion}/references/role_codes")
    Call<CloudRoles> get_reference_role_codes(@Path("cloudVersion") String cloudVersion);

    // version check
    @GET("v2/register/app_versions/{appName}")
    Call<UpdateData> get_app_versions(@Path("appName") String appName, @QueryMap Map<String, String> params);
}
