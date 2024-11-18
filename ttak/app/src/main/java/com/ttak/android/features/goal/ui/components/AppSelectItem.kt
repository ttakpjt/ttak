package com.ttak.android.features.goal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ttak.android.domain.model.AppInfo
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppSelectItem(
    app: AppInfo,
    isSelected: Boolean,
    onSelectApp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelectApp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App icon using DrawablePainter
        Image(
            painter = rememberDrawablePainter(drawable = app.icon),
            contentDescription = "${app.appName} icon",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // App name
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // Selection checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelectApp() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Blue,
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            )
        )
    }
}