package io.rokuko.azureflow.addon.nbt.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import getProperty
import io.rokuko.azureflow.AzureFlowBukkit
import io.rokuko.azureflow.addon.nbt.NBTAddon
import io.rokuko.azureflow.bukkit.command.Executor
import io.rokuko.azureflow.features.logger.logError
import io.rokuko.azureflow.utils.colorful
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin
import java.lang.reflect.Constructor

object NBTCommand {

    private val commandMap by lazy { Bukkit.getPluginManager().getProperty<CommandMap>("commandMap")!! }

    private val knownCommands by lazy {
        commandMap.getProperty<MutableMap<String, Command>>("knownCommands")!!
    }

    private val constructor: Constructor<PluginCommand> by lazy {
        PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java).also {
            it.isAccessible = true
        }
    }

    const val mainCommand = "afnbt"

    private val pluginCommand: PluginCommand = constructor.newInstance(mainCommand, NBTAddon.instance).apply {
        permission = "azureflow.nbt.admin"
        aliases = listOf("azureflownbt", "nbt")
    }

    private val commands = mapOf(
        "show" to nbtShowCommand
    )

    private fun help(sender: CommandSender) {
        sender.sendMessage("")
        sender.sendMessage("  &f&l${NBTAddon.instance.name} &7v${NBTAddon.instance.description.version}".colorful())
        sender.sendMessage("")
        sender.sendMessage("  &7命令: &f/afnbt &8[...]".colorful())
        sender.sendMessage("  &7参数:".colorful())
        for (command in commands) {
            if (AzureFlowBukkit.server.bukkitVersion <= "1.13") {
                sender.sendMessage("    &8- &f${command.key} ${command.value["tag"] ?: ""}".colorful())
            } else {
                // 判断params是否为空, 为空则为空字符串, 否则为list用空格拼接, 并且头部也有空格
                val params = ((command.value["params"] as? List<String>)?.joinToString(" ", prefix = " ")) ?: ""
                val component =
                    ComponentBuilder().append("    &8- &f${command.key}${params} ${command.value["tag"] ?: ""}".colorful())
                        .event(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/af ${command.key} ")).create()
                sender.spigot().sendMessage(*component)
            }
            sender.sendMessage("      &7${command.value["description"]}".colorful())
        }
        sender.sendMessage("")
    }

    fun register() {
        knownCommands[mainCommand] = pluginCommand

        pluginCommand.setExecutor { commandSender, command, label, _args ->
            val args = _args.toList()
            if (args.isEmpty()) {
                help(commandSender)
                return@setExecutor true
            }
            val sub = args[0]
            if (sub !in commands.keys) {
                help(commandSender)
                return@setExecutor true
            }
            val subCommand = commands[sub] ?: return@setExecutor true
            val executor = subCommand["executor"] as? Executor ?: return@setExecutor true
            try {
                executor.invoke(commandSender, args.subList(1, args.size))
            } catch (e: IllegalArgumentException) {
                logError("&7命令执行失败! (&c错误: ${e.message}&7)", commandSender)
            }
            return@setExecutor true
        }

        pluginCommand.setTabCompleter { sender, command, alias, _args ->
            val args = _args.toList().slice(1 until _args.size)
            val subCommand = _args[0]

            // e.g. /af
            if (subCommand.isEmpty()) {
                commands.keys.toList()
            } else if (subCommand !in commands.keys) {
                commands.keys.filter { it.startsWith(subCommand) }
            } else if (subCommand in commands.keys) {
                when (subCommand) {
                    "show" -> {
                        emptyList()
                    }
                    else -> emptyList()
                }
            } else {
                emptyList()
            }
        }

    }

}