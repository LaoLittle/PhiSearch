package org.laolittle.plugin.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import org.laolittle.plugin.PhiSearch
import org.laolittle.plugin.PhiSearch.dataFolder
import org.laolittle.plugin.model.Json
import org.laolittle.plugin.model.Song
import java.io.*
import java.util.jar.JarFile

var songInfo: Map<Int, Song> = linkedMapOf()

@ExperimentalSerializationApi
fun decodeJson() {
    songInfo = Json.decodeFromString(dataFolder.resolve("Songs.json").readText())
}

fun search(name: String, i: Int): Song? {
    if ((Regex(name, RegexOption.IGNORE_CASE).containsMatchIn("${songInfo[i]?.name}")))
        return songInfo[i]
    return null
}

@ExperimentalSerializationApi
fun init(){
    if(dataFolder.resolve("Songs.json").exists())
        decodeJson()
    else {
        selfRead().use { input ->
            BufferedOutputStream(FileOutputStream(dataFolder.resolve("Songs.json"))).use {
                input?.copyTo(it)
            }
        }
        decodeJson()
    }
}

@ExperimentalSerializationApi
private fun selfRead(): InputStream? {
    val path = PhiSearch.javaClass.protectionDomain.codeSource.location.path
    val jarpath = JarFile(path)
    val entry = jarpath.getJarEntry("data/Songs.json")
    return jarpath.getInputStream(entry)
}