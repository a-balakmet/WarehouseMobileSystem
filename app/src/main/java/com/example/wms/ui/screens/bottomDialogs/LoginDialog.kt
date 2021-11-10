package com.example.wms.ui.screens.bottomDialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wms.R
import com.example.wms.app.viewModels.LoginViewModel
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.components.*
import com.example.wms.ui.navigation.Screen
import com.example.wms.ui.theme.getBackgroundColor

@Composable
fun LoginDialog(navController: NavController, mainViewModel: MainViewModel) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val loginState by viewModel.authState.observeAsState()
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
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            CenteredBoldText(text = R.string.authorization)
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Image(
                painterResource(R.drawable.main_logo),
                contentDescription = "",
                modifier = Modifier.size(70.dp),
                alignment = Alignment.Center
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            loginState?.let {
                if (it != R.string.get_auth) {
                    CenteredTextInt(text = it)
                    when (it) {
                        R.string.auth_ok -> {
                            mainViewModel.loadInitUserData()
                            navController.navigate(Screen.Loading.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        R.string.wrong_date -> {
                            DefaultButtonStyle {
                                Button(
                                    onClick = mainViewModel::openDateSettings,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = buttonShadow()
                                ) {
                                    TextButton(text = R.string.ok)
                                }
                            }
                        }
                        R.string.no_net -> {
                            DefaultButtonStyle {
                                Button(
                                    onClick = { mainViewModel.openNetworkSettings(isStart = true) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = buttonShadow()
                                ) {
                                    TextButton(text = R.string.ok)
                                }
                            }
                        }
                        R.string.auth_error -> {
                            DefaultButtonStyle {
                                Button(
                                    onClick = {
                                        mainViewModel.dropAuthentication()
                                        mainViewModel.restartApp()},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = buttonShadow()
                                ) {
                                    TextButton(text = R.string.discharge)
                                }
                            }
                        }
                    }
                } else {
                    CenteredTextString(text = "${stringResource(id = it)}\n${viewModel.guid}")
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}