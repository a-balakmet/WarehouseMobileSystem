package com.example.wms.ui.screens.bottomDialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.wms.app.viewModels.LoadingViewModel
import com.example.wms.ui.components.*
import com.example.wms.ui.navigation.Screen
import com.example.wms.ui.theme.getBackgroundColor
import com.example.wms.ui.theme.getPrimaryColor

@Composable
fun LoadingScreen(navController: NavController) {
    val loadingViewModel = hiltViewModel<LoadingViewModel>()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
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
            CenteredBoldText(text = R.string.loading_references)
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Image(
                painterResource(R.drawable.main_logo),
                contentDescription = "",
                modifier = Modifier.size(70.dp),
                alignment = Alignment.Center
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Column {
                loadingViewModel.personnelLoadingState.value?.let {
                    SimpleTextInt(text = it)
                }
                loadingViewModel.cellsLoadingState.value?.let {
                    SimpleTextInt(text = it)
                }
                loadingViewModel.productsLoadingState.value?.let {
                    SimpleTextInt(text = it)
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            loadingViewModel.loadingErrors.value?.let {
                if (it.length > 5) {
                    Button(
                        onClick = { setShowDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = buttonShadow()
                    ) {
                        TextButton(text = R.string.errors)
                    }
                } else {
                    if (!loadingViewModel.progressFulfillment.value.contains(false)) {
                        navController.navigate(Screen.Start.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
            LoadingErrorsDialog(showDialog, setShowDialog, loadingViewModel)
        }
    }
}

@Composable
fun LoadingErrorsDialog(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, viewModel: LoadingViewModel) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.ReportProblem,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = getPrimaryColor()
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = stringResource(id = R.string.error_at_loading),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                viewModel.loadingErrors.value?.let { Text(text = it) }
            },
            confirmButton = {
                Button(onClick = {
                    setShowDialog(false)
                    viewModel.executeLoading()
                }) {
                    Text(text = stringResource(id = R.string.repeat))
                }
            }
        )
    }
}