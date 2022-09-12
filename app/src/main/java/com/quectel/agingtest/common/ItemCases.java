package com.quectel.agingtest.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ItemCases implements Parcelable {
    public String id; // 用于区分的编号
    public String name;
    public int type; // 0:表示次数;1:表示时间 单位分钟
    public int time; // 次数或者时间
    public List<ItemParams> params;// 参数

    public ItemCases(String id,String name, int type, int time) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.time = time;
    }

    protected ItemCases(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readInt();
        time = in.readInt();
        params = in.createTypedArrayList(ItemParams.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeInt(time);
        dest.writeTypedList(params);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemCases> CREATOR = new Creator<ItemCases>() {
        @Override
        public ItemCases createFromParcel(Parcel in) {
            return new ItemCases(in);
        }

        @Override
        public ItemCases[] newArray(int size) {
            return new ItemCases[size];
        }
    };
}
