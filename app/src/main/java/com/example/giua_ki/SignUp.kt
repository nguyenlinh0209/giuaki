package com.example.giua_ki

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SignUp(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val (focusEmail, focusPassword) = remember { FocusRequester.createRefs() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF4)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Tạo tài khoản mới", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(20.dp))

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
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB90020)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFB90020),
                unfocusedBorderColor = Color(0xFFB90020)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp),
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB90020)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFB90020),
                unfocusedBorderColor = Color(0xFFB90020)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && confirm.isNotEmpty()) {
                    if (password == confirm) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Signin.route)
                                } else {
                                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB90020)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate(Screen.Signin.route) }) {
            Text("Đã có tài khoản? Đăng nhập", color = Color(0xFFB90020))
        }
    }
}
