package dev.sussolino.postepay.api;

import dev.sussolino.postepay.file.CurrencyYaml;
import dev.sussolino.postepay.profile.Profile;
import org.bukkit.Bukkit;

import java.util.List;

public class PostePayAPI {

    /**
     *
     *   PROFILE
     *
     */

    public static Profile getProfile(String playerName, String currencyName) {
       return new Profile(playerName, currencyName);
    }

    /**
     *
     *   CURRENCY
     *
     */

    public static List<String> getCurrencies() {
        return CurrencyYaml.getCurrencies();
    }
    public static void createCurrency(String currencyName) {
        if (CurrencyYaml.getCurrencies().contains(currencyName)) {
            Bukkit.getLogger().warning("[PostePay] Currency " + currencyName + " already exists");
            return;
        }
        CurrencyYaml.getConfig().set(currencyName, currencyName);
        CurrencyYaml.reload();
    }
}
