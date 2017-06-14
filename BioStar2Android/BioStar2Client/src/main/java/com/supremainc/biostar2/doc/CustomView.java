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
 * @startuml note "The following is the criteria for making customview(or widget).\nrepetition use rate is high.\nIf the feature is meaningful to the UI component." as AA
 * note top of view
 * "Common view feature.
 * process only view.
 * Custom view has interfaces with the outside, if necessary.(touch event,UI value)
 * Custom view is pure view(It is possible to use in xml)"
 * end note
 * package "view" #AEEEEE {
 * class BaseView
 * note bottom of BaseView
 * "process common handle view
 * most of custom view inherit the baseview
 * When multiple same custom views are used, the child views will have the same android-id.(need process  saveInstance/restoreInstance HierarchyState)"
 * end note
 * <p>
 * class  	DetailEditItemView
 * class  	DetailSwitchItemView
 * class  	DetailTextItemView
 * class  	DrawLayerMenuView
 * class  	FilterView
 * class  	Html5WebView
 * class  	LoginView
 * class  	MainMenuView
 * class  	MenuItemView
 * class  	MobileCardListView
 * class  	RingTimeView
 * class  	SearchViewEx
 * class  	StyledEditTextView
 * class  	StyledTextView
 * class  	SubToolbar
 * class  	SummaryDoorView
 * class  	SummaryUserView
 * class  	SwitchView
 * view <|- BaseView
 * SummaryDoorView <|- BaseView
 * SummaryUserView <|- BaseView
 * SwitchView <|- BaseView
 * }
 * StyledEditTextView <|-- EditTextView
 * StyledTextView <|-- TextView
 * <p>
 * note "Use the font defined in the view properties. (StyledEditTextView,StyledTextView) Use to replace all textview and edittext" as Mfont
 * Mfont .. EditTextView
 * Mfont .. TextView
 * note top of widget
 * "widget is UI component. but widget class is not pure view."
 * end note
 * package "widget" #AEEEEE {
 * package "popup" #AEEEEE {
 * class PasswordPopup
 * class Popup
 * class SelectCustomData
 * class SelectPopup
 * class ToastPopup
 * }
 * class  	ActionbarTitle
 * class  	CustomDialog
 * class  	DateTimePicker
 * class  	ScreenControl
 * }
 * package "assets" #AEEEEE {
 * class "ttf font"
 * }
 * package "res" #AEEEEE {
 * package "drawable" #AEEEEE {
 * <p>
 * }
 * package "layout" #AEEEEE {
 * <p>
 * }
 * package "xml" #AEEEEE {
 * <p>
 * }
 * package "anim" #AEEEEE {
 * <p>
 * }
 * package "values" #AEEEEE {
 * class attrs
 * class colors
 * }
 * }
 * widget *-- "res" : use
 * view *-- "res" : use
 * StyledEditTextView *-- assets
 * StyledTextView *-- assets
 * <p>
 * package "fragment"  {
 * <p>
 * }
 * interface "DetailEditItemView Interface"
 * interface "Each Custom View Interface"
 * fragment -- "Each Custom View Interface"
 * fragment -- "DetailEditItemView Interface"
 * "Each Custom View Interface" -- view
 * "DetailEditItemView Interface" -- DetailEditItemView
 * @enduml
 */

public class CustomView {

}

