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
 * @startuml package "com.supremainc.biostar2.sdk" #DDDDDD {
 * <p>
 * package "sdk.datatype" #DDDDDD {
 * class "Each Data Class" implements Cloneable, Serializable
 * }
 * package "sdk.provider" #DDDDDD {
 * class DoorDataProvider
 * class DeviceDataProvider
 * class ManyDataProvider
 * class BaseDataProvider
 * DoorDataProvider <|- BaseDataProvider
 * DeviceDataProvider <|- BaseDataProvider
 * ManyDataProvider <|- BaseDataProvider
 * }
 * package "sdk.utils" {
 * <p>
 * }
 * package "sdk.volley" #DDDDDD {
 * <p>
 * }
 * package "sdk.okhttp" #DDDDDD {
 * <p>
 * }
 * sdk.provider *-- sdk.volley : use
 * sdk.volley *-- sdk.okhttp : use
 * <p>
 * note top of sdk.provider
 * "BioStar 2 Cloud API Wrapper
 * https://api.biostar2.com/v1/docs/"
 * end note
 * <p>
 * note top of sdk.volley
 * "manage network requst/response"
 * end note
 * <p>
 * note top of sdk.okhttp
 * "real network protocol lib"
 * end note
 * <p>
 * note top of "Each Data Class"
 * "json convert class"
 * end note
 * }
 * <p>
 * <p>
 * <p>
 * <p>
 * package "com.supremainc.biostar2" {
 * package "activity"  {
 * <p>
 * }
 * package "fragment"  {
 * <p>
 * }
 * <p>
 * package "util"  {
 * <p>
 * }
 * package "service"  {
 * package "push"  {
 * <p>
 * }
 * package "nfc"  {
 * <p>
 * }
 * package "ble"  {
 * <p>
 * }
 * }
 * package "impl"  {
 * <p>
 * }
 * <p>
 * <p>
 * package "adapter" #AEEEEE {
 * class PhotoUserAdapter
 * class SimpleUserAdapter
 * package "adapter.base" #FFFFFF {
 * abstract class BaseUserAdapter
 * }
 * }
 * "PhotoUserAdapter" <|- "BaseUserAdapter"
 * "SimpleUserAdapter" <|- "BaseUserAdapter"
 * note "develop view code" as Madapter
 * Madapter .. PhotoUserAdapter
 * Madapter .. SimpleUserAdapter
 * note top of adapter.base
 * "develop control(data manage) code"
 * end note
 * <p>
 * <p>
 * package "datatype" #DDDDDD {
 * <p>
 * }
 * package "db" #DDDDDD {
 * <p>
 * }
 * package "provier" #DDDDDD  {
 * <p>
 * }
 * package "meta" #DDDDDD {
 * <p>
 * }
 * <p>
 * package "view" #AEEEEE {
 * <p>
 * }
 * package "widget" #AEEEEE {
 * <p>
 * }
 * <p>
 * package "android.support" #888888 {
 * <p>
 * }
 * package "com.github.bumptech.glide" #888888 {
 * <p>
 * }
 * note bottom of com.github.bumptech.glide
 * "image loading lib(LRU DISK/MEMORY Cache)"
 * end note
 * <p>
 * package "com.orangegangsters.github.swipyrefreshlayout.library" #888888 {
 * <p>
 * }
 * note bottom of com.orangegangsters.github.swipyrefreshlayout.library
 * "draw (watting of refresh) material icon"
 * end note
 * <p>
 * package "com.tekinarslan.material.sample" #888888 {
 * <p>
 * }
 * note bottom of com.tekinarslan.material.sample
 * "draw direction floating action button"
 * end note
 * <p>
 * }
 * <p>
 * <p>
 * <p>
 * com.supremainc.biostar2 *----- "com.supremainc.biostar2.sdk" : use grandle option: compile 'com.supremainc.biostar2:biostar2sdk:1.1.+'
 * @enduml
 */

public class Package {

}

