package com.example.grocerychecklist.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.grocerychecklist.ui.theme.*

enum class ChecklistCategory(val color: Color, val text: String, val icon: ImageVector) {
    // General Grocery Categories
    MAIN_GROCERY(SkyGreen, "Main Grocery", Icons.Filled.ShoppingCart),
    FRUITS(VibrantOrange, "Fruits", Icons.Filled.Eco),
    VEGETABLES(FreshGreen, "Vegetables", Icons.Filled.Grass),
    DAIRY(CreamyWhite, "Dairy Products", Icons.Filled.LocalDrink),
    MEAT(BoldRed, "Meat & Poultry", Icons.Filled.SetMeal),
    SEAFOOD(DeepBlue, "Seafood", Icons.Filled.Waves),
    BAKERY(WarmBrown, "Bakery & Bread", Icons.Filled.BakeryDining),
    BEVERAGES(LightBlue, "Beverages", Icons.Filled.Coffee),
    SNACKS(GoldenYellow, "Snacks", Icons.Filled.Fastfood),
    FROZEN_FOOD(IcyBlue, "Frozen Food", Icons.Filled.AcUnit),

    // Household Essentials
    CLEANING_SUPPLIES(PureWhite, "Cleaning Supplies", Icons.Filled.CleanHands),
    PAPER_GOODS(LightGray, "Paper Goods", Icons.Filled.ReceiptLong),
    PET_SUPPLIES(FurryBrown, "Pet Supplies", Icons.Filled.Pets),
    BABY_PRODUCTS(SoftPink, "Baby Products", Icons.Filled.ChildCare),

    // Health & Wellness
    MEDICINE(Maroon, "Medicine", Icons.Filled.MedicalServices),
    VITAMINS(BrightYellow, "Vitamins & Supplements", Icons.Filled.LocalPharmacy),
    PERSONAL_CARE(SoftPurple, "Personal Care", Icons.Filled.FaceRetouchingNatural),

    // Cooking & Pantry Items
    SPICES(DarkOrange, "Spices & Condiments", Icons.Filled.Restaurant),
    CANNED_GOODS(SteelGray, "Canned Goods", Icons.Filled.Kitchen),
    GRAINS(WheatBrown, "Grains & Pasta", Icons.Filled.DinnerDining),
    OILS(SmoothGold, "Cooking Oils", Icons.Filled.OilBarrel),
    SWEETS(CherryPink, "Sweets & Desserts", Icons.Filled.Icecream),

    // Miscellaneous
    ALCOHOL(DarkGold, "Alcoholic Beverages", Icons.Filled.Liquor),
    SPECIALTY_FOODS(Teal, "Specialty Foods", Icons.Filled.EggAlt),
    ORGANIC_FOOD(LeafyGreen, "Organic & Natural", Icons.Filled.Spa)
}