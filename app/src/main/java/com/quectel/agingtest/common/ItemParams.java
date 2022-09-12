package com.quectel.agingtest.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用于测试项需要配置的参数
 */
public class ItemParams implements Parcelable {
    public String key; // 用于读取的设置的参数的
    public String name; // 用于展示当前参数信息
    public String desc; // 用于参数当前参数描述

    public ItemParams(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public ItemParams(String key, String name, String desc) {
        this(key,name);
        this.desc = desc;
    }

    protected ItemParams(Parcel in) {
        key = in.readString();
        name = in.readString();
        desc = in.readString();
    }

    public static final Creator<ItemParams> CREATOR = new Creator<ItemParams>() {
        @Override
        public ItemParams createFromParcel(Parcel in) {
            return new ItemParams(in);
        }

        @Override
        public ItemParams[] newArray(int size) {
            return new ItemParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(desc);
    }
}
