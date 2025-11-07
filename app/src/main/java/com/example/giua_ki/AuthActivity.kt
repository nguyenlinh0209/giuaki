package com.example.giua_ki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.giua_ki.ui.theme.FirebaseprojectTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Mynavigation(navController)
                }
            }
        }
    }
}
