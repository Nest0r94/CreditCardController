package com.example.creditcardcontroller.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListInt(value: List<Int>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toListInt(value: String?): List<Int>? {
        return value?.split(",")?.filter { it.isNotEmpty() }?.map { it.toInt() }
    }

    @TypeConverter
    fun fromListLong(value: List<Long>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toListLong(value: String?): List<Long>? {
        return value?.split(",")?.filter { it.isNotEmpty() }?.map { it.toLong() }
    }
}
