package com.mstf.agslpractice

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mstf.agslpractice.ui.theme.AGSLPracticeTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
private const val SHADER = """
    uniform shader composable;
    uniform float2 center;
    uniform float radius;
    uniform float hDisplacement;
    uniform float vDisplacement;
    uniform float enabled;

    half4 main(float2 fragCoord) {
        float dist = distance(fragCoord, center);
        half4 original = composable.eval(fragCoord);

        bool inside = (enabled > 0.5) ? (dist < radius) : true;

        if (inside) {
            half3 colors = original.rgb;
            colors.r = composable.eval(float2(fragCoord.x - hDisplacement, fragCoord.y - vDisplacement)).r;
            colors.b = composable.eval(float2(fragCoord.x + hDisplacement, fragCoord.y + vDisplacement)).b;
            return half4(colors, 1.0);
        } else {
            return original;
        }
    }
"""

private val chromaticShader = RuntimeShader(SHADER)

@Composable
fun ChromaticShader() {
    var hDisplacement by remember { mutableFloatStateOf(0f) }
    var vDisplacement by remember { mutableFloatStateOf(0f) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    var isCircleEnabled by remember { mutableStateOf(false) }
    var isShowingImage by remember { mutableStateOf(false) }
    val radius = 200f

    val chromaticShader = remember { RuntimeShader(SHADER) }

    // Recreate RenderEffect when something changes
    val renderEffect = remember(hDisplacement, vDisplacement, touchPosition, isCircleEnabled) {
        if (isCircleEnabled && touchPosition != null) {
            chromaticShader.setFloatUniform("center", touchPosition!!.x, touchPosition!!.y)
        } else {
            chromaticShader.setFloatUniform("center", -9999f, -9999f)
        }

        chromaticShader.setFloatUniform("radius", radius)
        chromaticShader.setFloatUniform("hDisplacement", hDisplacement)
        chromaticShader.setFloatUniform("vDisplacement", vDisplacement)
        chromaticShader.setFloatUniform("enabled", if (isCircleEnabled) 1f else 0f)

        RenderEffect
            .createRuntimeShaderEffect(chromaticShader, "composable")
            .asComposeRenderEffect()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box {
            if (!isShowingImage)
                Text(
                    text = "CHROMATIC\n\n\nSHADER\n\n\nEFFECT",
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .graphicsLayer {
                            clip = true
                            this.renderEffect = renderEffect
                        }
                )
            else
                Image(
                    modifier = Modifier
                        .graphicsLayer {
                            clip = true
                            this.renderEffect = renderEffect
                        }
                        .fillMaxWidth(0.9f)
                        .aspectRatio(9 / 13.4f),
                    painter = painterResource(id = R.drawable.dog),
                    contentDescription = null,
                )

            // Only show and enable circle when checkbox is checked
            if (isCircleEnabled) {
                DraggableCircle(
                    onTouchChange = { pos -> touchPosition = pos },
                    radius = radius
                )
            }
        }

        // Controls (bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Horizontal Displacement", style = TextStyle(color = White))
                Slider(
                    value = hDisplacement,
                    onValueChange = { hDisplacement = it },
                    valueRange = 0f..50f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Vertical Displacement", style = TextStyle(color = White))
                Slider(
                    value = vDisplacement,
                    onValueChange = { vDisplacement = it },
                    valueRange = 0f..50f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Checkbox(
                    checked = isCircleEnabled,
                    onCheckedChange = { isCircleEnabled = it }
                )
                Text("Enable draggable circle", style = TextStyle(color = White))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Checkbox(
                    checked = isShowingImage,
                    onCheckedChange = { isShowingImage = it }
                )
                Text("Apply on Image", style = TextStyle(color = White))
            }
        }
    }
}

@Composable
fun DraggableCircle(
    onTouchChange: (Offset?) -> Unit,
    radius: Float
) {
    var touchPosition by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        touchPosition = down.position
                        onTouchChange(down.position)

                        drag(down.id) { change ->
                            touchPosition = change.position
                            onTouchChange(change.position)
                            change.consume()
                        }

                        touchPosition = null
                        onTouchChange(null)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            touchPosition?.let { position ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = radius,
                    center = position
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChromaticShaderPreview() {
    AGSLPracticeTheme {
        ChromaticShader()
    }
}