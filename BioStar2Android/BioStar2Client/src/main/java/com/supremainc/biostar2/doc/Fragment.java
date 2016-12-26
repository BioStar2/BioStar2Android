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


skinparam packageStyle rect
rectangle ScreenControl {
(addScreen)
(backScreen)
(gotoScreen)
[ScreenType]
}
note "Use fragemnt stack..\nEach fragment has a unique screen type.\nActivity can be know fragment's screen type." as AA

rectangle HomeActivity {

   rectangle "time 8 : screen" {
   ["t8: mainFragment"]
   }
   rectangle "time 7 : screen" {
   ["t7: SettingFragment"] ..> (t7: backScreen) : if fragment stack is empty,goto mainFragment
   }
   rectangle "time 6 : screen" {
   ["t6: SettingFragment"]
   }
   rectangle "time 5 : screen" {
   ["t5: mainFragment"] ..> [t5: DoorListFragment] : addScreen
   [t5: DoorListFragment] ..> (t5: gotoScreen) : SettingFragment
   }
   rectangle "time 4 : screen" {
   ["t4: mainFragment"] ..> [t4: DoorListFragment] : addScreen
   }
   rectangle "time 3 : screen" {
   ["t3: mainFragment"] ..> [t3: DoorListFragment] : addScreen
   [t3: DoorListFragment] ..> [t3: DoorFragment] : addScreen
   [t3: DoorFragment] ..> (t3: backScreen)
   }
   rectangle "time 2 : screen" {
   ["t2: mainFragment"] ..> [t2: DoorListFragment] : addScreen
   [t2: DoorListFragment] ..> (t2: addScreen) : DoorFragment
   }
   rectangle "time 1 : screen" {
    ["t1: mainFragment"] ..> (t1: addScreen) : DoorListFragment
   }
}
HomeActivity *-- ScreenControl
 * @enduml
 */

public class Fragment {

}

