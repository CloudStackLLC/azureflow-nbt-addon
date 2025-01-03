package io.rokuko.azureflow.addon.nbt

import io.rokuko.azureflow.api.events.InitEngineEvent
import io.rokuko.azureflow.api.events.registry.IRegistry
import io.rokuko.azureflow.bukkit.event.on
import io.rokuko.azureflow.features.addon.BukkitAddon
import io.rokuko.azureflow.features.logger.log

class NBTAddon: BukkitAddon() {

    override fun register() {
        log("&7正在加载附属 [&fNBT Addon&7] &7中..")

        log("&7正在注册 &f配置项 nbt &7中..")
        on<IRegistry.ProcessorRegistry> {

        }

        log("&7正在注册 &fNBT 相关动作 &7中..")
        on<IRegistry.ResolverRegistry> {

        }

        log("&7正在注册 &fNBT 相关脚本 &7中..")
        on<InitEngineEvent> {

        }

        log("&7正在注册 &fNBT 指令 &7中..")

        log("&7成功加载附属 [&fNBT Addon&7]")
    }

    override fun unRegister() {

    }

}