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
package com.supremainc.biostar2.doc;

/**
 * @startuml


package "UserData" {
interface Cloneable
interface Serializable

class Users {
 @SerializedName("records")
 public ArrayList<ListUser> records;
}

class BaseUser {
@SerializedName("status_code")
public String statusCode;
@SerializedName("message")
public String message;

@SerializedName("user_id")
public String user_id;
@SerializedName("name")
public String name;
}

class ListUser {
public static final String TAG = ListDoor.class.getSimpleName();
 @SerializedName("email")
 public String email;
 @SerializedName("user_group")
 public BaseUserGroup user_group;
 @SerializedName("access_groups")
 public ArrayList<ListAccessGroup> access_groups;
 @SerializedName("fingerprint_count")
 public int fingerprint_count;
 @SerializedName("card_count")
 public int card_count;
 @SerializedName("pin_exist")
 public boolean pin_exist;
 @SerializedName("photo_exist")
 public boolean photo_exist;
 @SerializedName("last_modify")
 public String last_modify;
}

 class User {
 @SerializedName("password")
 public String password;
 @SerializedName("security_level")
 public String security_level = "0";
 @SerializedName("pin")
 public String pin;
 @SerializedName("roles")
 public ArrayList<CloudRole> roles;
 @SerializedName("photo")
 public String photo;
 @SerializedName("login_id")
 public String login_id;
@SerializedName("phone_number")
public String phone_number;
@SerializedName("status")
public String status;
@SerializedName("fingerprint_templates")
public ArrayList<ListFingerprintTemplate> fingerprint_templates;
@SerializedName("cards")
public ArrayList<ListCard> cards;
@SerializedName("password_exist")
public boolean password_exist;
@SerializedName("permissions")
public ArrayList<CloudPermission> permissions;
@SerializedName("password_strength_level")
public String password_strength_level;
@SerializedName("start_datetime")
private String start_datetime;
@SerializedName("expiry_datetime")
private String expiry_datetime;
public Calendar getTimeCalendar(TimeConvertProvider convert, UserTimeType timeType);
public boolean setTimeCalendar(TimeConvertProvider convert, UserTimeType timeType, Calendar cal);
public String getTimeFormmat(TimeConvertProvider convert, UserTimeType timeType, TimeConvertProvider.DATE_TYPE type);
public boolean setTimeFormmat(TimeConvertProvider convert, UserTimeType timeType, TimeConvertProvider.DATE_TYPE type, String src);
}

}
 "ListUser" <|- "BaseUser"
 "User" <|- "ListUser"
 Cloneable -- BaseUser
 Serializable -- BaseUser
 Cloneable -- ListUser
 Serializable -- ListUser
 Cloneable -- User
 Serializable -- User

 note top of UserData
 "data type구조 설명을 위해 일부 데이타만 요약해서 적음.
 https://api.biostar2.com/v1/docs/#!/User/의 json data와 1:1로 맵핑되는 멤버변수를 갖는다.
 Class member variable is mapped to 1:1 with json data.
 같은 사용자 데이타지만 상황에 따라 간략화된 정보만 필요한 경우가 있으므로 상황에 알맞게 간략화된 정보나 모든 정보를 사용한다.
 Same user data, but only basic information is needed depending on situations.
 Then use BaseUser or ListUser.
 "
 end note

 note top of User
 "서버는 UTC ZERO를 사용. 모바일 디바이스는 지역에 따라 다양한 UTC 시간대를 사용.
 Servers use UTC zero. The mobile device has various UTC time zones depending on the region.
 getTimeCalendar,getTimeFormmat 를 사용하면 모바일 디바이스의 시간대로 변환된 값이 리턴됨.
 Using getTimeCalendar(getTimeFormmat), the value converted to the time zone of the mobile device.
 setTimeCalendar,setTimeFormmat 를 사용하면 모바일 디바이스의 시간대를 UTC ZERO로 변환되어 전송.
 Using setTimeCalendar(setTimeFormmat), the value converted to the time zone of UTC zero.
 사용자 이미지는 subdomain과 user url 그리고 last_modify 멤버변수를 signature로 cache
 User images are cached.(by subdomain and user url and last_modify) "
 end note
 * @enduml
 */

public class DataType {

}

