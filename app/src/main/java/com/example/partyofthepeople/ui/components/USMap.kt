package com.example.partyofthepeople.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun USMap(
    modifier: Modifier = Modifier,
    onStateSelected: (String) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotationChange ->
                    scale *= zoom
                    rotation += rotationChange
                    offset += pan
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw map background
            drawRect(
                color = Color(0xFF457B9D),
                size = size
            )
            
            // Apply transformations
            withTransform({
                rotate(rotation, Offset(size.width / 2, size.height / 2))
                scale(scale, scale, Offset(size.width / 2, size.height / 2))
                translate(offset)
            }) {
                // Draw state boundaries
                drawStateBoundaries()
            }
        }
    }
}

private fun DrawScope.drawStateBoundaries() {
    // TODO: Implement state boundary drawing using SVG paths
    // For now, draw a placeholder rectangle
    drawRect(
        color = Color(0xFF2A4A73),
        topLeft = Offset(100f, 100f),
        size = androidx.compose.ui.geometry.Size(200f, 200f),
        style = Stroke(width = 2.dp.toPx())
    )
}

// State data class to hold state information
data class StateData(
    val name: String,
    val path: Path,
    val color: Color = Color(0xFF2A4A73),
    val isSelected: Boolean = false
)

// TODO: Add state path data
private val statePaths = mapOf<String, Path>() 