package com.example.wms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.example.wms.R
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.components.*
import com.example.wms.ui.navigation.Screen
import com.example.wms.ui.screens.bottomDialogs.TasksLoading
import com.example.wms.ui.theme.getPrimaryColor

@Composable
fun ShiftScreen(navController: NavController, mainViewModel: MainViewModel) {
    val (showTaskDialog, setShowTaskDialog) = remember { mutableStateOf(false) }
    BackHandler {
        navController.popBackStack()
        mainViewModel.userName.value = ""
        mainViewModel.userInfo.value = null
    }
    Column(modifier = Modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .background(getPrimaryColor())
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            IconButton(onClick = {
                navController.popBackStack()
                mainViewModel.userName.value = ""
                mainViewModel.userInfo.value = null
            }) {
                BackIcon()
            }
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                TitleText(text = stringResource(id = R.string.shift))
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            mainViewModel.userInfo.value?.let {
                Text(
                    text = it.personName,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                DefaultButtonStyle {
                    mainViewModel.userName.value.let {
                        Button(
                            onClick = { mainViewModel.theBarcode.value = "1503456782" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            enabled = it.length > 1,
                            elevation = buttonShadow()
                        ) {
                            HorizontalButtonWithIcon(
                                icon = R.drawable.loading_s,
                                text = R.string.movement
                            )
                        }
                        Button(
                            onClick = { mainViewModel.theBarcode.value = "1508762345" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            enabled = it.length > 1,
                            elevation = buttonShadow()
                        ) {
                            HorizontalButtonWithIcon(
                                icon = R.drawable.questionnaire_s,
                                text = R.string.invent
                            )
                        }
                        Button(
                            onClick = { mainViewModel.theBarcode.value = "1509823456" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            enabled = it.length > 1,
                            elevation = buttonShadow()
                        ) {
                            HorizontalButtonWithIcon(
                                icon = R.drawable.warehouse_s,
                                text = R.string.comparison_b
                            )
                        }
                        Button(
                            onClick = { mainViewModel.theBarcode.value = "1507638405" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            enabled = it.length > 1,
                            elevation = buttonShadow()
                        ) {
                            HorizontalButtonWithIcon(
                                icon = R.drawable.stock,
                                text = R.string.comparison_s
                            )
                        }
                        Button(
                            onClick = {
                                if (mainViewModel.userName.value == "") {
                                    mainViewModel.playSound(R.raw.error)
                                } else {
                                    val userData = Gson().toJson(mainViewModel.userInfo.value)
                                    val oType = -1
                                    navController.navigate(Screen.CheckPallet.route + "/$userData/$oType")
                                    mainViewModel.theBarcode.value = null
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            enabled = it.length > 1,
                            elevation = buttonShadow()
                        ) {
                            HorizontalButtonWithIcon(
                                icon = R.drawable.ic_search,
                                text = R.string.check_pallet
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .background(getPrimaryColor())
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                mainViewModel.messageToShow.value?.let { CenteredWhiteTextString(text = it) }
            }
        }
    }
    mainViewModel.theBarcode.value?.let {
        if (it.startsWith("03")) {
            mainViewModel.findUser(barcode = it)
        } else {
            when (it) {
                "1503456782" -> {
                    mainViewModel.loadTasks()
                    setShowTaskDialog(true)
                }
                "1508762345" -> {
                    if (mainViewModel.userName.value == "") {
                        mainViewModel.playSound(R.raw.error)
                    } else {
                        val userData = Gson().toJson(mainViewModel.userInfo.value)
                        val oType = 4
                        navController.navigate(Screen.Inventory.route + "/$userData/$oType")
                        mainViewModel.theBarcode.value = null
                    }
                }
                "1509823456" -> {
                    if (mainViewModel.userName.value == "") {
                        mainViewModel.playSound(R.raw.error)
                    } else {
                        val userData = Gson().toJson(mainViewModel.userInfo.value)
                        val oType = 0
                        navController.navigate(Screen.ComparisonB.route + "/$userData/$oType")
                        mainViewModel.theBarcode.value = null
                    }
                }
                "1507638405" -> {
                    if (mainViewModel.userName.value == "") {
                        mainViewModel.playSound(R.raw.error)
                    } else {
                        val userData = Gson().toJson(mainViewModel.userInfo.value)
                        val oType = 3
                        navController.navigate(Screen.ComparisonS.route + "/$userData/$oType")
                        mainViewModel.theBarcode.value = null
                    }
                }
                "1505623489" -> println("start adding, not applicable yet")
                "1599191919" -> {
                    navController.popBackStack()
                    mainViewModel.userName.value = ""
                    mainViewModel.userInfo.value = null
                }
            }
        }
        mainViewModel.theBarcode.value = null
    }
    mainViewModel.task.value?.let{ aTask->
        setShowTaskDialog(false)
        aTask.ProductToMove.removeIf{
            it.productName.contains("Паллет")
        }
        val userData = Gson().toJson(mainViewModel.userInfo.value)
        val task = Gson().toJson(aTask)
        val oType = 1
        navController.navigate(Screen.Movement.route + "/$userData/$oType/$task")
        mainViewModel.movementTasks.value = null
        mainViewModel.task.value = null
    }
    TasksLoading(showDialog = showTaskDialog, setShowDialog = setShowTaskDialog, mainViewModel = mainViewModel)
}