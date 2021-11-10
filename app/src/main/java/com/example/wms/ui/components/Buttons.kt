package com.example.wms.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wms.ui.theme.getAccentColor

@Composable
fun DefaultButtonStyle(content: @Composable () -> Unit) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(small = RoundedCornerShape(9.dp)),
        typography = MaterialTheme.typography.copy(
            button = MaterialTheme.typography.button.merge(TextStyle(fontSize = 17.sp))
        ),
        colors = MaterialTheme.colors.copy(
            primary = Color.LightGray,
            onPrimary = getAccentColor(),
            secondary = Color.DarkGray
        ),
    ) {
        content()
    }
}

@Composable
fun VerticalButtonWithImage(image: Int, text: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painterResource(image), contentDescription = "")
        Text(
            text = stringResource(id = text)
                .uppercase(),
            style = TextStyle(fontWeight = FontWeight.Bold)

        )
    }
}

@Composable
fun VerticalButtonWithIcon(icon: ImageVector, text: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = stringResource(id = text)
                .uppercase(),
            style = TextStyle(fontWeight = FontWeight.Bold)

        )
    }
}

@Composable
fun HorizontalButtonWithIcon(icon: Int, text: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(icon), contentDescription = "", modifier = Modifier.padding(end = 8.dp))
        Column {
            Text(
                text = stringResource(id = text).uppercase(),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center

            )
        }
    }
}

@Composable
fun TextButton(text: Int) {
    Text(
        text = stringResource(id = text)
            .uppercase(),
        style = TextStyle(fontWeight = FontWeight.Bold)

    )
}

/*@Composable
fun TogglingButton(
    isChecked: Boolean,
    textActive: Int,
    textPassive: Int,
    iconActive: ImageVector,
    iconPassive: ImageVector,
    onClick: () -> Unit
) {
    Row {
        Text(
            text = if (isChecked) stringResource(id = textActive) else stringResource(id = textPassive),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 3.dp)
        )
        Box(
            modifier = Modifier
                .size(17.dp)
                .background(Color(0x1A000000))
                .align(Alignment.CenterVertically)
        ) {
            IconToggleButton(checked = isChecked, onCheckedChange = { onClick() }) {
                Icon(
                    imageVector = if (isChecked) iconActive else iconPassive,
                    contentDescription = "toggle",
                    modifier = Modifier.size(15.dp).align(Alignment.Center)
                )
            }
        }
    }

}*/

@Composable
fun buttonShadow() = ButtonDefaults.elevation(
    defaultElevation = 8.dp,
    pressedElevation = 4.dp,
    disabledElevation = 0.dp
)