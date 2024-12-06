package com.gswxxn.restoresplashscreen.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import kotlin.math.max

@Composable
fun HeaderCard(
    imageResID: Int,
    title: String,
    maxLines: Int = 1
) {
    val offset: Offset
    val blurRadius: Float
    with(LocalDensity.current) {
        offset = Offset(0f, 3.dp.toPx())
        blurRadius = 6.dp.toPx()
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 6.dp, top = 12.dp)
    ) {
        var textStyle by remember { mutableStateOf(
            TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.1f),
                    offset = offset,
                    blurRadius = blurRadius
                )
            )
        ) }
        var readyToDraw by remember { mutableStateOf(false) }
        Layout(
            content = {
                Image(
                    modifier = Modifier,
                    painter = painterResource(imageResID),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .drawWithContent {
                            if (readyToDraw) drawContent()
                        },
                    text = title,
                    textAlign = TextAlign.Center,
                    style = textStyle,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) {
                            textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                        } else {
                            readyToDraw = true
                        }
                    },
                    maxLines = maxLines
                )
            }
        ) { measurables, constraints ->
            if (measurables.size != 2) {
                layout(0, 0) { }
            }
            val px16dp = 16.dp.roundToPx()
            val px32dp = 32.dp.roundToPx()
            val px250dp = 250.dp.roundToPx()
            val image = measurables[0].measure(constraints.copy(
                minHeight = px250dp, maxHeight = px250dp
            ))
            val text = measurables[1].measure(constraints.copy(
                maxWidth = constraints.maxWidth - image.width - px16dp * 3
            ))
            val totalHeight = max(image.height, text.height) + px32dp
            layout(constraints.maxWidth, totalHeight) {
                image.place(px16dp, (totalHeight - image.height) / 2)
                val rightCenterX = px16dp + image.width + (constraints.maxWidth - px16dp - image.width) / 2
                text.place(
                    rightCenterX - text.width / 2,
                    (totalHeight - text.height) / 2
                )
            }
        }
    }
}