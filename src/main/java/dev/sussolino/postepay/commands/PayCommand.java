package dev.sussolino.postepay.commands;

import dev.sussolino.postepay.api.PostePayAPI;
import dev.sussolino.postepay.file.CurrencyYaml;
import dev.sussolino.postepay.utils.command.AntiSocial;
import dev.sussolino.postepay.utils.config.Language;
import dev.sussolino.postepay.utils.reflection.Grrpow;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Grrpow
public class PayCommand extends AntiSocial implements TabCompleter {

    //pay <player> <currency> <amount>

    @Override
    public void execute(Player p, String[] args) {
        if (args.length != 3) {
            p.sendMessage(Language.PAY_HELP.getString());
            return;
        }

        Player t = Bukkit.getPlayerExact(args[0]);

        if (t == null) t = Bukkit.getOfflinePlayer(args[0]).getPlayer();

        if (t == null) {
            p.sendMessage(Language.ERRORS_INVALID__PLAYER.getString());
            return;
        }

        String currency = args[1].toLowerCase();

        if (!CurrencyYaml.getCurrencies().contains(currency)) {
            p.sendMessage(Language.ERRORS_INVALID__CURRENCY.getString());
            return;
        }

        double amount = Double.parseDouble(args[2]);

        if (amount <= 0 || amount > Double.MAX_VALUE) {
            p.sendMessage(Language.ERRORS_INVALID__AMOUNT.getString());
            return;
        }

        double balance = PostePayAPI.getProfile(p.getName(), currency).getBalance();

        if (balance < amount) {
            p.sendMessage(Language.ERRORS_INVALID__AMOUNT.getString());
            return;
        }

        double tBalance = PostePayAPI.getProfile(t.getName(), currency).getBalance();

        String msg;

        if (t.isOnline()) {
            msg = Language.PAY_RECEIVER__MESSAGE.getString();
            msg = msg
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{currency}",String.valueOf(tBalance))
                    .replace("{sender}", p.getName());

            t.getPlayer().sendMessage(msg);
        }

        msg = Language.PAY_SENDER__MESSAGE.getString();
        msg = msg
                .replace("{amount}", String.valueOf(amount))
                .replace("{currency}", String.valueOf(tBalance))
                .replace("{receiver}", t.getName());

        PostePayAPI.getProfile(p.getName(), currency).pay(t.getName(), amount);

        p.sendMessage(msg);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> TAB = new ArrayList<>();
        switch (strings.length) {
            case 1 -> Bukkit.getOnlinePlayers().forEach(p -> TAB.add(p.getName()));
            case 2 -> TAB.addAll(CurrencyYaml.getCurrencies());
            case 3 -> TAB.addAll(List.of("1000","100","10","1"));
        }
        return TAB;
    }
}
