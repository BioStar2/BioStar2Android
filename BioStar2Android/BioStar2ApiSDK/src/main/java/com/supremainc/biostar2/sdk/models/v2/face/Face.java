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
package com.supremainc.biostar2.sdk.models.v2.face;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Face implements Cloneable, Serializable {
    public static final String TAG = Face.class.getSimpleName();
    private static final long serialVersionUID = 2416280140967826530L;

    @SerializedName("id")
    public String id ;
    @SerializedName("raw_image")
    public String raw_image ;
    @SerializedName("templates")
    public ArrayList<String> templates;

    public Face() {

    }

    public Face clone() throws CloneNotSupportedException {
        Face target = (Face) super.clone();
        if (templates != null) {
            target.templates = (ArrayList<String>)templates.clone();
        }
        return target;
    }
}