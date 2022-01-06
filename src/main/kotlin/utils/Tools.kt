package org.laolittle.plugin.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import org.laolittle.plugin.PhiSearch
import org.laolittle.plugin.PhiSearch.dataFolder
import org.laolittle.plugin.model.Json
import org.laolittle.plugin.model.Song
import java.io.BufferedOutputStream
import java.io.FileOutputStream

var songInfo: LinkedHashMap<Int, Song> = linkedMapOf()

@ExperimentalSerializationApi
fun decodeJson() {
    songInfo = Json.decodeFromString(dataFolder.resolve("Songs.json").readText())
}

fun HashMap<Int, Song>.search(name: String): List<Pair<Song, Int>> {
    val candidates = this.fuzzySearchSong(name)
    val maxPerMember = mutableListOf<Pair<Song, Int>>()
    candidates.forEach {
        for (i in this) {
            if (i.value.name == it.first.name) {
                maxPerMember.add(it.first to i.key)
                break
            }
        }
    }
    return maxPerMember
}

@ExperimentalSerializationApi
fun init() {
    if (dataFolder.resolve("Songs.json").exists())
        decodeJson()
    else {
        PhiSearch.javaClass.getResourceAsStream("/data/Songs.json").use { input ->
            BufferedOutputStream(FileOutputStream(dataFolder.resolve("Songs.json"))).use {
                input?.copyTo(it)
            }
        }
        decodeJson()
    }
}