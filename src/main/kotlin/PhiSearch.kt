package org.laolittle.plugin

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.command.ConfigReload
import org.laolittle.plugin.model.Searcher
import org.laolittle.plugin.utils.init

@ExperimentalSerializationApi
object PhiSearch : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.FuzzySearch",
        version = "1.0",
        name = "PhiSearch"
    )
) {
    override fun onEnable() {
        logger.info { "加载完成" }
        init()
        ConfigReload.register()
        Searcher.start()
        /*GlobalEventChannel.subscribeMessages {
            startsWith("测试"){
                val date = LocalDate.now()
                val random = Random(sender.id + date.year + date.monthValue + date.dayOfMonth).nextInt(1, 100)
                subject.sendMessage("""
                    ${songInfo[5]}
                    测，都可以测
                    $random
                """.trimIndent())
            }
            /*startsWith("复读") {
                if(isActivate(sender.id)) return@startsWith
                subject.sendMessage("好")
                whileSelectMessages {
                    startsWith("停止") {
                        isActivate = isActivate.minus(sender.id)
                        false
                    }
                    default {
                        if (stop) return@default false
                        val sourcemessage = this
                        subject.sendMessage(sourcemessage.message)
                        true
                    }
                }
                subject.sendMessage("不复读了")
            }*/
        }*/
    }

    override fun onDisable() {
        logger.info { "卸载完成" }
    }
}