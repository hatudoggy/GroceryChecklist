package com.example.grocerychecklist.ui.component;

enum class Measurement(val singularText: String, val pluralText: String) {
    GRAM("g", "g"),
    KILOGRAM("kg", "kg"),
    DOZEN("dozen", "dozens"),
    PACK("pack", "packs"),
    PIECE("pc", "pcs"),
    MILLILITER("ml", "ml"),
    LITER("l", "l"),
    BOX("box", "boxes"),
    TABLET("tab", "tabs"),
    TUB("tub", "tubs"),
}

fun Measurement.getText(quantity: Double): String {
    return if (quantity == 1.00) singularText else pluralText
}