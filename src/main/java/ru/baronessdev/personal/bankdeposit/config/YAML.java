package ru.baronessdev.personal.bankdeposit.config;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.baronessdev.personal.bankdeposit.BankDeposit;

import java.io.File;
import java.io.IOException;

public abstract class YAML {
    protected static final BankDeposit plugin = BankDeposit.inst;
    protected final YamlConfiguration configuration = new YamlConfiguration();
    protected File file;

    public void Initialize(String fileName) {
        String path = plugin.getDataFolder() + "/" + fileName;
        file = new File(path);

        if (!file.exists())
            plugin.saveResource(fileName, true);

        try {
            configuration.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
