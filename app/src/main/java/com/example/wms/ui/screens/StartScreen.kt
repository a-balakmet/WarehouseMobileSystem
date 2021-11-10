package com.example.wms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wms.R
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.components.*
import com.example.wms.ui.navigation.Screen
import com.example.wms.ui.theme.getPrimaryColor

@Composable
fun StartScreen(navController: NavController, mainViewModel: MainViewModel) {
    BackHandler(onBack = {
        mainViewModel.closeApp()
    })
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(
                modifier = Modifier
                    .background(getPrimaryColor())
                    .fillMaxWidth()
                    .padding(all = 16.dp)
            ) {
                TitleText(text = "${stringResource(id = R.string.app_name)} - ${stringResource(id = R.string.app_ver)}")
            }
            Box(modifier = Modifier.fillMaxSize()) {
                mainViewModel.userInfo.value?.let {
                    Text(
                        text = it.deviceID,
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
                        Button(
                            onClick = { mainViewModel.theBarcode.value = "1500101010" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = buttonShadow()
                        ) {
                            VerticalButtonWithImage(image = R.drawable.ic_scanner, text = R.string.begin)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.Settings.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = buttonShadow()
                        ) {
                            VerticalButtonWithImage(image = R.drawable.ic_settings, text = R.string.settings)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.BarcodeCheck.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = buttonShadow()
                        ) {
                            VerticalButtonWithImage(image = R.drawable.ic_barcode, text = R.string.check_barcode)
                        }
                    }
                }
                mainViewModel.userInfo.value?.let {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
    mainViewModel.theBarcode.value?.let {
        if (it == "1500101010") {
            mainViewModel.userInfo.value = null
            mainViewModel.theBarcode.value = null
            mainViewModel.playSound(R.raw.info)
            mainViewModel.messageToShow.value = stringResource(id = R.string.scan_person_barcode)
            navController.navigate(Screen.Shift.route)
        }
    }
}



