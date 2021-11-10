package com.example.wms.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.wms.R
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.ui.navigation.Navigation
import com.example.wms.ui.theme.WarehouseMobileSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var aBarcode = ""
    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (it.value) {
                initNavigation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestPermissionsLauncher.launch(
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    @ExperimentalMaterialApi
    private fun initNavigation() {
        setContent {
            WarehouseMobileSystemTheme {
                Navigation()
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed()
            } else {
                if (event.keyCode != KeyEvent.KEYCODE_ENTER) {
                    val keyUnicode = event.getUnicodeChar(event.metaState)
                    val character = keyUnicode.toChar()
                    aBarcode += character.toString()
                } else {
                    if (!aBarcode.startsWith("0") && !aBarcode.startsWith("1") && !aBarcode.startsWith("4")) {
                        aBarcode = "0$aBarcode"
                    }
                    mainViewModel.theBarcode.value = aBarcode
                    aBarcode = ""
                }
            }
        }
        return true
    }
}
