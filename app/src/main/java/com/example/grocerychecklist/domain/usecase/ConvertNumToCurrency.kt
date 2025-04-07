package com.example.grocerychecklist.domain.usecase

import java.util.Locale

enum class Currency {
    USD,
    PHP,
    YEN,
    EUR
}

class ConvertNumToCurrency {
    operator fun invoke(currency: Currency, price: Double, showDecimal: Boolean? = true, showZeroDecimal: Boolean = true): String {
        return when (currency) {
            Currency.USD -> formatPrice(price, "$", showDecimal, showZeroDecimal)
            Currency.PHP -> formatPrice(price, "₱", showDecimal, showZeroDecimal)
            Currency.YEN -> formatPrice(price, "¥", showDecimal, showZeroDecimal)
            Currency.EUR -> formatPrice(price, "€", showDecimal, showZeroDecimal)
        }
    }

    private fun formatPrice(price: Double, symbol: String, showDecimal: Boolean?, showZeroDecimal: Boolean?): String {
        return when {
            showDecimal == true && showZeroDecimal == true -> {
                if (price % 1 == 0.0)
                    "$symbol${price.toInt()}"
                else
                    "$symbol${String.format(Locale.US, "%.2f", price)}"
            }
            showDecimal == true -> "$symbol${String.format(Locale.US, "%.2f", price)}"
            else -> "$symbol${price.toInt()}"
        }
    }
}