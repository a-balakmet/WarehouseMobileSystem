package com.example.wms.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wms.R
import com.example.wms.app.viewModels.OperationalViewModel
import com.example.wms.ui.components.DefaultButtonStyle
import com.example.wms.ui.theme.getPrimaryColor
import java.util.*

@Composable
fun AskComparison(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, operationViewModel: OperationalViewModel) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                setShowDialog(false)
            },
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
                        text = stringResource(id = R.string.no_pallet_data),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.compare_pallet),
                    color = Color.Black,
                    fontSize = 17.sp
                )
            },
            confirmButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                        operationViewModel.startComparison()
                    }) {
                        Text(text = stringResource(id = R.string.yes).uppercase(Locale.getDefault()))
                    }
                }
            },
            dismissButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                        operationViewModel.cancelComparison()
                    }) {
                        Text(text = stringResource(id = R.string.no).uppercase(Locale.getDefault()))
                    }
                }
            }
        )
    }
}

@Composable
fun NoComparisonDialog(showDialog: Boolean, setShowDialog: (Boolean) -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
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
                        text = stringResource(id = R.string.attention),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.comparison_not_started),
                    color = Color.Black,
                    fontSize = 17.sp
                )
            },
            confirmButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        )
    }
}

@Composable
fun AskNewComparison(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, operationViewModel: OperationalViewModel) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
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
                        text = stringResource(id = R.string.attention),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                Text(
                    text = "${stringResource(id = R.string.no_product_received)}\n${stringResource(id = R.string.continue_comparison)}",
                    color = Color.Black,
                    fontSize = 17.sp
                )
            },
            confirmButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                        operationViewModel.startComparison()
                    }) {
                        Text(text = stringResource(id = R.string.yes).uppercase(Locale.getDefault()))
                    }
                }
            },
            dismissButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                        operationViewModel.cancelComparison()
                    }) {
                        Text(text = stringResource(id = R.string.no).uppercase(Locale.getDefault()))
                    }
                }
            }
        )
    }
}

@Composable
fun DropPartAtComparison(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, operationViewModel: OperationalViewModel) {
    if (showDialog) {
        val message = stringResource(id = R.string.cancel_part)
        val request = stringResource(id = R.string.scan_part)
        AlertDialog(
            onDismissRequest = {
                setShowDialog(false)
            },
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Help,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = getPrimaryColor()
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = stringResource(id = R.string.confirmation),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                Text(text = "$message ${operationViewModel.partCode.value}?")
            },
            confirmButton = {
                Button(onClick = {
                    setShowDialog(false)
                    operationViewModel.partCode.value = ""
                    operationViewModel.palletsList.clear()
                    operationViewModel.setMessage(value = request)
                }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                Button(onClick = {
                    setShowDialog(false)
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun AskUploading(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, operationViewModel: OperationalViewModel) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                setShowDialog(false)
            },
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Help,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = getPrimaryColor()
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = stringResource(id = R.string.confirmation),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                Text(text = stringResource(id = R.string.ask_uploading))
            },
            confirmButton = {
                Button(onClick = {
                    operationViewModel.isComparison.value = true
                    operationViewModel.comparePalletWithProduct(product = null)
                    setShowDialog(false)
                }) {
                    Text(text = stringResource(id = R.string.upload_finish))
                }
            },
            dismissButton = {
                Button(onClick = {
                    operationViewModel.palletsRequestedQuantity.value = null
                    operationViewModel.comparePalletWithProduct(product = null)
                    setShowDialog(false)
                }) {
                    Text(text = stringResource(id = R.string.upload_continue))
                }
            }
        )
    }
}