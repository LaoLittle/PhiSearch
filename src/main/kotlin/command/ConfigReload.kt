package org.laolittle.plugin.command

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.laolittle.plugin.PhiSearch
import org.laolittle.plugin.utils.decodeJson

@ExperimentalSerializationApi
object ConfigReload : SimpleCommand(
    PhiSearch, "fsreload",
    description = "PhiSearch配置重载"
) {

    @Handler
    suspend fun CommandSender.handle() {
        decodeJson()
        sendMessage("配置重载成功")
    }
}