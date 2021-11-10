package com.example.wms.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.wms.R
import com.example.wms.app.models.PalletData
import com.example.wms.app.models.ProductSeries
import com.example.wms.app.models.ProductToMove
import com.example.wms.app.models.StorekeeperTask
import com.example.wms.app.repositories.ConvertorsRepository.dateConverter
import com.example.wms.app.viewModels.OperationalViewModel
import kotlin.math.roundToInt

@Composable
fun BarcodesItemList(barcode: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 5.dp
    ) {
        Text(
            text = barcode,
            modifier = Modifier.padding(all = 8.dp)
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun PalletIItemListSwipe(pallet: PalletData, viewModel: OperationalViewModel) {
    val squareSize = 50.dp
    val swipeState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                scope.launch { swipeState.animateTo(0) }
            }
            .padding(4.dp),
        elevation = 5.dp
    ) {
        Box(
            modifier = Modifier.swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal,
                reverseDirection = true
            ),
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterEnd)
                    .clickable {
                        scope.launch { swipeState.animateTo(0) }
                        viewModel.deletePalletData(palletData = pallet)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .size(squareSize - 6.dp)
                )
            }
            Box(modifier = Modifier
                .offset { -IntOffset(swipeState.offset.value.roundToInt(), 0) }
            ) {
                PalletItem(pallet = pallet)
            }
        }
    }
}

@Composable
fun PalletItem(pallet: PalletData) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, end = 0.dp, bottom = 8.dp)
            .background(Color.White)
    ) {
        TwoTableCells(text1 = stringResource(id = R.string.pallet), text2 = pallet.palletBarcode)
        TwoTableCells(text1 = stringResource(id = R.string.article), text2 = pallet.productArticle)
        TwoTableCells(text1 = stringResource(id = R.string.goods), text2 = pallet.productName)
        TwoTableCells(text1 = stringResource(id = R.string.series), text2 = pallet.productSeries)
        TwoTableCells(text1 = stringResource(id = R.string.date), text2 = pallet.productDate)
        //TwoTableCells(text1 = stringResource(id = R.string.cell), text2 = pallet.palletLocation)
    }
}

@Composable
fun PalletMoved(pallet: PalletData) {
    Column(modifier = Modifier.padding(8.dp)) {
        TwoTableCells(text1 = stringResource(id = R.string.pallet), text2 = pallet.palletBarcode)
        pallet.cellTo?.let {
            TwoTableCells(text1 = stringResource(id = R.string.to), text2 = it.cellName)
        }
    }
}

@Composable
fun ProductForComparison(product: ProductSeries) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 0.dp, bottom = 8.dp)
            .background(Color.White)
    ) {
        TwoTableCells(text1 = stringResource(id = R.string.series), text2 = "0${product.seriesNo}")
        TwoTableCells(text1 = stringResource(id = R.string.date), text2 = dateConverter(product.productionDate).substring(0 ,10))
    }
}

@Composable
fun TaskItem(task: StorekeeperTask){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 0.dp, bottom = 8.dp)
                .background(Color.White)
        ) {
            ThreeTableCells(text1 = stringResource(id = R.string.task), text2 = task.number)
        }
    }
}

@Composable
fun ProductToMoveItem(product: ProductToMove){
    TwoTableCells(text1 = stringResource(id = R.string.article), text2 = product.productArticle)
    TwoTableCells(text1 = stringResource(id = R.string.goods), text2 = product.productName)
    TwoTableCells(text1 = stringResource(id = R.string.series), text2 = product.productSeries)
    TwoTableCells(text1 = stringResource(id = R.string.cell), text2 = product.cellName)
    TwoTableCells(text1 = "${stringResource(id = R.string.quantity)}:", text2 = product.packsQuantity.toString())
    Divider()
}
