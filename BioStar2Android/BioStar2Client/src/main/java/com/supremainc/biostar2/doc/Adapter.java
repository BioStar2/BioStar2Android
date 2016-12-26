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
class BaseAdapter
abstract class "BaseListAdapter<T>" {
SwipyRefreshLayout
FloatingActionButton
BaseListViewScroll
}
note top of "BaseListAdapter<T>"
"user,door,device등 여러 data adapter의 원형. 각 adapter의 공통처리 코드"
end note

abstract class "BaseUserAdapter<ListUser>" {
mUserDataProvider;
ArrayList <ListUser> mDatas;
}
note top of "BaseUserAdapter<ListUser>"
"user data를 offset 단위로 끊어서 AC서버로부터 읽어온다. User Data에 대한 처리를 담당한다.
 floating버튼이나 로딩처리 scroll등의 user관련 리스트 UI도 담당한다."
end note

class "PhotoUserAdapter<ListUser>"
note top of "PhotoUserAdapter<ListUser>"
"사진정보가 있는 사용자 리스트 UI를 처리한다.
오직 UI화면 draw와 아이템 클릭만 담당한다."
end note

class "SimpleUserAdapter<ListUser>"
note top of "SimpleUserAdapter<ListUser>"
"사진정보가 없는 사용자 리스트 UI를 처리한다.
오직 UI화면 draw와 아이템 클릭만 담당한다."
end note
class Glide
class Popup

BaseAdapter <|-- "BaseListAdapter<T>"
"BaseListAdapter<T>"  <|-- "BaseUserAdapter<ListUser>"
"BaseUserAdapter<ListUser>" <|-- "PhotoUserAdapter<ListUser>"
"BaseUserAdapter<ListUser>" <|-- "SimpleUserAdapter<ListUser>"
Popup *-- "SimpleUserAdapter<ListUser>" : use
"PhotoUserAdapter<ListUser>" *-- Glide
Glide *-- "okhttp"
UserListFragment *-- "PhotoUserAdapter<ListUser>"
 * @enduml
 */

public class Adapter {

}

