package com.example.giua_ki

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
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
import coil.compose.rememberAsyncImagePainter
import com.example.giua_ki.model.Note
import com.example.giua_ki.ui.theme.FirebaseprojectTheme
import com.example.giua_ki.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        ) {
                            NoteUI(LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteUI(context: Context) {
    val db = FirebaseFirestore.getInstance()
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }

    // Load dữ liệu từ Firestore
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
        OutlinedTextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Tên sản phẩm") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 15.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFF90CAF9)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Loại sản phẩm / Mô tả") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 15.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFF90CAF9)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imageUrl.value,
            onValueChange = { imageUrl.value = it },
            leadingIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Folder, contentDescription = "Chọn ảnh")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 15.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFF90CAF9)
            )
        )

        if (imageUrl.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(imageUrl.value),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(4.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (TextUtils.isEmpty(title.value) ||
                    TextUtils.isEmpty(description.value) ||
                    TextUtils.isEmpty(imageUrl.value)
                ) {
                    Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                } else {
                    if (isEditing.value) {
                        db.collection("Notes").document(editingId.value)
                            .update(
                                mapOf(
                                    "title" to title.value,
                                    "description" to description.value,
                                    "imageUrl" to imageUrl.value
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(context, "Đã sửa sản phẩm!", Toast.LENGTH_SHORT).show()
                                isEditing.value = false
                                title.value = ""
                                description.value = ""
                                imageUrl.value = ""
                            }
                    } else {
                        val note = hashMapOf(
                            "title" to title.value,
                            "description" to description.value,
                            "imageUrl" to imageUrl.value
                        )
                        db.collection("Notes").add(note)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Đã thêm sản phẩm!", Toast.LENGTH_SHORT).show()
                                title.value = ""
                                description.value = ""
                                imageUrl.value = ""
                            }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text(
                if (isEditing.value) "SỬA SẢN PHẨM" else "THÊM SẢN PHẨM",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Danh sách sản phẩm:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                                Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onEdit: (Note) -> Unit, onDelete: (Note) -> Unit) {
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
            modifier = Modifier
                .size(80.dp)
                .padding(4.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text("Tên sp: ${note.title}", fontWeight = FontWeight.Bold)
            Text("Mô tả: ${note.description}")
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {

            IconButton(
                onClick = { onEdit(note) },
                modifier = Modifier
                    .padding(4.dp)
                    .background(Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFFFC107), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFFFFC107)
                )
            }

            IconButton(
                onClick = { onDelete(note) },
                modifier = Modifier
                    .padding(4.dp)
                    .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFF44336), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFF44336)
                )
            }
        }
    }
}
