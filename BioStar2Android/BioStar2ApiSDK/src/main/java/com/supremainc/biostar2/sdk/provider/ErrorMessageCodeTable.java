/*
 * Copyright 2015 Suprema(biostar2@suprema.co.kr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supremainc.biostar2.sdk.provider;


public class ErrorMessageCodeTable {
    private static final int NOT_DEFINED = -1;
    // STATUS= OK
    private static final int SUCCESS = 0;        //< Success (no error)
    private static final int PARTIAL_SUCCESS = 1;        //< Request is successful; but partially
    private static final int ALREADY_LOGGED_IN = 2;        //< User has already logged in
    private static final int DEVICE_HAS_INVALID_CONFIG = '3'; //< Device has confugration which server can not be parsed.
    private static final int WEB_REQUEST_TIMEOUT = 4;        //< Synced Web Request is not respond in timeout period
    // STATUS= UNAUTHORIZED
    private static final int LOGIN_REQUIRED = 10;        //< Login is required
    private static final int SESSION_TIMEOUT = 11;        //< Session is invalid for timeout
    // STATUS= FORBIDDEN
    private static final int PERMISSION_DENIED = 20;        //< Request is forbidden because have no permission
    private static final int INVALID_PARAMETERS = 30;   //< Parameter(s) is(are) not valid on URI.
    // BAD_REQUEST
    // general
    private static final int LOGIN_FAILED = 101;        //< Failed to login for invalid username or password
    private static final int METHOD_NOT_ALLOWED = 102;        //< Request method is not allowed
    private static final int NOT_SUPPORTED_REQUEST = 103;        //< Request is not supported
    private static final int INVALID_JSON_FORMAT = 104;        //< Request parameter is invalid
    private static final int INVALID_QUERY_PARAMETER = 105;        //< Request query parameter is invalid
    private static final int COULD_NOT_UPDATE_OR_DELETE_PREDEFINED_ITEMS = 106; //< Impossible to update or delete predefined items.
    // user
    private static final int USER_NOT_FOUND = 201;        //< User can not be found with id
    private static final int DUPLICATED_USER = 202;        //< User who has same id alreay exists
    private static final int DUPLICATED_FINGERPRINT = 203;        //< Fingerprint template duplicated with other user
    private static final int DUPLICATED_USERGROUP = 204;        //< User group that has same name already exists
    private static final int ADMIN_COULD_NOT_BE_DELETED = 205;///< Admin could be deleted.
    private static final int ADMIN_COULD_NOT_HAVE_OTHER_PERMISSION = 206;
    private static final int ID_COULD_NOT_BE_CHANGED = 207;
    private static final int DUPLICATED_LOGIN_ID = 208;        //< User who has same id alreay exists
    private static final int FAILED_TO_ENROLL_USER = 209;        //< Failed to enroll user
    private static final int DEVICE_USERS_FULL = 210;        //< users maxed out on device
    // device
    private static final int DUPLICATED_DEVICE = 300;        //< Device that has same name already exists
    private static final int DEVICE_NOT_FOUND = 301;        //< Device can not be found with id
    private static final int DEVICE_COULD_NOT_ENABLE_FULL_ACCESS = 302; //< Device what is included in an Access Group can not enable full access.
    private static final int TOO_MANY_DEVICE_OPERATOR = 303;//< The number of device operators can be up to 10.
    private static final int FAILED_TO_ADD_DEVICE = 304;//< Failed to add device.
    private static final int DUPLICATED_DEVICE_GROUP = 65645;        //
    private static final int TOO_MANY_ACCESS_GROUP_ASSIGNED = 65637;        //< Too many access groups are assigned for given user ( max 16 access groups)
    // card
    private static final int CARD_NOT_FOUND = 400;        //< Card can not be found with id
    private static final int DUPLICATED_CARD = 401;        //< Card that has same id already exists
    // device group
    private static final int DEVICE_GROUP_NOT_FOUND = 500;///< Device Group doesn't exist.
    // User Group
    private static final int USER_GROUP_NOT_FOUND = 600;///< User Group doesn't exist.
    // Door
    private static final int DOOR_NOT_FOUND = 601;///< Door doesn't exist.
    private static final int HOLIDAYGROUP_NOT_FOUND = 602;///< Holiday Group doesn't exist.
    private static final int ACCESSGROUP_NOT_FOUND = 603;///< Access Group doesn't exist.
    private static final int SCHEDULE_NOT_FOUND = 604;///< Schedule doesn't exist.
    private static final int ACCESSLEVEL_NOT_FOUND = 605;///< Access Level doesn't exist.
    private static final int PERMISSION_NOT_FOUND = 606;
    private static final int DOOR_GROUP_NOT_FOUND = 607;///< Door Group doesn't exist.
    private static final int PREFERENCE_NOT_FOUND = 608;
    //Access Group
    private static final int COULD_HAVE_FULL_ACCESS_DEVICES = 650; //< Access Group can not have Device(s) what is(are) set Full Access enabled.
    private static final int TOO_MANY_ACCESSGROUP = 651;
    private static final int ACCESSGROUPLEVEL_DUPLICATED_NAME = 652;   //< The name is duplicated.
    private static final int NO_ACCESSGROUP_NAME_EXISTS = 653;
    private static final int INVALID_DOOR = 65644;
    // Json
    private static final int FAILED_TO_PARSE_JSON = 700;///< Failed to parse json data.
    private static final int FAILED_TO_PARSE_JSON_INTERNAL = 701; //< Failed to parse json data by internal server error
    private static final int FAILED_TO_EXECUTE_DB_QUERY = 800;///< Failed to execute Database query
    // STATUS= INTERNAL
    // General
    private static final int SERVER_ERROR = 1000;        //< Something wrong with server
    private static final int DEVICE_IS_NOT_CONNECTED = 1001;        //< Device is not connected to biostar
    private static final int DEVICE_IS_NOT_READY = 1002;        //< Device is connected but not accpeted
    private static final int DEVICE_REQUEST_TIMEOUT = 1003;        //< Device does not respond within the timeout period
    // Network
    private static final int NET_INVALID_ADDRESS = 1004;        //< Network address is invalid
    private static final int NET_CONNECTION_FAILED = 1005;        //< Failed to connect device
    private static final int NET_WRONG_CHECKSUM = 1010;        //< Packet checksum is wrong
    private static final int NET_MALFORMED_HEADER = 1011;        //< Header format is not valid
    private static final int NET_MALFORMED_PAYLOAD = 1012;        //< Payload format is not valid
    private static final int DEVICE_NOT_SUPPORTED = 1013;
    // fingerprint scan
    private static final int FINGERPRINT_QUALITY_TOO_LOW = 1014;
    private static final int FAILED_TO_VERIFY_FINGERPRINT = 1015;
    private static final int FAILED_TO_UPGRADE_FIRMWARE = 1016;
    private static final int DEVICE_IS_BUSY = 1017;
    private static final int FAILED_TO_SCAN_FINGERPRINT = 1018;
    //biostar update
    private static final int BIOSTAR_UPDATE_SERVER_BUSY = 1020;  //< biostar launcher is busy... try later.
    private static final int BIOSTAR_UPDATE_NOT_EXIST = 1021;  //< biostar update version is not exist
    private static final int LAUNCHER_REQUEST_TIMEOUT = 1022;    //< Launcher does not respond within the timeout period
    //wet socket
    private static final int WEB_SOCKET_NOT_FOUND = 1100; //< web socket not found by session id
    private static final int WEB_SOCKET_INVALID_SESSION = 1101; //< invalid session
    // user
    private static final int INVALID_LENGTH_OF_USERID = 131072;
    private static final int INVALID_USERID = 131073;
    private static final int INVALID_USER_NAME = 131074;
    private static final int INVALID_LENGTH_OF_TITLE = 131075;
    private static final int INVALID_LENGTH_OF_PHONE_NUM = 131076;
    private static final int INVALID_PHONE = 131077;
    private static final int INVALID_LENGTH_OF_EMAIL = 131078;
    private static final int INVALID_EMAIL = 131079;
    private static final int INVALID_LENGTH_OF_PIN = 131080;
    private static final int INVALID_PIN = 131081;
    private static final int INVALID_LENGTH_OF_LOGINID = 131082;
    private static final int INVALID_LOGINID = 131083;
    private static final int INVALID_LENGTH_OF_PASSWORD = 131084;
    private static final int INVALID_EXPIRY_DATE = 131085;
    private static final int EXPIRY_DATE_IS_LT_START = 131086;
    private static final int INVALID_LENGTH_OF_MESSAGE = 131087;
    private static final int INVALID_COUNT_OF_FINGERPRINT = 131088;
    private static final int INVALID_COUNT_OF_FACETEMPLATE = 131089;
    private static final int OVER_MAX_ACCESS_GROUPS = 131090;
    private static final int INVALID_SECURITY_LEVEL = 131091;
    private static final int INVALID_DEVICE_STATUS = 131092;
    private static final int EXCEED_DESCRIPTION_MAX_LENGTH = 131093;
    private static final int EXCEED_NAME_MAX_LENGTH = 131094;
    private static final int CARD_ID_IS_REQUIRED = 131095;
    private static final int PARENT_DOOR_GROUP_IS_REQUIRED = 131096;
    private static final int DUPLICATE_USER_GROUP = 65646;
    private static final int PARENT_USER_GROUP_NOT_FOUND = 65647;
    private static final int PARENT_USER_GROUP_NOT_SET = 65648;
    private static final int USER_NOT_EXIST = 65649;
    private static final int ERR_NUM_CARD_ID_ALREADY_EXISTS = 65651;
    private static final int ERR_NUM_DOOR_NAME_ALREADY_EXISTS = 65652;
    private static final int ERR_NUM_DOOR_GROUP_NAME_ALREADY_EXISTS = 65653;
    private static final int ERR_NUM_DOOR_GROUP_NOT_FOUND = 65654;
    private static final int ERR_NUM_DEVICE_ALREADY_USED = 65655;
    private static final int ERR_NUM_RELAY_ALREADY_USED = 65656;
    private static final int ERR_NUM_DEVICE_NOT_IN_SAME_RS485 = 65657;

    public static String getMessage(int code) {
        switch (code) {
            case ACCESSGROUPLEVEL_DUPLICATED_NAME:
                return "Name Already Exists";
            case ACCESSGROUP_NOT_FOUND:
                return "Access Group Not Found";
            case ACCESSLEVEL_NOT_FOUND:
                return "Access Level Not Found";
            case ADMIN_COULD_NOT_BE_DELETED:
                return "Administrator cannot be deleted.";
            case ADMIN_COULD_NOT_HAVE_OTHER_PERMISSION:
                return "Admin is not allowed to have other permissions.";
            case ALREADY_LOGGED_IN:
                return "Already Logged In";
            case BIOSTAR_UPDATE_NOT_EXIST:
                return "Update for BioStar does not exist.";
            case BIOSTAR_UPDATE_SERVER_BUSY:
                return "BioStar server is busy.  Please try again later.";
            case CARD_ID_IS_REQUIRED:
                return "Card Id is required.";
            case CARD_NOT_FOUND:
                return "Card Not Found";
            case COULD_HAVE_FULL_ACCESS_DEVICES:
                return "Device with full access enabled cannot be assigned to Access Group";
            case COULD_NOT_UPDATE_OR_DELETE_PREDEFINED_ITEMS:
                return "Predefined items cannot be updated.";
//		 case DB_CONFIGURATION_FAILED                     : return "Invalid Database Configuration";
//		 case DB_CONNECTION_FAILED                        : return "Connection to database failed.";
//		 case DB_INTERNAL_ERROR                           : return "Database Internal Error";
//		 case DB_INVALID_VALUE_ERROR                      : return "Invalid Value";
            case DEVICE_COULD_NOT_ENABLE_FULL_ACCESS:
                return "Unable to enable full access for Device included in Access Group";
            case DEVICE_GROUP_NOT_FOUND:
                return "Device Group Not Found";
            case DEVICE_HAS_INVALID_CONFIG:
                return "Device has configuration which server can not be parsed. Factory reset is recommended.";
            case DEVICE_IS_BUSY:
                return "Device is now busy. Please try again later.";
            case DEVICE_IS_NOT_CONNECTED:
                return "Device Not Connected";
            case DEVICE_IS_NOT_READY:
                return "Device Not Ready";
            case DEVICE_NOT_FOUND:
                return "Device Not Found";
            case DEVICE_NOT_SUPPORTED:
                return "Request Not Supported By Device";
            case DEVICE_REQUEST_TIMEOUT:
                return "Device Timed Out";
            case DEVICE_USERS_FULL:
                return "Users cannot be added anymore.";
            case DOOR_GROUP_NOT_FOUND:
                return "Door Group Not Found";
            case DOOR_NOT_FOUND:
                return "Door Not Found";
            case DUPLICATED_CARD:
                return "Duplicate Card";
            case DUPLICATED_DEVICE:
                return "Duplicate Device";
            case DUPLICATED_DEVICE_GROUP:
                return "Duplicate Device Group";
            case DUPLICATED_FINGERPRINT:
                return "Duplicate Fingerprint";
            case DUPLICATED_LOGIN_ID:
                return "Duplicate Login ID";
            case DUPLICATED_USER:
                return "Duplicate User";
            case DUPLICATED_USERGROUP:
                return "Duplicate User Group";
            case DUPLICATE_USER_GROUP:
                return "Duplicate User Group";
            case ERR_NUM_CARD_ID_ALREADY_EXISTS:
                return "Card ID already exists.";
            case ERR_NUM_DEVICE_ALREADY_USED:
                return "Device already being used.";
            case ERR_NUM_DEVICE_NOT_IN_SAME_RS485:
                return "Device does not exist within same RS485 network.";
            case ERR_NUM_DOOR_GROUP_NAME_ALREADY_EXISTS:
                return "Door group name already exists.";
            case ERR_NUM_DOOR_GROUP_NOT_FOUND:
                return "Door Group Not Found";
            case ERR_NUM_DOOR_NAME_ALREADY_EXISTS:
                return "Door name already exists.";
            case ERR_NUM_RELAY_ALREADY_USED:
                return "Relay already being used.";
            case EXCEED_DESCRIPTION_MAX_LENGTH:
                return "Maximum length of Description exceeded.";
            case EXCEED_NAME_MAX_LENGTH:
                return "Maximum length of Name exceeded.";
            case EXPIRY_DATE_IS_LT_START:
                return "Expiration Date is less than Start Date.";
            case FAILED_TO_ADD_DEVICE:
                return "Failed to add device.";
            case FAILED_TO_ENROLL_USER:
                return "Enroll user failed.";
            case FAILED_TO_EXECUTE_DB_QUERY:
                return "One or more values are invalid. Check the values and try again.";
            case FAILED_TO_PARSE_JSON:
                return "Failed to parse JSON.";
            case FAILED_TO_PARSE_JSON_INTERNAL:
                return "Failed to parse JSON.";
            case FAILED_TO_SCAN_FINGERPRINT:
                return "Failed to scan fingerprint.";
            case FAILED_TO_UPGRADE_FIRMWARE:
                return "Failed to upgrade firmware.";
            case FAILED_TO_VERIFY_FINGERPRINT:
                return "Failed to verify fingerprint.";
            case FINGERPRINT_QUALITY_TOO_LOW:
                return "Fingerprint quality is too low.";
            case HOLIDAYGROUP_NOT_FOUND:
                return "Holiday Group Not Found";
            case ID_COULD_NOT_BE_CHANGED:
                return "ID cannot be modified.";
            case INVALID_COUNT_OF_FACETEMPLATE:
                return "Too Many Face Templates";
            case INVALID_COUNT_OF_FINGERPRINT:
                return "Too Many Fingerprints";
            case INVALID_DEVICE_STATUS:
                return "Invalid Device Status";
            case INVALID_DOOR:
                return "Invalid Door";
            case INVALID_EMAIL:
                return "Invalid Email";
            case INVALID_EXPIRY_DATE:
                return "Invalid Expiration Date";
            case INVALID_JSON_FORMAT:
                return "Invalid JSON";
            case INVALID_LENGTH_OF_EMAIL:
                return "Invalid Length of Email";
            case INVALID_LENGTH_OF_LOGINID:
                return "Invalid Length of Login Id";
            case INVALID_LENGTH_OF_MESSAGE:
                return "Invalid Length of Message";
            case INVALID_LENGTH_OF_PASSWORD:
                return "Invalid Length of Password";
            case INVALID_LENGTH_OF_PHONE_NUM:
                return "Invalid Length of Telephone";
            case INVALID_LENGTH_OF_PIN:
                return "Invalid Length of PIN";
            case INVALID_LENGTH_OF_TITLE:
                return "Invalid Length of Title";
            case INVALID_LENGTH_OF_USERID:
                return "Invalid Length of ID";
            case INVALID_LOGINID:
                return "Invalid Login ID";
            case INVALID_PARAMETERS:
                return "Invalid Parameters";
            case INVALID_PHONE:
                return "Invalid Telephone";
            case INVALID_PIN:
                return "Invalid PIN";
            case INVALID_QUERY_PARAMETER:
                return "Invalid Query";
            case INVALID_SECURITY_LEVEL:
                return "Invalid Security Level";
            case INVALID_USERID:
                return "Invalid User ID";
            case INVALID_USER_NAME:
                return "Invalid User Name";
            case LAUNCHER_REQUEST_TIMEOUT:
                return "Launcher failed to respond.";
            case LOGIN_FAILED:
                return "ID or password is incorrect. <br/>Make sure you're using ID or password for BioStar 2.";
            case LOGIN_REQUIRED:
                return "Login Required";
            case METHOD_NOT_ALLOWED:
                return "Request Method Not Allowed";
            case NET_CONNECTION_FAILED:
                return "Connection to device failed";
            case NET_INVALID_ADDRESS:
                return "Invalid Network Address";
            case NET_MALFORMED_HEADER:
                return "Invalid Header";
            case NET_MALFORMED_PAYLOAD:
                return "Invalid Payload";
            case NET_WRONG_CHECKSUM:
                return "Invalid Checksum";
            case NOT_DEFINED:
                return "Not Defined";
            case NOT_SUPPORTED_REQUEST:
                return "Request Not Supported";
            case NO_ACCESSGROUP_NAME_EXISTS:
                return "There is no Access Group with specified name.";
            case OVER_MAX_ACCESS_GROUPS:
                return "Too Many Access Groups";
            case PARENT_DOOR_GROUP_IS_REQUIRED:
                return "Parent door group is required.";
            case PARENT_USER_GROUP_NOT_FOUND:
                return "Parent User Group Not Found";
            case PARENT_USER_GROUP_NOT_SET:
                return "Parent User Group Not Set";
            case PARTIAL_SUCCESS:
                return "Processed with some errors";
            case PERMISSION_DENIED:
                return "Permission Denied";
            case PERMISSION_NOT_FOUND:
                return "Permission Not Found";
            case PREFERENCE_NOT_FOUND:
                return "Preference Not Found";
            case SCHEDULE_NOT_FOUND:
                return "Schedule Not Found";
            case SERVER_ERROR:
                return "Server Error";
//		 case SERVER_ERROR_DETAIL_MESSAGE                 : return "An unknown error occurred. Please check the log file for detail information.";
            case SESSION_TIMEOUT:
                return "Session Invalid";
            case SUCCESS:
                return "Successful";
            case TOO_MANY_ACCESSGROUP:
                return "There are too many access groups.";
            case TOO_MANY_ACCESS_GROUP_ASSIGNED:
                return "Too many access groups are assigned for given user.";
            case TOO_MANY_DEVICE_OPERATOR:
                return "Maximum of 10 Device operators are allowed";
            case USER_GROUP_NOT_FOUND:
                return "User Group Not Found";
            case USER_NOT_EXIST:
                return "User does not exist.";
            case USER_NOT_FOUND:
                return "User Not Found";
            case WEB_REQUEST_TIMEOUT:
                return "Request Timed Out";
            case WEB_SOCKET_INVALID_SESSION:
                return "Invalid session in web socket.";
            case WEB_SOCKET_NOT_FOUND:
                return "Web socket not found.";
        }
        return null;
    }
}
