package com.example.giua_ki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.giua_ki.route.Screen

@Composable
fun Mynavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Signin.route
    ) {
        composable(Screen.Signin.route) {
            SignIn(navController = navController)
        }
        composable(Screen.Signup.route) {
            SignUp(navController = navController)
        }
    }
}
