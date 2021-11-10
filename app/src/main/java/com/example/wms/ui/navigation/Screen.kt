package com.example.wms.ui.navigation

sealed class Screen(val route: String){
    object Login : Screen(route = "Login")
    object Loading : Screen(route = "Loading")
    object Start : Screen(route = "Start")
    object Settings : Screen (route = "Settings")
    object BarcodeCheck : Screen (route = "BarcodeCheck")
    object Shift : Screen(route = "Shift")
    object Movement : Screen(route = "Movement")
    object Inventory : Screen(route = "Inventory")
    object ComparisonB: Screen(route = "ComparisonB")
    object ComparisonS: Screen(route = "ComparisonS")
    object CheckPallet : Screen(route = "CheckPallet")
}
