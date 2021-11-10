package com.example.wms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wms.R
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.theme.getPrimaryColor
import com.example.wms.ui.components.*

@Composable
fun BarcodeCheckScreen(navController: NavController, mainViewModel: MainViewModel) {
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
                TitleText(text = stringResource(id = R.string.check_barcode))
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CenteredTextInt(R.string.scan_barcode)
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 5.dp
            ) {
                mainViewModel.theBarcode.value?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            DefaultButtonStyle {
                Button(
                    onClick = { mainViewModel.theBarcode.value = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = buttonShadow()
                ) {
                    TextButton(text = R.string.clear)
                }
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = buttonShadow()
                ) {
                    TextButton(text = R.string.finish)
                }
            }
        }
    }
}