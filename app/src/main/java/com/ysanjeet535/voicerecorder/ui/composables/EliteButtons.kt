package com.ysanjeet535.voicerecorder.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ysanjeet535.voicerecorder.ui.theme.lightWhite
import me.nikhilchaudhari.library.NeuInsets
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.Pressed
import me.nikhilchaudhari.library.shapes.Punched

@Composable
fun EliteButtons(
    iconId: Int? = null,
    iconTint : Color = Color.Black,
    label: String = "Record",
    isPressed: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .wrapContentWidth(unbounded = false)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
            .background(lightWhite)
            .neumorphic(
                neuShape = if (isPressed) {
                    Pressed.Rounded(radius = 16.dp)
                } else {
                    Punched.Rounded(radius = 16.dp)
                },
                neuInsets = NeuInsets(horizontal = 8.dp, vertical = 8.dp)
            )
            .padding(8.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            if (iconId != null) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "record icon",
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = label, color = Color.Black, style = MaterialTheme.typography.button)
        }
    }
}