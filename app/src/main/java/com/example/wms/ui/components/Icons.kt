package com.example.wms.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.twotone.Forward
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.wms.ui.theme.accentColorDark

@Composable
fun BackIcon() {
    Icon(
        Icons.Filled.ArrowBack,
        "back",
        tint = Color.White)
}

@Composable
fun UploadIcon() {
    Icon(
        Icons.Filled.CloudUpload,
        "upload",
        tint = Color.White)
}

@Composable
fun NextIcon() {
    Icon(
        Icons.TwoTone.Forward,
        "next",
        tint = accentColorDark
    )
}

@Preview
@Composable
fun IconPreview(){
    UploadIcon()
}