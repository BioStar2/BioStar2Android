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
 * @startuml note "Message Code Table\nEvent Table " as N1
 * note "domain,subdomain,read guide,user/door count" as N2
 * note "manage network requst/response" as N3
 * note "real network protocol lib" as N4
 * note "DB TABLE,COLUMN" as N5
 * note "SQLiteDatabase handle" as N6
 * note "device,door,user,card,access group,permission,etc..." as N7
 * note "provider is singletone\nprovider is several by feature" as N8
 * Provider .. N8
 * "Memory" .. N1
 * "Preference Storage" .. N2
 * "Volley" .. N3
 * "OKhttp" .. N4
 * "DB Adapter(Notification)" .. N5
 * "SQL DB" .. N6
 * N5 .. "DB Adapter(Search Suggestion)"
 * Provider .. N7
 * Provider *-- "DB Adapter(Notification)" : use
 * Provider *-- "DB Adapter(Search Suggestion)" : use
 * Provider *-- "Preference Storage" : use
 * Provider *-- "Security Storage" : use
 * Provider *-- "Volley" : use
 * Provider *-- "Memory" : use
 * "Volley" *-- "OKhttp" : use
 * "OKhttp"  .. "BioStar Cloud" : network
 * "BioStar Cloud" .. "BioStar AC Server" : network
 * "DB Adapter(Notification)" <|- "SQL DB"
 * "DB Adapter(Search Suggestion)" <|- "SQL DB"
 * Activity *-- Provider : use
 * Fragment *-- Provider : use
 * Adapter *-- Provider : use
 * @enduml
 */

public class Provider {

}

