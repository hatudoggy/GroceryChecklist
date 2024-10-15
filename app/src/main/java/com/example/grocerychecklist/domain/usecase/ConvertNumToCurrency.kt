package com.example.grocerychecklist.domain.usecase

import java.util.Locale

enum class Currency {
    USD,
    PHP,
    YEN,
    EUR
}

class ConvertNumToCurrency {
    operator fun invoke(currency: Currency, price: Double, showDecimal: Boolean? = true): String {
        return when (currency) {
            Currency.USD -> formatPrice(price, "$", showDecimal)
            Currency.PHP -> formatPrice(price, "₱", showDecimal)
            Currency.YEN -> formatPrice(price, "¥", showDecimal)
            Currency.EUR -> formatPrice(price, "€", showDecimal)
        }
    }

    private fun formatPrice(price: Double, symbol: String, showDecimal: Boolean?): String {
        return if (showDecimal == true) {
            "$symbol${String.format(Locale.US,"%.2f", price)}"
        } else {
            "$symbol${price.toInt()}"
        }
    }
}