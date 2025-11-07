package com.example.giua_ki

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.giua_ki.model.Note
import com.example.giua_ki.route.Screen
import com.example.giua_ki.ui.theme.FirebaseprojectTheme
import com.example.giua_ki.ui.theme.greenColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseprojectTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                var role by remember { mutableStateOf("user") }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(user) {
                    user?.let {
                        db.collection("users").document(it.uid).get()
                            .addOnSuccessListener { doc ->
                                role = doc.getString("role") ?: "user"
                                isLoading = false
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }
                    } ?: run { isLoading = false }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = greenColor)
                    }
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = greenColor,
                                title = {
                                    Text(
                                        text = "DỮ LIỆU SẢN PHẨM",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                },
                                actions = {
                                    IconButton(onClick = {
                                        FirebaseAuth.getInstance().signOut()
                                        Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
                                        navController.navigate(Screen.Signin.route) {
                                            popUpTo(Screen.Home.route) { inclusive = true }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = "Đăng xuất",
                                            tint = Color.White
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        ) {
                            NoteUI(context, role)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteUI(context: Context, role: String) {
    val db = FirebaseFirestore.getInstance()
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    LaunchedEffect(Unit) {
        db.collection("Notes").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.map {
                Note(
                    noteID = it.id,
                    title = it.getString("title"),
                    description = it.getString("description"),
                    imageUrl = it.getString("imageUrl")
                )
            } ?: emptyList()
            notes = list
        }
    }

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val imageUrl = remember { mutableStateOf("") }
    val isEditing = remember { mutableStateOf(false) }
    val editingId = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (role == "admin") {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Tên sản phẩm") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = imageUrl.value,
                onValueChange = { imageUrl.value = it },
                label = { Text("Ảnh URL") },
                leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (title.value.isEmpty() || description.value.isEmpty() || imageUrl.value.isEmpty()) {
                        Toast.makeText(context, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                    } else {
                        if (isEditing.value) {
                            db.collection("Notes").document(editingId.value)
                                .update(
                                    mapOf(
                                        "title" to title.value,
                                        "description" to description.value,
                                        "imageUrl" to imageUrl.value
                                    )
                                )
                            Toast.makeText(context, "Đã sửa sản phẩm!", Toast.LENGTH_SHORT).show()
                            isEditing.value = false
                        } else {
                            val note = hashMapOf(
                                "title" to title.value,
                                "description" to description.value,
                                "imageUrl" to imageUrl.value
                            )
                            db.collection("Notes").add(note)
                            Toast.makeText(context, "Đã thêm sản phẩm!", Toast.LENGTH_SHORT).show()
                        }
                        title.value = ""
                        description.value = ""
                        imageUrl.value = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(if (isEditing.value) "SỬA SẢN PHẨM" else "THÊM SẢN PHẨM", color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Text("Danh sách sản phẩm:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onEdit = {
                        title.value = it.title ?: ""
                        description.value = it.description ?: ""
                        imageUrl.value = it.imageUrl ?: ""
                        editingId.value = it.noteID ?: ""
                        isEditing.value = true
                    },
                    onDelete = {
                        db.collection("Notes").document(it.noteID!!).delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show()
                            }
                    },
                    canEdit = role == "admin"
                )
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onEdit: (Note) -> Unit, onDelete: (Note) -> Unit, canEdit: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, Color(0xFF2196F3), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(note.imageUrl),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text("Tên: ${note.title}", fontWeight = FontWeight.Bold)
            Text("Mô tả: ${note.description}")
        }
        if (canEdit) {
            Column {
                IconButton(onClick = { onEdit(note) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFFC107))
                }
                IconButton(onClick = { onDelete(note) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336))
                }
            }
        }
    }
}
