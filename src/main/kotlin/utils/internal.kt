/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package org.laolittle.plugin.utils

import org.laolittle.plugin.model.Song
import java.util.*
import kotlin.math.max
import kotlin.math.min


internal fun String.fuzzyMatchWith(target: String): Double {
    val originL = this.toLowerCase()
    this.toLowerCase()
    val targetL = target.toLowerCase()
    if (originL == targetL) {
        return 1.0
    }
    var match = 0
    for (i in 0..(max(originL.lastIndex, targetL.lastIndex))) {
        val t = targetL.getOrNull(match) ?: break
        if (t == originL.getOrNull(i)) {
            match++
        }
    }

    val longerLength = max(this.length, target.length)
    val shorterLength = min(this.length, target.length)

    return match.toDouble() / (longerLength + (shorterLength - match))
}


/**
 * @return candidates
 */
internal fun HashMap<Int, Song>.fuzzySearchSong(
    name: String,
    minRate: Double = 0.2, // 参与判断, 用于提示可能的解
    matchRate: Double = 0.6,// 最终选择的最少需要的匹配率, 减少歧义
    /**
     * 如果有多个值超过 [matchRate], 并相互差距小于等于 [disambiguationRate], 则认为有较大歧义风险, 返回可能的解的列表.
     */
    disambiguationRate: Double = 0.1,
): List<Pair<Song, Double>> {
    val results = this.values
        .asSequence()
        .associateWith { it.name.fuzzyMatchWith(name) }
        .filter { it.value >= minRate }
        .toList()
        .sortedByDescending { it.second }

    val matches = results.filter { it.second >= matchRate }

    return when {
        matches.isEmpty() -> results
        matches.size == 1 -> listOf(matches.single().first to 1.0)
        else -> {
            if (matches.first().second - matches.last().second <= disambiguationRate) {
                results
            } else {
                listOf(matches.first().first to 1.0)
            }
        }
    }
}

private fun String.toLowerCase() = this.lowercase(Locale.getDefault())