package org.laolittle.plugin.model

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.PlainText
import org.laolittle.plugin.PhiSearch.dataFolder
import org.laolittle.plugin.Service
import org.laolittle.plugin.utils.*
import java.io.File

@ExperimentalSerializationApi
object Searcher : Service() {

    private var name: String = ""
    private var order: Map<Int, Int> = linkedMapOf()
    private var results: Int = 0

    override suspend fun main() {
        GlobalEventChannel.subscribeMessages {
            finding(Regex("定数")) {
                subject.sendMessage("请输入歌曲名称")
                whileSelectMessages {
                    default {
                        results = 0
                        var info: Song?
                        name = ""
                        for (i in 0 until songInfo.size) {
                            info = search(it, i)
                            if (info != null) {
                                results++
                                order = order.plus(Pair(results, i))
                                name += "$results.${info.name}\n"
                            }
                        }

                        when (results) {
                            0 -> subject.sendMessage("未找到结果! ")
                            1 -> {
                                if (img(results).exists())
                                    subject.sendMessage(
                                        PlainText(
                                            """
                            ${songInfo[order[results]]?.name}
                            ${songInfo[order[results]]?.description}
                        """.trimIndent()
                                        ).plus(subject.uploadImage(img(results)))
                                    )
                                else subject.sendMessage(
                                    """
                            ${songInfo[order[results]]?.name}
                            ${songInfo[order[results]]?.description}
                        """.trimIndent()
                                )
                            }
                            else -> {
                                subject.sendMessage(name + "找到${results}个结果，请输入序号查询")
                                whileSelectMessages<MessageEvent> {
                                    default Here@{ msg ->
                                        if (Regex("""\D""").containsMatchIn(msg))
                                            subject.sendMessage("请输入数字 !")
                                        else if ((msg.toInt() > results) or (msg.toInt() <= 0))
                                            subject.sendMessage("请输入正确的数字 !")
                                        else {
                                            if (img(msg.toInt()).exists())
                                                subject.sendMessage(
                                                    PlainText(
                                                        """
                                                ${songInfo[order[msg.toInt()]]?.name}
                                                ${songInfo[order[msg.toInt()]]?.description}
                                            """.trimIndent()
                                                    ).plus(subject.uploadImage(img(msg.toInt())))
                                                )
                                            else subject.sendMessage(
                                                """
                                                ${songInfo[order[msg.toInt()]]?.name}
                                                ${songInfo[order[msg.toInt()]]?.description}
                                            """.trimIndent()
                                            )
                                            return@Here false
                                        }
                                        true
                                    }
                                    timeout(8000) {
                                        subject.sendMessage("超时未输入")
                                        false
                                    }
                                }
                            }
                        }
                        false
                    }
                    timeout(12000) {
                        subject.sendMessage("超时未输入")
                        false
                    }
                }
            }
        }
    }

    private fun img(i: Int): File {
        return File("$dataFolder/SongImg").resolve("${order[i]}.png")
    }
}