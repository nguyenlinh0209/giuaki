package com.example.giua_ki

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.example.giua_ki.route.Screen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignIn(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (focusEmail, focusPassword) = remember { FocusRequester.createRefs() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF4)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Welcome Back!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .focusRequester(focusEmail),
            label = { Text("Email") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFB90020),
                unfocusedBorderColor = Color(0xFFB90020)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusPassword.requestFocus() })
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .focusRequester(focusPassword),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB90020)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFB90020),
                unfocusedBorderColor = Color(0xFFB90020)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB90020)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate(Screen.Signup.route) }) {
            Text("Chưa có tài khoản? Đăng ký ngay", color = Color(0xFFB90020))
        }
    }
}