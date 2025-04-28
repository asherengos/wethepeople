package com.example.wethepeople.data.model

import androidx.compose.ui.geometry.Offset

// Represents a draggable accessory for Eagly
// id: unique string, resId: drawable resource, offset: position, scale: size

data class Accessory(
    val id: String,
    val resId: Int,
    var offset: Offset = Offset(0f, 0f),
    var scale: Float = 1.0f,
    var initialSize: Int = 80 // Default size in dp
) 