package com.mstf.agslpractice

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mstf.agslpractice.ui.theme.AGSLPracticeTheme

private const val SHADER = """
    uniform float2 size;
    uniform shader composable;
    
    half4 main(float2 fragCoord) {
        return half4((fragCoord / size).xy, 0.75, composable.eval(fragCoord).a);
    }
"""

@Composable
fun ShaderText() {

    val shader = RuntimeShader(SHADER)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Hello World",
                fontSize = 60.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .onSizeChanged {
                        shader.setFloatUniform(
                            "size",
                            it.width.toFloat(),
                            it.height.toFloat(),
                        )
                    }
                    .graphicsLayer {
                        clip = true
                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(shader, "composable")
                            .asComposeRenderEffect()
                    },
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ShaderTextPreview() {
    AGSLPracticeTheme {
        ShaderText()
    }
}