package com.example.grocerychecklist.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

enum class IconOption(val iconName: String, val imageVector: ImageVector) {
    Android("android", Icons.Filled.Android),
    Home("home", Icons.Filled.Home);

    companion object {
        fun fromName(name: String): IconOption? {
            return entries.find { it.iconName == name }
        }
    }
}

enum class ColorOption(val colorName: String, val color: Color) {
    White("white", Color.White),
    Black("black", Color.Black);

    companion object {
        fun fromName(name: String): ColorOption? {
            return entries.find { it.colorName == name }
        }
    }
}

class Converter {

    //LocalDateTime Converter
    @TypeConverter
    fun fromTimestamp(value: Long): LocalDateTime {
        return value.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime): Long {
        return date.toEpochSecond(ZoneOffset.UTC)
    }

    //IconOption Converter
    @TypeConverter
    fun fromIconOption(iconOption: IconOption): String {
        return iconOption.iconName
    }
    @TypeConverter
    fun toIconOption(iconName: String): IconOption? {
        Icons.Filled.Android.name
        return IconOption.fromName(iconName)
    }

    //Color Converter
    @TypeConverter
    fun fromColor(color: Color): Long {
        return color.value.toLong()
    }
    @TypeConverter
    fun toColor(colorLong: Long): Color {
        return Color(colorLong)
    }
}