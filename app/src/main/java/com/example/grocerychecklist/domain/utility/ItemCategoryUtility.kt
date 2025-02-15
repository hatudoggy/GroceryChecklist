package com.example.grocerychecklist.domain.utility

class ItemCategoryUtility {
    companion object {
        fun getItemCategoryFromString(categoryText: String): ItemCategory? {
            return ItemCategory.entries.firstOrNull {
                it.text.equals(
                    categoryText,
                    ignoreCase = true
                )
            }
        }
    }
}