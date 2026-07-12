package com.example.ui.model

import java.util.UUID

data class VeggieItem(
    val id: String = UUID.randomUUID().toString(),
    val type: VeggieType,
    val xPercent: Float, // Relative positioning (0.00 to 1.00)
    val yPercent: Float, // Relative positioning (0.00 to 1.00)
    val rotation: Float, // Playful offset rotations
    val scale: Float = 1.0f,
    val depth: Int, // Layers (0 = bottom layer, larger = on top)
    var isCovered: Boolean = false // Set programmatically based on overlap algorithms
)
