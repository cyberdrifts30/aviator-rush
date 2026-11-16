package com.Qempibo9.Pebfik.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.theme.TextMuted

@Composable
fun LockedTile(
    requirementRes: Int,
    modifier: Modifier = Modifier,
    iconSize: Int = 20
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_lock),
            contentDescription = stringResource(R.string.locked_description),
            tint = TextMuted,
            modifier = Modifier.size(iconSize.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(requirementRes),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
