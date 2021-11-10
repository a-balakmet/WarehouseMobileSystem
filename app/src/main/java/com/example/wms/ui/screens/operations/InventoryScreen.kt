package com.example.wms.ui.screens.operations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.wms.R
import com.example.wms.app.viewModels.OperationalViewModel
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.room.entities.UserInfo
import com.example.wms.ui.components.*
import com.example.wms.ui.navigation.Screen
import com.example.wms.ui.screens.AskComparison
import com.example.wms.ui.screens.AskNewComparison
import com.example.wms.ui.screens.NoComparisonDialog
import com.example.wms.ui.screens.bottomDialogs.PalletDataShow
import com.example.wms.ui.screens.bottomDialogs.PalletsOfCell
import com.example.wms.ui.screens.bottomDialogs.ProductsToCompare
import com.example.wms.ui.theme.getPrimaryColor

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun InventoryScreen(navController: NavController, mainViewModel: MainViewModel, userInfo: UserInfo) {
    val operationViewModel = hiltViewModel<OperationalViewModel>()
    operationViewModel.user = userInfo
    val theMessage by operationViewModel.theMessage.collectAsState()
    val (isChecked, setChecked) = remember { mutableStateOf(false) }
    val (showNoCompareDialog, setShowNoCompareDialog) = remember { mutableStateOf(false) }
    val (showCompareDialog, setShowCompareDialog) = remember { mutableStateOf(false) }
    val (showUnknownProductDialog, setShowUnknownProductsDialog) = remember { mutableStateOf(false) }
    val (showUploadingErrorDialog, setShowUploadingErrorDialog) = remember { mutableStateOf(false) }
    val comparisonStatus by operationViewModel.startedComparison.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    operationViewModel.initOperationData()
    BackHandler(onBack = {
        operationViewModel.clearOnExit()
        navController.popBackStack()
    })
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .background(getPrimaryColor())
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                IconButton(onClick = {
                    operationViewModel.clearOnExit()
                    navController.popBackStack()
                }) {
                    BackIcon()
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1F)
                ) {
                    TitleText(text = stringResource(id = R.string.invent))
                }
                IconButton(onClick = { operationViewModel.sendOperationResults() }) {
                    UploadIcon()
                }
            }
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "${stringResource(id = R.string.pallets)}: ${operationViewModel.fullPalletsList.size}",
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            setChecked(!isChecked)
                        },
                    color = if (isChecked) Color.Black else getPrimaryColor(),
                    textAlign = TextAlign.Center
                )
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.cells)}: ${operationViewModel.cellsList.size}",
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            setChecked(!isChecked)
                        },
                    color = if (isChecked) getPrimaryColor() else Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            Divider()
            LazyColumn(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
                    .weight(2F),
                state = listState
            ) {
                if (!isChecked) {
                    val groupedPalletsList = operationViewModel.fullPalletsList.groupBy { it.cellTo!!.cellName }
                    groupedPalletsList.forEach { (location, palletData) ->
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = location,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .align(alignment = Alignment.Center)
                                )
                            }
                        }
                        items(palletData) { pData ->
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        operationViewModel.deletePalletData(palletData = pData)
                                    }
                                    true
                                }
                            )
                            SwipeToDismiss(state = dismissState,
                                background = {
                                    val color = when (dismissState.dismissDirection) {
                                        DismissDirection.StartToEnd -> Color.Transparent
                                        DismissDirection.EndToStart -> Color.Red
                                        null -> Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(color)
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteForever,
                                            contentDescription = "delete",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .align(alignment = Alignment.CenterEnd)
                                                .padding(end = 8.dp)
                                        )
                                    }
                                },
                                dismissContent = {
                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            operationViewModel.getPalletData(barcode = pData.palletBarcode)
                                        }
                                        .background(Color.White)
                                    ) {
                                        Text(
                                            text = pData.palletBarcode,
                                            modifier = Modifier.padding(all = 8.dp),
                                        )
                                        Divider()
                                    }
                                }
                            )
                        }
                    }
                } else {
                    items(operationViewModel.cellsList) { cell ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = {
                                if (it == DismissValue.DismissedToStart) {
                                    operationViewModel.deleteCellWithPallets(cellCode = cell)
                                }
                                true
                            }
                        )
                        SwipeToDismiss(state = dismissState,
                            background = {
                                val color = when (dismissState.dismissDirection) {
                                    DismissDirection.StartToEnd -> Color.Transparent
                                    DismissDirection.EndToStart -> Color.Red
                                    null -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteForever,
                                        contentDescription = "delete",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(alignment = Alignment.CenterEnd)
                                            .padding(end = 8.dp)
                                    )
                                }
                            },
                            dismissContent = {
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        operationViewModel.getPalletsInCell(cellCode = cell)
                                    }
                                    .background(Color.White)
                                ) {
                                    Text(
                                        text = cell,
                                        modifier = Modifier.padding(all = 8.dp),
                                    )
                                    Divider()
                                }
                            }
                        )
                    }
                }
            }
            theMessage?.let {
                when (it) {
                    "XXXXXXXXXX" -> setShowCompareDialog(true)
                    else -> {
                        Text(
                            color = Color.White,
                            fontSize = 19.sp,
                            text = it,
                            modifier = Modifier
                                .background(getPrimaryColor())
                                .fillMaxWidth()
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        when (it) {
                            stringResource(id = R.string.upload_success) -> coroutineScope.launch { navController.popBackStack(Screen.Shift.route, false) }
                            stringResource(id = R.string.upload_false), stringResource(id = R.string.no_server_connection) -> {
                                setShowUploadingErrorDialog(true)
                            }
                        }
                        /*if (it == stringResource(id = R.string.upload_success)) {
                            coroutineScope.launch { navController.popBackStack(Screen.Shift.route, false) }
                        } */
                    }
                }
            }
        }
        operationViewModel.aPallet.value?.let {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable {
                    operationViewModel.aPallet.value = null
                }) {
                PalletDataShow(pallet = it)
            }
        }
        operationViewModel.palletsInCell.value?.let {
            operationViewModel.aCell?.let { aCell ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        operationViewModel.palletsInCell.value = null
                    }) {
                    PalletsOfCell(cellName = aCell.cellName, palletsBarcodes = it)
                }
            }
        }
        operationViewModel.products2compare.value?.let {
            Box(modifier = Modifier.fillMaxSize()) {
                ProductsToCompare(product = operationViewModel.aProduct, productsList = it, operationViewModel = operationViewModel)
            }
        }
    }
    mainViewModel.theBarcode.value?.let {
        if (it.isNotEmpty()) {
            if (!comparisonStatus) {
                when {
                    it.startsWith("02") -> {
                        operationViewModel.getCell(barcode = it)
                        operationViewModel.aCell?.let {
                            operationViewModel.aPallet.value = null
                        }
                    }
                    it.startsWith("08") -> {
                        if (operationViewModel.aCell != null) {
                            operationViewModel.getPalletData(barcode = it)
                            if (!isChecked) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index = operationViewModel.fullPalletsList.size)
                                }
                            }
                        } else {
                            mainViewModel.playSound(R.raw.error)
                        }
                    }
                    else -> {
                        operationViewModel.aPallet.value = null
                        setShowNoCompareDialog(true)
                    }
                }
            } else {
                when {
                    it.startsWith("02") -> mainViewModel.playSound(R.raw.error)
                    it.startsWith("08") -> mainViewModel.playSound(R.raw.error)
                    else -> operationViewModel.getProductData(barcode = it)
                }
            }
        }
        mainViewModel.theBarcode.value = null
    }
    NoComparisonDialog(showDialog = showNoCompareDialog, setShowDialog = setShowNoCompareDialog)
    AskComparison(showDialog = showCompareDialog, setShowDialog = setShowCompareDialog, operationViewModel = operationViewModel)
    AskNewComparison(showDialog = showUnknownProductDialog, setShowDialog = setShowUnknownProductsDialog, operationViewModel = operationViewModel)
    UploadErrorDialog(showDialog = showUploadingErrorDialog, setShowDialog = setShowUploadingErrorDialog, operationViewModel = operationViewModel)
}