package com.example.wms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wms.R
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.theme.getPrimaryColor
import com.example.wms.ui.components.*

@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = hiltViewModel<MainViewModel>()
    Column(modifier = Modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .background(getPrimaryColor())
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                BackIcon()
            }
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                TitleText(text = stringResource(id = R.string.settings))
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            DefaultButtonStyle {
                Button(
                    onClick = { viewModel.openNetworkSettings(isStart = false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = buttonShadow()
                ) {
                    VerticalButtonWithIcon(icon = Icons.Filled.Wifi, text = R.string.net_connect)
                }
                Button(
                    onClick = viewModel::openBluetoothSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = buttonShadow()
                ) {
                    VerticalButtonWithIcon(icon = Icons.Filled.Bluetooth, text = R.string.bluetooth)
                }
                Button(
                    onClick = {
                        viewModel.dropAuthentication()
                        /*runBlocking {
                            delay(1000)
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }*/
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = buttonShadow()
                ) {
                    VerticalButtonWithIcon(icon = Icons.Filled.DeleteForever, text = R.string.discharge)
                }
            }
        }
    }
}
