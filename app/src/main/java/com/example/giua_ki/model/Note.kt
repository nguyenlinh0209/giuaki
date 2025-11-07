package com.example.giua_ki.model

import com.google.firebase.firestore.Exclude

data class Note(
    @get:Exclude var noteID: String? = "",
    var title: String? = "",
    var description: String? = "",
    var imageUrl: String? = ""
)