package org.laolittle.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Song(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
) {
    override fun toString(): String {
        return name + "\n" + description + "\n"
    }
}

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}