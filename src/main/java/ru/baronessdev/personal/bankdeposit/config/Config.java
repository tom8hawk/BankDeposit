package ru.baronessdev.personal.bankdeposit.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class Config extends YAML {
    public static Config inst;

    public Config() {
        Initialize("config.yml");
        inst = this;
    }

    public String getMessage(String path) {
        String result = configuration.getString(path);
        return ChatColor.translateAlternateColorCodes('&', result != null ? result : "");
    }

    public List<String> getList(String path) {
        return configuration.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }

    public ConfigurationSection getSelection(String path) {
        return configuration.getConfigurationSection(path);
    }

    public Double getDouble(String path) {
        return configuration.getDouble(path);
    }
}