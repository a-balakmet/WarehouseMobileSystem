package com.example.wms.ui.screens.operations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.app.viewModels.OperationalViewModel
import com.example.wms.room.entities.UserInfo
import com.example.wms.ui.components.BackHandler
import com.example.wms.ui.components.BackIcon
import com.example.wms.ui.components.TitleText
import com.example.wms.ui.components.UploadErrorDialog
import com.example.wms.ui.navigation.Screen
import com.example.wms.ui.screens.AskUploading
import com.example.wms.ui.screens.DropPartAtComparison
import com.example.wms.ui.screens.bottomDialogs.PalletsQuantityRequest
import com.example.wms.ui.theme.getPrimaryColor

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ComparisonBaiserke(navController: NavController, mainViewModel: MainViewModel, userInfo: UserInfo) {
    val operationViewModel = hiltViewModel<OperationalViewModel>()
    operationViewModel.user = userInfo
    val theMessage by operationViewModel.theMessage.collectAsState()
    val (showQuantityDialog, setShowQuantityDialog) = remember { mutableStateOf(false) }
    val (showAlertDialog, setShowAlertDialog) = remember { mutableStateOf(false) }
    val (showUploadDialog, setShowUploadDialog) = remember { mutableStateOf(false) }
    val (showUploadingErrorDialog, setShowUploadingErrorDialog) = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val part = stringResource(id = R.string.part)
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
                    TitleText(text = stringResource(id = R.string.comparison_b))
                }
            }
            operationViewModel.aCell.let {
                if (it != null) {
                    Text(
                        text = it.cellName,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                }
            }
            Divider()
            operationViewModel.partCode.value.let {
                if (it != "") {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = "$part ${operationViewModel.partCode.value}:")
                        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(text = operationViewModel.palletsList.size.toString())
                            Text(
                                text = "/",
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            operationViewModel.palletsRequestedQuantity.value?.let { request->
                                Text(text = request.toString())
                            }
                        }
                    }
                }
            }
            Divider()
            LazyColumn(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
                    .weight(2F),
                state = listState
            ) {
                items(operationViewModel.palletsList) { pallet ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                operationViewModel.deletePalletOfComparison(palletCode = pallet)
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
                                Text(
                                    text = pallet,
                                    modifier = Modifier.padding(all = 8.dp),
                                )
                                Divider()
                            }
                        }
                    )
                }
            }
            theMessage?.let {
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
    mainViewModel.theBarcode.value?.let {
        if (it.isNotEmpty()) {
            when {
                it.startsWith("02") -> {
                    operationViewModel.getCell(barcode = it)
                    operationViewModel.aCell?.let {
                        operationViewModel.aPallet.value = null
                    }
                }
                it.startsWith("07") -> {
                    if (operationViewModel.partCode.value == "") {
                        if (operationViewModel.aCell == null) {
                            mainViewModel.playSound(R.raw.error)
                        } else {
                            operationViewModel.partCode.value = it
                            setShowQuantityDialog(true)
                        }
                    } else {
                        setShowAlertDialog(true)
                    }
                }
                it.startsWith("08") -> {
                    if (operationViewModel.aCell != null) {
                        if (operationViewModel.partCode.value != "") {
                            operationViewModel.getPalletData(barcode = it)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index = operationViewModel.fullPalletsList.size)
                            }
                        } else mainViewModel.playSound(R.raw.error)
                    } else mainViewModel.playSound(R.raw.error)
                }
                else -> {
                    operationViewModel.aPallet.value = null
                }
            }
        }
        mainViewModel.theBarcode.value = null
    }
    operationViewModel.palletsRequestedQuantity.value?.let {
        if (it == operationViewModel.palletsList.size) {
            setShowUploadDialog(true)
        }
        if (it == -1) {
            operationViewModel.clearOnExit()
            coroutineScope.launch { navController.popBackStack(Screen.Shift.route, false)  }
        }
    }
    PalletsQuantityRequest(showDialog = showQuantityDialog, setShowDialog = setShowQuantityDialog, operationViewModel = operationViewModel)
    DropPartAtComparison(showDialog = showAlertDialog, setShowDialog = setShowAlertDialog, operationViewModel = operationViewModel)
    AskUploading(showDialog = showUploadDialog, setShowDialog = setShowUploadDialog, operationViewModel = operationViewModel)
    UploadErrorDialog(showDialog = showUploadingErrorDialog, setShowDialog = setShowUploadingErrorDialog, operationViewModel = operationViewModel)
}