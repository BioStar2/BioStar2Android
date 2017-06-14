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
 * @startuml skinparam packageStyle rect
 * note "ListView or RecyleView or ScrollView" as M3
 * rectangle "screen1" {
 * rectangle "UI_Component_1" {
 * }
 * rectangle "UI_Component_2" {
 * }
 * rectangle "UI_Component_3" {
 * <p>
 * }
 * UI_Component_1 -- UI_Component_2
 * UI_Component_2 -- UI_Component_3
 * M3 .. UI_Component_3
 * }
 * <p>
 * note "상황에 따라 Empty영역을 특정DP로 고정하지 않고 weight값으로 처리. 혹은 특정 UI 영역을 weight로 처리" as M2
 * rectangle "screen2" {
 * rectangle "UI_Component_4" {
 * }
 * rectangle "emptyArea1" {
 * }
 * rectangle "UI_Component_5" {
 * }
 * rectangle "emptyArea2" {
 * }
 * rectangle "UI_Component_6" {
 * }
 * UI_Component_4 -- emptyArea1
 * emptyArea1 -- UI_Component_5
 * UI_Component_5 -- emptyArea2
 * emptyArea2 -- UI_Component_6
 * M2 .. emptyArea1
 * M2 .. emptyArea2
 * }
 * <p>
 * note "Device Height DP가  600DP보다 낮을때 모든 UI 컴포넌트 Height의 합이 Device Height보다 클 수 있다\n Menu컴포넌트를 scrollview에 넣는것보다 realtime에 RingTimeView의 크기를 조절하거나 보이지 않게 하는게 더 사용성이 좋다" as M4
 * note "고정높이" as M5
 * note "고정높이지만 스크린 사이즈에 따라 가변되거나 Gone됨" as M6
 * note "퍼미션에 따라 메뉴아이템의 갯수가 달라져 가변높이됨" as M7
 * <p>
 * rectangle "screen3" {
 * rectangle "UI_Component_7" {
 * }
 * rectangle "RingTimeView" {
 * }
 * rectangle "Menu" {
 * }
 * UI_Component_7 -- RingTimeView
 * RingTimeView -- Menu
 * }
 * M4 .. screen3
 * UI_Component_7 .. M5
 * RingTimeView .. M6
 * Menu .. M7
 * @enduml
 */

public class ScreenSize2 {

}

