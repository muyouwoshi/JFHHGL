/*
 * Copyright (C) 2016 sanchi3 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.juxin.library.unread;

import java.util.ArrayList;
import java.util.List;

/**
 * 未读角标数据解析类，为角标类型的最小元组
 * Created by sanchi3 on 2016/10/18.
 */
public class Unread {

    private String key;             //角标的key值：不可以为空
    private int num;                //角标显示的数字：本地控制的内容，不存库
    private String show;            //角标显示的文字：如果为文字类型的角标，进行该字段的update，不再次add
    private List<String> indicates; //角标唯一标识：根据key的类型外部确定标识字段，只有最小子级角标才有该属性

    public Unread() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public List<String> getIndicates() {
        return indicates == null ? new ArrayList<String>() : indicates;
    }

    public void setIndicates(List<String> indicates) {
        this.indicates = indicates;
    }

    @Override
    public String toString() {
        return "Unread{" +
                "key='" + key + '\'' +
                ", num=" + num +
                ", show='" + show + '\'' +
                ", indicates=" + indicates +
                '}';
    }
}
