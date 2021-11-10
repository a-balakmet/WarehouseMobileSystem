package com.example.wms.ui.screens.bottomDialogs

import android.view.KeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wms.ui.theme.getBackgroundColor
import com.example.wms.R
import com.example.wms.app.viewModels.OperationalViewModel
import com.example.wms.ui.components.DefaultButtonStyle
import com.example.wms.ui.theme.Shapes
import com.example.wms.ui.theme.getPrimaryColor

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun PalletsQuantityRequest(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, operationViewModel: OperationalViewModel) {
    if (showDialog) {
        val ok = stringResource(id = R.string.ok)
        val cancel = stringResource(id = R.string.cancel)
        val buttons = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ok, "0", cancel)
        val scanPart = stringResource(id = R.string.scan_part)
        val scanPallet = stringResource(id = R.string.scan_pallet)
        var inputText by rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        keyboardController?.hide()
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp
                            )
                        )
                        .background(getPrimaryColor())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .clickable {
                                operationViewModel.partCode.value = ""
                                operationViewModel.setMessage(value = scanPart)
                                setShowDialog(false)
                            }
                    ) {
                        Text(
                            text = stringResource(id = R.string.pallet_quantity).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            modifier = Modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .padding(all = 8.dp),
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close",
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .clip(shape = Shapes.small)
                            .background(Color.Black.copy(alpha = 0.1F))
                            .size(size = 20.dp)
                            .clickable {
                                operationViewModel.partCode.value = ""
                                operationViewModel.setMessage(value = scanPart)
                                setShowDialog(false)
                            },
                        tint = Color.White
                    )
                }
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .onKeyEvent {
                            if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                keyboardController?.hide()
                            }
                            false
                        },
                    placeholder = { Text(stringResource(id = R.string.quantity)) },
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "clear",
                            tint = Color.DarkGray,
                            modifier = Modifier.clickable {
                                inputText = ""
                            })
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.DarkGray
                    )
                )
                LazyVerticalGrid(
                    cells = GridCells.Fixed(3),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    items(buttons) { pad ->
                        DefaultButtonStyle {
                            Button(
                                onClick = {
                                    when (pad) {
                                        ok -> {
                                            operationViewModel.palletsRequestedQuantity.value = inputText.toInt()
                                            operationViewModel.setMessage(value = scanPallet)
                                            setShowDialog(false)
                                        }
                                        cancel -> {
                                            operationViewModel.partCode.value = ""
                                            operationViewModel.setMessage(value = scanPart)
                                            setShowDialog(false)
                                        }
                                        else -> inputText += pad
                                    }
                                },
                                modifier = Modifier.padding(all = 4.dp)
                            ) {
                                Text(text = pad)
                            }
                        }
                    }
                }
            }
        }
    }
}
