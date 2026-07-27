package com.shuleka.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val category: String = "",
    @SerialName("pdf_url")
    val pdfUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("created_by")
    val createdBy: String = ""
)

enum class PostCategory(val label: String, val icon: String) {
    MATOKEO("Matokeo", "📊"),
    TAARIFA("Taarifa", "📢"),
    NOTES("Notes", "📝"),
    VIPIMO("Vipimo", "📋"),
    MENGINEYO("Mengineyo", "📁");

    companion object {
        fun fromString(value: String): PostCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MENGINEYO
        }
    }
}
