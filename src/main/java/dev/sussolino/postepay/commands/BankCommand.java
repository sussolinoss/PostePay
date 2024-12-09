package dev.sussolino.postepay.commands;

import dev.sussolino.postepay.api.PostePayAPI;
import dev.sussolino.postepay.profile.Profile;
import dev.sussolino.postepay.file.CurrencyYaml;
import dev.sussolino.postepay.utils.command.AntiSocial;
import dev.sussolino.postepay.utils.command.ServerCommand;
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

public class BankCommand extends ServerCommand implements TabCompleter {

    //bank reload/give/set/take <player> <currency> <amount>

    @Override
    public void execute(@NotNull CommandSender s, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                CurrencyYaml.reload();
                s.sendMessage(Language.BANK_HELP.getString());
            }
            return;
        }

        if (args.length < 4) {
            s.sendMessage(Language.BANK_HELP.getString());
            return;
        }

        Player t = Bukkit.getPlayerExact(args[1]);

        if (t == null) t = Bukkit.getOfflinePlayer(args[1]).getPlayer();

        if (t == null) {
            s.sendMessage(Language.ERRORS_INVALID__PLAYER.getString());
            return;
        }

        String currency = args[2];

        if (CurrencyYaml.getConfig().get(currency) == null) {
            s.sendMessage(Language.ERRORS_INVALID__CURRENCY.getString());
            return;
        }

        double amount = Double.parseDouble(args[3]);

        if (amount < 0 || amount > Double.MAX_VALUE) {
            s.sendMessage(Language.ERRORS_INVALID__AMOUNT.getString());
            return;
        }

        String command = args[0];

        final Profile profile = PostePayAPI.getProfile(t.getName(), currency);

        String msg;

        switch (command) {
            case "give" -> {
                profile.setBalance(profile.getBalance() + amount);
                msg = Language.BANK_GIVE.getString();
            }
            case "set" -> {
                profile.setBalance(amount);
                msg = Language.BANK_SET.getString();
            }
            case "take" -> {
                if (profile.getBalance() < amount) {
                    s.sendMessage(Language.ERRORS_INVALID__MONEY.getString());
                    return;
                }

                profile.take(amount);

                msg = Language.BANK_TAKE.getString();
            }
            default -> msg = Language.BANK_HELP.getString();
        }

        msg = msg
                .replace("{currency}", currency)
                .replace("{player}", t.getName())
                .replace("{amount}", String.valueOf(amount));

        s.sendMessage(msg);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> TAB = new ArrayList<>();
        switch (strings.length) {
            case 1 -> TAB.addAll(List.of("give", "set", "take", "reload"));
            case 2 -> Bukkit.getOnlinePlayers().forEach(p -> TAB.add(p.getName()));
            case 3 -> TAB.addAll(CurrencyYaml.getCurrencies());
            case 4 -> TAB.addAll(List.of("1000","100","10","1"));
        }
        return TAB;
    }
}
