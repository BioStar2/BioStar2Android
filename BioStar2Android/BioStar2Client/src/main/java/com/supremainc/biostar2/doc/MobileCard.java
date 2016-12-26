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
box "phsycial area" #LightBlue
participant device
end box

box "smart phone" #LightBlue
participant "nfc/ble service"
participant provider
participant "so lib"
participant "security storage"
participant "application ui"
end box

device -> "nfc/ble service" : AID check
"nfc/ble service" -> provider : check latest login
provider -> "application ui" : if more than two weeks
provider -> "nfc/ble service" : if In two weeks
"nfc/ble service" -> device : AID check reponse
device -> "nfc/ble service" : Auth request
"nfc/ble service" -> provider : Auth request
provider -> "so lib" : JNI
"so lib"  <--> "security storage"
"so lib"  -> provider : Auth reponse
provider -> "nfc/ble service" : Auth reponse
"nfc/ble service" -> device : Auth reponse
device -> "nfc/ble service" : request command
"nfc/ble service" -> "provider" : request command
"provider" -> "so lib" : JNI
"so lib"  <--> "security storage"
"so lib" -> provider : command response
"provider" -> "nfc/ble service" : command response
"nfc/ble service" -> device : command response
 * @enduml
 */

public class MobileCard {

}

