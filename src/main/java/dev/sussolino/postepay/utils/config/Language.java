package dev.sussolino.postepay.utils.config;

import dev.sussolino.postepay.file.LanguageYaml;
import dev.sussolino.postepay.utils.color.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Language {

    ERRORS_INVALID__AMOUNT(),
    ERRORS_INVALID__CURRENCY(),
    ERRORS_INVALID__PLAYER(),
    ERRORS_INVALID__MONEY(),

    PAY_HELP(),
    PAY_SENDER__MESSAGE(),
    PAY_RECEIVER__MESSAGE(),

    BALANCE(),
    INVENTORY(),

    BANK_HELP(),
    BANK_RELOAD(),
    BANK_GIVE(),
    BANK_SET(),
    BANK_TAKE();

    private final FileConfiguration yaml;
    private final String path;

    Language() {
        this.yaml = LanguageYaml.getConfig();
        this.path = name()
                .replace("__", "-")
                .replace('_', '.')
                .toLowerCase();
    }

    public String getString() {
        return ColorUtils.color(yaml.getString(path));
    }
    public List<String> getStringList() {
        return ColorUtils.colorList(yaml.getStringList(path));
    }
}
