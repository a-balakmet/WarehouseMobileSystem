package com.example.wms.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(text: String) {
    Text(
        color = Color.White,
        fontSize = 19.sp,
        text = text,
        textAlign = TextAlign.Center,
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun SubtitleText(text: String) {
    Text(
        color = Color.White,
        fontSize = 17.sp,
        text = text,
        textAlign = TextAlign.Center
    )
}

@Composable
fun CenteredTextInt(text: Int) {
    Text(
        text = stringResource(id = text),
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun CenteredWhiteTextString(text: String) {
    Text(
        color = Color.White,
        fontSize = 19.sp,
        text = text,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun SimpleTextInt(text: Int) {
    Text(
        text = stringResource(id = text),
        fontSize = 15.sp
    )
}

@Composable
fun CenteredTextString(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun CenteredBoldText(text: Int) {
    Text(
        text = stringResource(id = text),
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ButtonText(text: String) {
    Text(
        text = text,
        fontSize = 27.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun TwoTableCells(text1: String, text2: String) {
    Row (modifier = Modifier.fillMaxWidth()){
        Text(text = text1, modifier = Modifier.weight(0.5F), color = Color.Gray)
        Text(text = text2, modifier = Modifier.weight(2F))
    }
}
@Composable
fun ThreeTableCells(text1: String, text2: String) {
    Row (modifier = Modifier.fillMaxWidth()){
        Text(text = text1, modifier = Modifier.weight(0.5F), color = Color.Gray)
        Text(text = text2, modifier = Modifier.weight(2F).padding(horizontal = 4.dp))
        NextIcon()
    }
}



@Preview
@Composable
fun TextPreview(){
    //ButtonText("1")
    //CenteredBoldText(text = R.string.pallet_quantity)
    ThreeTableCells(text1 = "task", text2 = "04038239")
}