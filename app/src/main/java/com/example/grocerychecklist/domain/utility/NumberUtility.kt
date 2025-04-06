package com.example.grocerychecklist.domain.utility

import java.util.Locale

class NumberUtility {
    companion object {
        fun formatQuantity(quantity: Double, measurement: String): String {
            val formattedQuantity = if (quantity % 1.0 == 0.0) {
                quantity.toInt().toString()
            } else {
                String.format(Locale.US, "%.2f", quantity)
            }

            return "x $formattedQuantity $measurement"
        }
    }
}