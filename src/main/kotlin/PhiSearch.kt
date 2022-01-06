package org.laolittle.plugin

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.command.ConfigReload
import org.laolittle.plugin.model.Song
import org.laolittle.plugin.utils.init
import org.laolittle.plugin.utils.search
import org.laolittle.plugin.utils.songInfo
import java.io.File

@ExperimentalSerializationApi
object PhiSearch : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.FuzzySearch",
        version = "1.0.1",
        name = "PhiSearch"
    )
) {
    override fun onEnable() {
        logger.info { "加载完成" }
        init()
        ConfigReload.register()
        globalEventChannel().subscribeMessages {
            startsWith("定数查询") {
                subject.sendMessage("请输入歌曲名称")
                val name = runCatching { nextMessage(30_000) }.onFailure { subject.sendMessage("超时未输入") }
                    .getOrElse { return@startsWith }
                val info: List<Pair<Song, Int>> = songInfo.search(name.content)
                when (info.size) {
                    0 -> subject.sendMessage("未找到结果! ")
                    1 -> {
                        val result = buildMessageChain {
                            add(PlainText("${info[0].first.name}\n"))
                            add(PlainText("${info[0].first.description}\n"))
                            val imageFile = img(info[0].second)
                            if (imageFile.exists()) add(subject.uploadImage(imageFile))
                        }
                        subject.sendMessage(result)
                    }
                    else -> {
                        subject.sendMessage(buildMessageChain {
                            var order = 0
                            info.forEach {
                                order++
                                add("$order${it.first.name}\n")
                            }
                            add("找到${order}个结果，请输入序号查询")
                        })
                        val number = try {
                            nextMessage(12_000).content.toInt() - 1
                        } catch (_: NumberFormatException) {
                            subject.sendMessage("请输入数字 !")
                            return@startsWith
                        } catch (_: TimeoutCancellationException) {
                            subject.sendMessage("超时未输入")
                            return@startsWith
                        }
                        if (number > info.size - 1 || number < 0) subject.sendMessage("请输入正确的数字 !")
                        else {
                            val result = buildMessageChain {
                                add(PlainText("${info[number].first.name}\n"))
                                add(PlainText("${info[number].first.description}\n"))
                                val imageFile = img(info[number].second)
                                if (imageFile.exists()) add(subject.uploadImage(imageFile))
                            }
                            subject.sendMessage(result)
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        logger.info { "卸载完成" }
    }

    private fun img(i: Int): File {
        return File("${dataFolder}/SongImg").resolve("${i}.png")
    }
}