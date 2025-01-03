package io.rokuko.azureflow.addon.nbt.command

import io.rokuko.azureflow.addon.nbt.getByPath
import io.rokuko.azureflow.bukkit.itemTag
import io.rokuko.azureflow.bukkit.nms.tag.CompoundTag
import io.rokuko.azureflow.bukkit.nms.tag.CompoundTag.Companion.TAG_COMPOUND
import io.rokuko.azureflow.bukkit.nms.tag.CompoundTag.Companion.TAG_STRING
import io.rokuko.azureflow.features.logger.log
import io.rokuko.azureflow.utils.colorful
import io.rokuko.azureflow.utils.gson
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun List<String>.beautifyNbtPath() = if (size == 1) {
    "&3${first()}"
} else {
    "&7${subList(0, size - 1).joinToString(".")}.&3${last()}"
}

val nbtShowCommand = mapOf(
    "description" to "显示物品的NBT数据",
    "executor" to executor@{ sender: CommandSender, args: List<String> ->
        require(sender is Player)
        val itemInMainHand = sender.inventory.itemInMainHand
        require(itemInMainHand.type != Material.AIR) { "主手并未手持物品" }
        val itemTag = requireNotNull(itemInMainHand.itemTag) { "该物品不存在NBT数据" }
        val path = args.getOrNull(0)
        val value = path?.let { itemTag.getByPath(it) } ?: itemTag
        val split = path?.split(".") ?: emptyList()

        log("&f该物品拥有如下 NBT 数据:", sender)
        sender.sendMessage("")

        if (value is CompoundTag) {
            if (split.size >= 1) {
                sender.sendMessage("  &7···".colorful())
                sender.sendMessage("  &7${split.beautifyNbtPath()}: &f{".colorful())
                formatNBTMessage(sender, value, 2)
                sender.sendMessage("  &f}".colorful())
                sender.sendMessage("  &7···".colorful())
            } else {
                formatNBTMessage(sender, value, 1)
            }
        } else {
            sender.sendMessage("  ${split.beautifyNbtPath()}: &f$value".colorful())
        }
        sender.sendMessage("")
    }
)

fun formatMapNBTMessage(sender: CommandSender, tagMap: Map<String, Any?>, level: Int) {
    tagMap.forEach { key, value ->
        val prefix = "  ".repeat(level)
        if (value is Map<*, *>) {
            sender.sendMessage("$prefix&7$key: &f{".colorful())
            formatMapNBTMessage(sender, value as? Map<String, Any?> ?: error("解析NBT出现异常"), level + 1)
            sender.sendMessage("$prefix&f}".colorful())
        } else {
            sender.sendMessage("$prefix&7$key: &f$value".colorful())
        }
    }
}

fun formatListNBTMessage(sender: CommandSender, tagList: List<Any?>, level: Int) {
    tagList.forEachIndexed { index, value ->
        val prefix = "  ".repeat(level)
        if (value is Map<*, *>) {
            sender.sendMessage("$prefix&7$index: &f[".colorful())
            formatMapNBTMessage(sender, value as? Map<String, Any?> ?: error("解析NBT出现异常"), level + 1)
            sender.sendMessage("$prefix&f]".colorful())
        } else {
            sender.sendMessage("$prefix&7$index: &f$value".colorful())
        }
    }
}

fun formatNBTMessage(sender: CommandSender, tag: CompoundTag, level: Int) {
    tag.forEach { key, value ->
        val prefix = "  ".repeat(level)
        val type = tag.tagType(key)
        if (type == TAG_COMPOUND) {
            sender.sendMessage("$prefix&7$key&7: &f{".colorful())
            formatNBTMessage(sender, CompoundTag(value!!), level + 1)
            sender.sendMessage("$prefix&f}".colorful())
        } else if (type == TAG_STRING && tag.getString(key).startsWith("json:")) {
            val strValue = tag.getString(key)
            var json = strValue.substring(5)
            var v: Any
            if (json.startsWith("[")) {
                json = "{\"array\": $json}"
                v = gson.fromJson(json, Map::class.java)["array"] as? List<*> ?: error("解析NBT出现异常")
            } else {
                v = gson.fromJson(json, Map::class.java) as? Map<String, *> ?: error("解析NBT出现异常")
            }
            sender.sendMessage("$prefix&7$key &b&ljson&7: &f{".colorful())
            if (v is Map<*, *>) {
                formatMapNBTMessage(sender, v as? Map<String, Any?> ?: error("解析NBT出现异常"), level + 1)
            } else if (v is List<*>) {
                formatListNBTMessage(sender, v, level + 1)
            }
            sender.sendMessage("$prefix&f}".colorful())
        } else {
            sender.sendMessage("$prefix&7$key: &f$value".colorful())
        }
    }
}