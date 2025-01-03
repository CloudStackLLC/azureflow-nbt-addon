package io.rokuko.azureflow.addon.nbt

import io.rokuko.azureflow.bukkit.nms.tag.CompoundTag

fun CompoundTag.getByPath(path: String): Any {
    // path 支持深度获取, 如 AzureFlow.data 等
    val paths = path.split(".")
    var tag = this
    for ((idx, pth) in paths.withIndex()) {
        val tmp = tag[pth] ?: return "&7路径 &c$path &7不存在"
        if (tag.tagType(pth) == CompoundTag.TAG_COMPOUND) {
            tag = CompoundTag(tmp)
        } else {
            return if (idx != paths.size - 1) {
                "&7路径 &c$path &7不存在"
            } else {
                tmp
            }
        }
    }
    return tag
}