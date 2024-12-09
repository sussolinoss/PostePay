package dev.sussolino.postepay.file;

import dev.sussolino.postepay.PostePay;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class CurrencyYaml {

    private static File file;
    @Getter
    private static FileConfiguration config;

    @SneakyThrows
    public static void save() {
        config.save(file);
    }

    public static void reload() {
        save();
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void init() {
        file = new File(PostePay.INSTANCE.getDataFolder(), "currency.yml");

        if (!file.exists()) {
            PostePay.INSTANCE.saveResource("currency.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static List<String> getCurrencies() {
        return config.getKeys(false).stream().toList();
    }
}
