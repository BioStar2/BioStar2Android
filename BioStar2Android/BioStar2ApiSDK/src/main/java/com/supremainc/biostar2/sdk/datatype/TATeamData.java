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

public class TATeamData {


	public static class MyTeam implements Cloneable, Serializable {
		public static final String TAG = MyTeam.class.getSimpleName();
		private static final long serialVersionUID = 42943682986981617L;

		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name ;
		@SerializedName("member_list")
		public ArrayList<TeamMember> member_list ;

		public MyTeam() {

		}

		public MyTeam clone() throws CloneNotSupportedException {
			MyTeam target = (MyTeam) super.clone();
			if (member_list != null) {
				target.member_list = ( ArrayList<TeamMember>)member_list.clone();
			}
			return target;
		}
	}

	public static class TeamMember implements Cloneable, Serializable {
		public static final String TAG = TeamMember.class.getSimpleName();
		private static final long serialVersionUID = 4306771365419079372L;

		@SerializedName("leader")
		public boolean leader;
		@SerializedName("name")
		public String name ;
		@SerializedName("user_id")
		public String user_id  ;

		public TeamMember() {

		}

		public TeamMember clone() throws CloneNotSupportedException {
			TeamMember target = (TeamMember) super.clone();
			return target;
		}
	}
}
