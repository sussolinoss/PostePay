package dev.sussolino.postepay.commands;

import dev.sussolino.postepay.file.CurrencyYaml;
import dev.sussolino.postepay.utils.command.AntiSocial;
import dev.sussolino.postepay.utils.reflection.Grrpow;
import org.bukkit.entity.Player;

@Grrpow
public class CurrencyCommand extends AntiSocial {

    @Override
    public void execute(Player p, String[] args) {
        if (args.length != 1) {
            //TODO: INVALID COMMAND
            return;
        }
        String currency = args[0];

        if (CurrencyYaml.getCurrencies().contains(currency)) {
            p.sendMessage("Hai già sta currency mongoloide");
            return;
        }

        CurrencyYaml.getConfig().set(currency, currency);
        CurrencyYaml.reload();

        p.sendMessage("Currency creata: " + currency);
    }
}
