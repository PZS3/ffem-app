/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly.
 *
 * Akvo Caddisfly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Caddisfly. If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.caddisfly.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"uid"})
public class CalibrationDetail implements Parcelable {

    @SuppressWarnings("unused")
    public static final Creator<CalibrationDetail> CREATOR = new Creator<CalibrationDetail>() {
        @Override
        public CalibrationDetail createFromParcel(Parcel in) {
            return new CalibrationDetail(in);
        }

        @Override
        public CalibrationDetail[] newArray(int size) {
            return new CalibrationDetail[size];
        }
    };
    @NonNull
    public String uid = "";
    @ColumnInfo(name = "date")
    public long date;
    @ColumnInfo(name = "expiry")
    public long expiry;
    @ColumnInfo(name = "cuvetteType")
    public String cuvetteType;

    public CalibrationDetail() {
    }

    private CalibrationDetail(Parcel in) {
        uid = in.readString();
        date = in.readLong();
        expiry = in.readLong();
        cuvetteType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeLong(date);
        dest.writeLong(expiry);
        dest.writeString(cuvetteType);
    }
}