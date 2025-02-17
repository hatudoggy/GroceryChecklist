package com.example.grocerychecklist.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter
import com.example.grocerychecklist.ui.theme.*
import java.time.LocalDateTime
import java.time.ZoneOffset

enum class IconOption(val iconName: String, val imageVector: ImageVector) {
    Android("android", Icons.Filled.Android),
    Home("home", Icons.Filled.Home),
    MAIN_GROCERY("Main Grocery", Icons.Filled.ShoppingCart),
    FRUITS("Fruits", Icons.Filled.Eco),
    VEGETABLES("Vegetables", Icons.Filled.Grass),
    DAIRY("Dairy Products", Icons.Filled.LocalDrink),
    MEAT("Meat & Poultry", Icons.Filled.SetMeal),
    SEAFOOD("Seafood", Icons.Filled.Waves),
    BAKERY("Bakery & Bread", Icons.Filled.BakeryDining),
    BEVERAGES("Beverages", Icons.Filled.Coffee),
    SNACKS("Snacks", Icons.Filled.Fastfood),
    FROZEN_FOOD("Frozen Food", Icons.Filled.AcUnit),
    CLEANING_SUPPLIES("Cleaning Supplies", Icons.Filled.CleanHands),
    PAPER_GOODS("Paper Goods", Icons.Filled.ReceiptLong),
    PET_SUPPLIES("Pet Supplies", Icons.Filled.Pets),
    BABY_PRODUCTS("Baby Products", Icons.Filled.ChildCare),
    MEDICINE("Medicine", Icons.Filled.MedicalServices),
    VITAMINS("Vitamins & Supplements", Icons.Filled.LocalPharmacy),
    PERSONAL_CARE("Personal Care", Icons.Filled.FaceRetouchingNatural),
    SPICES("Spices & Condiments", Icons.Filled.Restaurant),
    CANNED_GOODS("Canned Goods", Icons.Filled.Kitchen),
    GRAINS("Grains & Pasta", Icons.Filled.DinnerDining),
    OILS("Cooking Oils", Icons.Filled.OilBarrel),
    SWEETS("Sweets & Desserts", Icons.Filled.Icecream),
    ALCOHOL("Alcoholic Beverages", Icons.Filled.Liquor),
    SPECIALTY_FOODS("Specialty Foods", Icons.Filled.EggAlt),
    ORGANIC_FOOD("Organic & Natural", Icons.Filled.Spa);

    companion object {
        fun fromName(name: String): IconOption? {
            return entries.find { it.iconName == name }
        }
    }
}

enum class ColorOption(val colorName: String, val color: Color) {
    White("white", Color.White),
    Black("black", Color.Black),
    CopySkyGreen("Sky Green", SkyGreen),
    CopyVibrantOrange("Vibrant Orange", VibrantOrange),
    CopyFreshGreen("Fresh Green", FreshGreen),
    CopyCreamyWhite("Creamy White", CreamyWhite),
    CopyBoldRed("Bold Red", BoldRed),
    CopyDeepBlue("Deep Blue", DeepBlue),
    CopyWarmBrown("Warm Brown", WarmBrown),
    CopyLightBlue("Light Blue", LightBlue),
    CopyGoldenYellow("Golden Yellow", GoldenYellow),
    CopyIcyBlue("Icy Blue", IcyBlue),
    CopyPureWhite("Pure White", PureWhite),
    CopyLightGray("Light Gray", LightGray),
    CopyFurryBrown("Furry Brown", FurryBrown),
    CopySoftPink("Soft Pink", SoftPink),
    CopyMaroon("Maroon", Maroon),
    CopyBrightYellow("Bright Yellow", BrightYellow),
    CopySoftPurple("Soft Purple", SoftPurple),
    CopyDarkOrange("Dark Orange", DarkOrange),
    CopySteelGray("Steel Gray", SteelGray),
    CopyWheatBrown("Wheat Brown", WheatBrown),
    CopySmoothGold("Smooth Gold", SmoothGold),
    CopyCherryPink("Cherry Pink", CherryPink),
    CopyDarkGold("Dark Gold", DarkGold),
    CopyTeal("Teal", Teal),
    CopyLeafyGreen("Leafy Green", LeafyGreen);

    companion object {
        fun fromName(name: String): ColorOption? {
            return entries.find { it.colorName == name }
        }

        fun fromColor(color: Color): ColorOption? {
            return entries.find {
                it.color == color
            }
        }
    }
}

class Converter {

    //CopyDateTime Converter
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