package com.example.wms.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.example.wms.app.models.StorekeeperTask
import com.example.wms.app.viewModels.MainViewModel
import com.example.wms.room.entities.UserInfo
import com.example.wms.ui.screens.*
import com.example.wms.ui.screens.bottomDialogs.LoadingScreen
import com.example.wms.ui.screens.bottomDialogs.LoginDialog
import com.example.wms.ui.screens.operations.*

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Navigation() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // basics
        composable(route = Screen.Login.route) {
            LoginDialog(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = Screen.Loading.route) {
            LoadingScreen(navController = navController)
        }
        composable(route = Screen.Start.route) {
            StartScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(route = Screen.BarcodeCheck.route) {
            BarcodeCheckScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = Screen.Shift.route) {
            ShiftScreen(navController = navController, mainViewModel = mainViewModel)
        }
        // operations
        composable(
            route = Screen.Movement.route + "/{userData}/{oType}/{task}",
            arguments = listOf(
                navArgument("userData") {
                    type = NavType.StringType
                },
                navArgument(("oType")) {
                    type = NavType.IntType
                },
                navArgument(("task")) {
                    type = NavType.StringType
                })
        ) { entry ->
            entry.arguments?.getString("userData")?.let { jsonUser ->
                val userInfo = Gson().fromJson(jsonUser, UserInfo::class.java)
                entry.arguments?.getString("task")?.let { jsonTask ->
                    val task = Gson().fromJson(jsonTask, StorekeeperTask::class.java)
                    entry.arguments?.getInt("oType")?.let {
                        MovementScreen(navController = navController, mainViewModel = mainViewModel, userInfo = userInfo, task = task)
                    }
                }
            }
        }
        composable(
            route = Screen.Inventory.route + "/{userData}/{oType}",
            arguments = listOf(
                navArgument("userData") {
                    type = NavType.StringType
                },
                navArgument(("oType")) {
                    type = NavType.IntType
                })
        ) { entry ->
            entry.arguments?.getString("userData")?.let { json ->
                val userInfo = Gson().fromJson(json, UserInfo::class.java)
                entry.arguments?.getInt("oType")?.let {
                    InventoryScreen(navController = navController, mainViewModel = mainViewModel, userInfo = userInfo)
                }
            }
        }
        composable(
            route = Screen.ComparisonB.route + "/{userData}/{oType}",
            arguments = listOf(
                navArgument("userData") {
                    type = NavType.StringType
                },
                navArgument(("oType")) {
                    type = NavType.IntType
                })
        ) { entry ->
            entry.arguments?.getString("userData")?.let { json ->
                val userInfo = Gson().fromJson(json, UserInfo::class.java)
                entry.arguments?.getInt("oType")?.let {
                    ComparisonBaiserke(navController = navController, mainViewModel = mainViewModel, userInfo = userInfo)
                }
            }
        }
        composable(
            route = Screen.ComparisonS.route + "/{userData}/{oType}",
            arguments = listOf(
                navArgument("userData") {
                    type = NavType.StringType
                },
                navArgument(("oType")) {
                    type = NavType.IntType
                })
        ) { entry ->
            entry.arguments?.getString("userData")?.let { json ->
                val userInfo = Gson().fromJson(json, UserInfo::class.java)
                entry.arguments?.getInt("oType")?.let {
                    ComparisonSuynbai(navController = navController, mainViewModel = mainViewModel, userInfo = userInfo)
                }
            }
        }
        composable(
            route = Screen.CheckPallet.route + "/{userData}/{oType}",
            arguments = listOf(
                navArgument("userData") {
                    type = NavType.StringType
                },
                navArgument(("oType")) {
                    type = NavType.IntType
                })
        ) { entry ->
            entry.arguments?.getString("userData")?.let { json ->
                val userInfo = Gson().fromJson(json, UserInfo::class.java)
                entry.arguments?.getInt("oType")?.let {
                    CheckPalletScreen(navController = navController, mainViewModel = mainViewModel, userInfo = userInfo)
                }
            }
        }
    }
}
