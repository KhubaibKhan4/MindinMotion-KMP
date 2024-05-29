package org.mind.app.domain.model.notes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notes(
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("pdfUrl")
    val pdfUrl: String = "",
    @SerialName("title")
    val title: String = ""
)