package com.github.arboriginal.ScaffoldingBlocks;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    boolean      fall, sneak, silk;
    List<String> alts, nulls, tools;

    // JavaPlugin methods ----------------------------------------------------------------------------------------------

    @Override
    public void onEnable() {
        super.onEnable();
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sb-reload")) {
            reloadConfig();
            sender.sendMessage("§7[§6ScaffoldingBlocks§7] §aConfiguration reloaded.");
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        saveDefaultConfig();

        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        alts  = config.getStringList("altScaffoldings");
        fall  = config.getBoolean("falling");
        nulls = config.getStringList("ignoredBlocks");
        silk  = config.getBoolean("silkDrop");
        sneak = config.getBoolean("sneakDisable");
        tools = config.getStringList("offHandTools");
    }
}
