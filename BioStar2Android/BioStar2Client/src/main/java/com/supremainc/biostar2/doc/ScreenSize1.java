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


:"px,sp를 사용하지 않는다. dp를 사용한다.
Do not use px(or sp). use dp";

:"layout match_parent,weight를 적절히 사용한다.
use match_parent(or weight) propely";

:"화면 하단은 가급적 ScrollView,RecyleView,ListView등을 배치해서 Device Height DP가 변해도 자동으로 대응되도록한다.
At the bottom of the screen, position ScrollView, ListView, RecyleView.
Be sure to respond automatically when the height changes.";

:"각각의 Device들이 지원하는 width,height DP가 다름을 인식한다.(해상도와 ppi에 의해)
width(height) DP supported by each device are different.(Rely on resolution and pixel per inch)";

:"한개의 Devic라도 Android 7.0이상은 Setting에서 조정하여 Screen width,height DP가 달라짐을 인식한다.
Even if the same device differnt width(height) DP. (by setting)
ex) nexsus5x
1단계: (UI 컴포넌트가 작아지고 고해상도로 보이는 효과가 난다,UI components become smaller and seen high-resolution.)
xhdpi
display width : 1080, height : 1813, densityDpi : 356
1dp to px:2.225
width dp:485.39328
height dp:814.8315

2단계: (기본값)
xxhdpi
display width : 1080, height : 1794, densityDpi : 420
1dp to px:2.625
3px to dp:1.1428572
width dp:411.42856
height dp:683.4286

3단계:
xxhdpi
display width : 1080, height : 1782, densityDpi : 460
1dp to px:2.875
width dp:375.65216
height dp:619.8261

4단계:
xxhdpi
display width : 1080, height : 1770, densityDpi : 500
1dp to px:3.125
width dp:345.6
height dp:566.4

5단계: (UI 컴포넌트가 커지고 저해상도로 보이는 효과가 난다,UI components become bigger and seen low-resolution)
xxxhdpi
display width : 1080, height : 1758, densityDpi : 540
1dp to px:3.375
width dp:320.0
height dp:520.8889";

:" 물리적인 디바이스 width,height의 dp가 컴포넌트들의 크기보다 작고 scroolview도 listview도 아닐 경우";
split
:"UI 컴포넌트의 크기를 가독성에 영향을 주지 않을 정도로 줄여도 될때는 ui컴포넌트 크기를 줄인다.";
split again
:"화면에서 표현을 생략해도 되는 컴포넌트는 생략한다";
split again
:"일부 UI를 ScrollView로 감싼다";
split again
:"화면설계를 다시 한다.";
end split


 * @enduml
 */

public class ScreenSize1 {

}

