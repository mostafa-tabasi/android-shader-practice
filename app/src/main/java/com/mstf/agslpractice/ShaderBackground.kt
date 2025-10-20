package com.mstf.agslpractice

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import com.mstf.agslpractice.ui.theme.AGSLPracticeTheme

private const val SHADER = """
    uniform float2 size;
    
    half4 main(float2 fragCoord) {
        return half4((fragCoord / size).xy, 0.75, 1);
    }
"""

@Composable
fun ShaderBackground() {

    val shader = RuntimeShader(SHADER)
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                shader.setFloatUniform(
                    "size",
                    it.width.toFloat(),
                    it.height.toFloat(),
                )
            }
            .graphicsLayer {
                renderEffect =
                    RenderEffect.createShaderEffect(shader).asComposeRenderEffect()
            }
    ) {

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AGSLPracticeTheme {
        ShaderBackground()
    }
}