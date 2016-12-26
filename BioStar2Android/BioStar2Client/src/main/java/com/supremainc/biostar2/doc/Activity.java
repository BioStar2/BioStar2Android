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
class HomeActivity
LoginActivity -> HomeActivity
LoginActivity -> GuideActivity
LoginActivity <- GuideActivity

package "push service"  {
}
"push service" -> DummyActivity
DummyActivity->HomeActivity : if running
DummyActivity->LoginActivity : if not running
HomeActivity-->AlarmListFragment : if user started at push notification
note "Auto Destroy" as N3
N3 .. DummyActivity
note "User Launch Application"  as N1
note "if the connection session is valid, the screen will be omitted"  as N2
N1  .. LoginActivity
N2 .. LoginActivity

 * @enduml
 */

public class Activity {

}

