package com.example.wms.ui.screens.bottomDialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.wms.R
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.components.CenteredBoldText
import com.example.wms.ui.components.DefaultButtonStyle
import com.example.wms.ui.components.SimpleTextInt
import com.example.wms.ui.components.TaskItem
import com.example.wms.ui.theme.getBackgroundColor
import com.example.wms.ui.theme.getPrimaryColor

@Composable
fun TasksLoading(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, mainViewModel: MainViewModel) {
    if (showDialog) {
        val (showErrorsDialog, setShowErrorsDialog) = remember { mutableStateOf(false) }
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
                CenteredBoldText(text = R.string.loading_task)
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Image(
                    painterResource(R.drawable.main_logo),
                    contentDescription = "",
                    modifier = Modifier.size(70.dp),
                    alignment = Alignment.Center
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                mainViewModel.tasksLoadingState.value?.let {
                    SimpleTextInt(text = it)
                    when (it) {
                        R.string.loading_finished -> {
                            mainViewModel.movementTasks.value?.let { tasks ->
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(all = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    items(items = tasks) { task ->
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                mainViewModel.task.value = task
                                            }){
                                            TaskItem(task = task)
                                        }
                                    }
                                }
                            }
                            DefaultButtonStyle {
                                Button(
                                    onClick = {
                                        setShowDialog(false)
                                        mainViewModel.movementTasks.value = null
                                    },
                                    modifier = Modifier
                                        .align(alignment = Alignment.CenterHorizontally)
                                        .padding(all = 8.dp)
                                ) {
                                    Text(text = stringResource(id = R.string.cancel))
                                }
                            }
                        }
                        R.string.no_tasks -> {
                            DefaultButtonStyle {
                                Button(
                                    onClick = { setShowDialog(false) },
                                    modifier = Modifier
                                        .align(alignment = Alignment.CenterHorizontally)
                                        .padding(all = 8.dp)
                                ) {
                                    Text(text = stringResource(id = R.string.ok))
                                }
                            }
                        }
                        R.string.no_server_connection -> {
                            Text(text = stringResource(id = R.string.repeat_loading))
                            DefaultButtonStyle {
                                Button(
                                    onClick = { setShowDialog(false) },
                                    modifier = Modifier
                                        .padding(all = 8.dp)
                                ) {
                                    Text(text = stringResource(id = R.string.ok))
                                }
                            }
                        }
                        R.string.error_at_task_loading -> {
                            Row {
                                DefaultButtonStyle {
                                    Button(
                                        onClick = {
                                            //setShowDialog(false)
                                            setShowErrorsDialog(true)
                                        },
                                        modifier = Modifier
                                            .padding(all = 8.dp)
                                    ) {
                                        Text(text = stringResource(id = R.string.errors))
                                    }
                                    Button(
                                        onClick = {
                                            setShowDialog(false)
                                        },
                                        modifier = Modifier
                                            .padding(all = 8.dp)
                                    ) {
                                        Text(text = stringResource(id = R.string.cancel))
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
        mainViewModel.taskLoadingProgress.value.let {
            if (it == 2) mainViewModel.mergeTasks()
        }
        LoadingTasksErrorsDialog(showErrorsDialog, setShowErrorsDialog, mainViewModel)
    }
}

@Composable
fun LoadingTasksErrorsDialog(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, viewModel: MainViewModel) {
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
                }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }
}