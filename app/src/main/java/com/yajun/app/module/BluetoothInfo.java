package com.yajun.app.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yajun on 2017/5/31.
 *
 */
public class BluetoothInfo implements Parcelable {

    public BluetoothInfo(String _name , String _address ){
        this.name = _name;
        this.address = _address;
    }

    public String name;
    public String address;

    protected BluetoothInfo(Parcel in) {
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<BluetoothInfo> CREATOR = new Creator<BluetoothInfo>() {
        @Override
        public BluetoothInfo createFromParcel(Parcel in) {
            return new BluetoothInfo(in);
        }

        @Override
        public BluetoothInfo[] newArray(int size) {
            return new BluetoothInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
    }
}
