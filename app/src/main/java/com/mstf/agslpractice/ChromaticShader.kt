package com.mstf.agslpractice

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mstf.agslpractice.ui.theme.AGSLPracticeTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
private const val SHADER = """
    uniform shader composable;
    
    half4 main(float2 fragCoord) {
        float displacement = 20.0;
        half3 colors = composable.eval(fragCoord).rgb;
        colors.r = composable.eval(float2(fragCoord.x - displacement, fragCoord.y)).r;
        colors.b = composable.eval(float2(fragCoord.x + displacement, fragCoord.y)).b;
        return half4(colors, 1.0);
    }
"""

private val chromaticShader = RuntimeShader(SHADER)

@Composable
fun ChromaticShader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    clip = true
                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(chromaticShader, "composable")
                        .asComposeRenderEffect()
                }
                .fillMaxWidth(.9f)
                .aspectRatio(9 / 16f),
            painter = painterResource(id = R.drawable.dog),
            contentDescription = null,
        )
    }
    // DraggableCircle()
}

@Composable
fun DraggableCircle() {
    var touchPosition by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // Wait for the first touch (finger down)
                        val down = awaitFirstDown()
                        touchPosition = down.position

                        // Track finger movement until released
                        drag(down.id) { change ->
                            touchPosition = change.position
                            change.consume()
                        }

                        // When released
                        touchPosition = null
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            touchPosition?.let { position ->
                drawCircle(
                    color = Color.Red,
                    radius = 80f,
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