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
package com.supremainc.biostar2.sdk.datatype;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CardData {
	public static class Cards  implements Cloneable, Serializable  {
		private static final long serialVersionUID = -1992147184266614998L;
		public static final String TAG = Cards.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		@SerializedName("records")
		public ArrayList<Card> records;
		@SerializedName("total")
		public int total;

		public Cards() {

		}

		public Cards(ArrayList<Card> rows, int total) {
			this.total = total;
			this.records = rows;
		}

		public Cards(ArrayList<Card> rows) {
			if (rows != null) {
				total = rows.size();
			}
			this.records = rows;
		}

		@SuppressWarnings("unchecked")
		public Cards clone() throws CloneNotSupportedException {
			Cards target = (Cards) super.clone();
			if (records != null) {
				target.records = (ArrayList<Card>) records.clone();
			}
			return target;
		}
	}
	
	public static class BaseCard implements Cloneable, Serializable {
		private static final long serialVersionUID = -7803676161993959797L;
		public static final String TAG = BaseCard.class.getSimpleName();
		@SerializedName("statusCode")
		public String statusCode;
		@SerializedName("message")
		public String message;
		
		@SerializedName("card_id")
		public String card_id ;
		@SerializedName("id")
		public String id ;
		@SerializedName("type")
		public String type ;
		
		public BaseCard() {

		}

		public BaseCard clone() throws CloneNotSupportedException {
			BaseCard target = (BaseCard) super.clone();
			return target;
		}
	}
	
	public static class ListCard extends BaseCard implements Cloneable, Serializable {
		private static final long serialVersionUID = 1080742920196590586L;
		public  static final String TAG = ListCard.class.getSimpleName();
		
		public ListCard() {

		}

		public ListCard clone() throws CloneNotSupportedException {
			ListCard target = (ListCard) super.clone();
			return target;
		}
	}

	public static class Card extends ListCard implements Cloneable, Serializable {
		private static final long serialVersionUID = -4530271987334137218L;
		public static final String TAG = Card.class.getSimpleName();
//		@SerializedName("card_type")
//		public CardType card_type;
//		@SerializedName("wiegand_format_id")
//		public String wiegand_format_id;
//		@SerializedName("active")
//		public boolean active;
//		@SerializedName("user")
//		public BaseUser user;
		@SerializedName("unassigned")
		public boolean unassigned;
				
		public Card clone() throws CloneNotSupportedException {
			Card target = (Card) super.clone();
			return target;
		}
	}


}
