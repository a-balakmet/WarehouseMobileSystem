package com.example.wms.ui.screens.bottomDialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.wms.ui.components.BarcodesItemList
import com.example.wms.ui.theme.Shapes
import com.example.wms.ui.theme.getBackgroundColor

@Composable
fun PalletsOfCell(cellName: String, palletsBarcodes: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
                .background(getBackgroundColor())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = cellName,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = 8.dp)
                        .weight(1F)
                )
                Image(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close",
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .clip(shape = Shapes.small)
                        .background(Color.Black.copy(alpha = 0.1F))
                        .size(size = 20.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .wrapContentHeight()
            ) {
                items(items = palletsBarcodes) {
                    BarcodesItemList(barcode = it)
                }
            }
        }
    }
}