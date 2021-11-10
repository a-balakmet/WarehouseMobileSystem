package com.example.wms.ui.screens.operations

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wms.R
import com.example.wms.app.viewModels.OperationalViewModel
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.room.entities.UserInfo
import com.example.wms.ui.components.*
import com.example.wms.ui.theme.getPrimaryColor

@ExperimentalMaterialApi
@Composable
fun CheckPalletScreen(navController: NavController, mainViewModel: MainViewModel, userInfo: UserInfo) {
    val operationViewModel = hiltViewModel<OperationalViewModel>()
    operationViewModel.user = userInfo
    val theMessage by operationViewModel.theMessage.collectAsState()
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
                    navController.popBackStack()
                }) {
                    BackIcon()
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    TitleText(text = stringResource(id = R.string.check_pallet))
                }
            }
            Box(modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxSize()
                .weight(2F)
                ) {
                ReorderableList(operationalViewModel = operationViewModel)
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
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
    mainViewModel.theBarcode.value?.let {
        if (it.startsWith("08")) {
            operationViewModel.getPalletData(it)
            mainViewModel.theBarcode.value = null
        }
    }
}