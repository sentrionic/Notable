package io.notable.note_domain

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
)