package com.Qempibo9.Pebfik.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.Primary
import com.Qempibo9.Pebfik.core.theme.PrimaryBright
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.TextMuted
import com.Qempibo9.Pebfik.core.theme.TextPrimary

@Composable
fun PrimaryButton(
    textRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 60.dp,
    cornerRadius: Dp = 20.dp,
    gradient: List<Color> = listOf(Primary, PrimaryBright),
    contentColor: Color = TextPrimary,
    borderColor: Color = Accent.copy(alpha = 0.55f),
    shadowColor: Color = Primary.copy(alpha = 0.5f),
    leadingIcon: Int? = null,
    caption: String? = null,
    captionRes: Int? = null,
    textStyle: TextStyle? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        label = PRESS_LABEL
    )
    val shape = RoundedCornerShape(cornerRadius)
    val activeGradient = if (enabled) gradient else listOf(SurfaceHigh, SurfaceHigh)
    val activeContent = if (enabled) contentColor else TextMuted

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = activeContent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextMuted
        ),
        elevation = null,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 20.dp else 0.dp,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .background(Brush.linearGradient(activeGradient), shape)
            .border(1.5.dp, if (enabled) borderColor else SurfaceHigh, shape)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Image(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(textRes),
                    style = textStyle ?: MaterialTheme.typography.labelLarge,
                    color = activeContent,
                    textAlign = TextAlign.Center
                )
                val captionText = caption ?: captionRes?.let { stringResource(it) }
                if (captionText != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = captionText,
                        style = MaterialTheme.typography.labelSmall,
                        color = activeContent.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private const val PRESS_LABEL = "press-scale"
