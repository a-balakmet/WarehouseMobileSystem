package com.example.wms.ui.screens.bottomDialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wms.R
import com.example.wms.app.models.PalletData
import com.example.wms.room.entities.Cells
import com.example.wms.ui.components.TwoTableCells
import com.example.wms.ui.theme.Shapes
import com.example.wms.ui.theme.getBackgroundColor

@Composable
fun PalletDataShow(pallet: PalletData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3F))
    ) {
        Box(
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
            Image(
                imageVector = Icons.Filled.Close,
                contentDescription = "close",
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(all = 8.dp)
                    .clip(shape = Shapes.small)
                    .background(Color.Black.copy(alpha = 0.1F))
                    .size(size = 20.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                TwoTableCells(text1 = stringResource(id = R.string.pallet), text2 = pallet.palletBarcode)
                TwoTableCells(text1 = stringResource(id = R.string.article), text2 = pallet.productArticle)
                TwoTableCells(text1 = stringResource(id = R.string.goods), text2 = pallet.productName)
                TwoTableCells(text1 = stringResource(id = R.string.series), text2 = pallet.productSeries)
                TwoTableCells(text1 = stringResource(id = R.string.date), text2 = pallet.productDate)
                pallet.cellFrom?.let {
                    TwoTableCells(text1 = "◄ ${stringResource(id = R.string.cell)}", text2 = it.cellName)
                }
                pallet.cellTo?.let {
                    TwoTableCells(text1 = "► ${stringResource(id = R.string.cell)}", text2 = it.cellName)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PalletPreview() {
    val aPalletData = PalletData(
        index = 1,
        palletBarcode = "00001",
        productArticle = "00001",
        productName = "sample name",
        productSeries = "A00A",
        productDate = "01.01.2001",
        productQuantity = "10",
        cellFrom = Cells("785", "name from", "description"),
        cellTo = Cells("452", "name to", "description"),
        operationID = "123"
    )
    PalletDataShow(pallet = aPalletData)
}