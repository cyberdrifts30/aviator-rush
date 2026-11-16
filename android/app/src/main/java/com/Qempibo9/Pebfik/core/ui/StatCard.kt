package com.Qempibo9.Pebfik.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.TextSecondary

data class StatCardData(
    val labelRes: Int,
    val value: String,
    val accent: Color = Accent
)

@Composable
fun StatCard(
    data: StatCardData,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(SurfaceLow.copy(alpha = 0.82f), shape)
            .border(1.dp, data.accent.copy(alpha = 0.35f), shape)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .background(data.accent, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = data.value,
            style = MaterialTheme.typography.titleMedium,
            color = data.accent,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(data.labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
