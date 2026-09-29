package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun IncognitoSpyIcon(
    modifier: Modifier = Modifier.size(64.dp),
    tint: Color = Color(0xFF212121)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = (w.coerceAtMost(h) / 2f) * 0.95f

        // Outer circle
        drawCircle(
            color = tint,
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(width = w * 0.055f)
        )

        val strokeW = w * 0.05f

        // Fedora Crown (top hat)
        val crownPath = Path().apply {
            moveTo(cx - w * 0.22f, cy - h * 0.05f)
            lineTo(cx - w * 0.20f, cy - h * 0.32f)
            // top dent
            cubicTo(
                cx - w * 0.10f, cy - h * 0.28f,
                cx + w * 0.10f, cy - h * 0.28f,
                cx + w * 0.20f, cy - h * 0.32f
            )
            lineTo(cx + w * 0.22f, cy - h * 0.05f)
            close()
        }
        drawPath(path = crownPath, color = tint)

        // Hat Brim
        val brimWidth = w * 0.65f
        val brimHeight = h * 0.07f
        drawRoundRect(
            color = tint,
            topLeft = Offset(cx - brimWidth / 2f, cy - h * 0.07f),
            size = Size(brimWidth, brimHeight),
            cornerRadius = CornerRadius(brimHeight / 2f, brimHeight / 2f)
        )

        // Glasses (Left & Right Oval)
        val glassW = w * 0.19f
        val glassH = h * 0.15f
        val glassY = cy + h * 0.08f

        // Left lens
        drawOval(
            color = tint,
            topLeft = Offset(cx - glassW - w * 0.035f, glassY),
            size = Size(glassW, glassH),
            style = Stroke(width = strokeW)
        )

        // Right lens
        drawOval(
            color = tint,
            topLeft = Offset(cx + w * 0.035f, glassY),
            size = Size(glassW, glassH),
            style = Stroke(width = strokeW)
        )

        // Glasses bridge
        drawLine(
            color = tint,
            start = Offset(cx - w * 0.04f, glassY + glassH * 0.4f),
            end = Offset(cx + w * 0.04f, glassY + glassH * 0.4f),
            strokeWidth = strokeW
        )
    }
}
