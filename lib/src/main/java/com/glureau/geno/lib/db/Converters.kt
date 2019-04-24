package com.glureau.geno.lib.db

import androidx.room.TypeConverter
import java.util.*


/**
 * Created by Greg on 03/11/2017.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}