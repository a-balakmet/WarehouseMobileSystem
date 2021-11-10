package com.example.wms.ui.screens.bottomDialogs

import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import com.example.wms.R
import com.example.wms.app.models.ProductSeries
import com.example.wms.app.viewModels.OperationalViewModel
import com.example.wms.room.entities.Product
import com.example.wms.ui.components.ButtonText
import com.example.wms.ui.components.DefaultButtonStyle
import com.example.wms.ui.components.ProductForComparison
import com.example.wms.ui.theme.Shapes
import com.example.wms.ui.theme.getBackgroundColor
import com.example.wms.ui.theme.getPrimaryColor

@ExperimentalComposeUiApi
@Composable
fun ProductsToCompare(product: Product?, productsList: List<ProductSeries>, operationViewModel: OperationalViewModel) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
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
                .background(getBackgroundColor())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val productTitle = product?.name ?: ""
                Text(
                    text = productTitle,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = 8.dp)
                        .weight(1F)
                )
                Image(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close",
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .clip(shape = Shapes.small)
                        .background(Color.Black.copy(alpha = 0.1F))
                        .size(size = 20.dp)
                        .clickable {
                            operationViewModel.cancelComparison()
                        }
                )
            }
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = if (it.any { char ->
                            char == '\n'
                        }) {
                        searchText
                    } else {
                        it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    //.background(getPrimaryColor())
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                            keyboardController?.hide()
                        }
                        false
                    },
                placeholder = { Text(stringResource(id = R.string.search)) },
                //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, /*keyboardType = KeyboardType.Number*/),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "searcher",
                        tint = getPrimaryColor()
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "clear",
                        tint = Color.DarkGray,
                        modifier = Modifier.clickable {
                            searchText = ""
                        })
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.DarkGray
                )
            )

            LazyColumn(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .weight(1F)
            ) {
                items(items = productsList.filter { product ->
                    product.seriesNo.contains(searchText) || product.productionDate.contains(searchText)
                }) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            operationViewModel.products2compare.value = null
                            operationViewModel.comparePalletWithProduct(product = it)
                        }) {
                        ProductForComparison(product = it)
                        Divider()
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { searchText += "1" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "1")
                }
                TextButton(onClick = { searchText += "2" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "2")
                }
                TextButton(onClick = { searchText += "3" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "3")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { searchText += "4" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "4")
                }
                TextButton(onClick = { searchText += "5" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "5")
                }
                TextButton(onClick = { searchText += "6" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "6")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { searchText += "7" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "7")
                }
                TextButton(onClick = { searchText += "8" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "8")
                }
                TextButton(onClick = { searchText += "9" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "9")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { searchText += "-" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "-")
                }
                TextButton(onClick = { searchText += "0" }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "0")
                }
                TextButton(onClick = { searchText = searchText.dropLast(1) }, modifier = Modifier.weight(1F)) {
                    ButtonText(text = "\u232b")
                }
            }
            DefaultButtonStyle {
                Button(
                    onClick = { operationViewModel.cancelComparison() },
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}