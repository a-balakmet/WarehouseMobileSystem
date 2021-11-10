package com.example.wms.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import com.example.wms.ui.theme.getPrimaryColor
import java.util.*

@Composable
fun UploadErrorDialog(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, operationViewModel: OperationalViewModel){
    if (showDialog) {
        val successfulUpload = stringResource(id = R.string.upload_success)
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
                        text = stringResource(id = R.string.error_at_uploading),
                        color = getPrimaryColor()
                    )
                }
            },
            text = {
                Text(
                    text = "${operationViewModel.uploadingErrors}\n\n${stringResource(id = R.string.repeat_or_exit)}",
                    color = Color.Black,
                    fontSize = 17.sp
                )
            },
            confirmButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                        operationViewModel.sendOperationResults()
                    }) {
                        Text(text = stringResource(id = R.string.repeat).uppercase(Locale.getDefault()))
                    }
                }
            },
            dismissButton = {
                DefaultButtonStyle {
                    Button(onClick = {
                        setShowDialog(false)
                        operationViewModel.setMessage(successfulUpload)
                    }) {
                        Text(text = stringResource(id = R.string.exit).uppercase(Locale.getDefault()))
                    }
                }
            }
        )
    }
}