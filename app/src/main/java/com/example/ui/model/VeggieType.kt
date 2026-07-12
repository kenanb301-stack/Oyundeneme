package com.example.ui.model

import androidx.compose.ui.graphics.Color

enum class VeggieType(val emoji: String, val TurkishName: String, val cardBgColor: Color) {
    CARROT("🥕", "Havuç", Color(0xFFFFE6D5)),
    TOMATO("🍅", "Domates", Color(0xFFFFEAEA)),
    BROCCOLI("🥦", "Brokoli", Color(0xFFE2F4E2)),
    EGGPLANT("🍆", "Patlıcan", Color(0xFFF3E5F5)),
    CORN("🌽", "Mısır", Color(0xFFFFF9C4)),
    POTATO("🥔", "Patates", Color(0xFFEFEBE9)),
    PUMPKIN("🎃", "Balkabağı", Color(0xFFFFF3E0)),
    GARLIC("🧄", "Sarımsak", Color(0xFFECEFF1)),
    ONION("🧅", "Soğan", Color(0xFFF5E6CC)),
    PEPPER("🫑", "Biber", Color(0xFFE8F5E9)),
    MUSHROOM("🍄", "Mantar", Color(0xFFFFEBEE)),
    AVOCADO("🥑", "Avokado", Color(0xFFF1F8E9))
}
