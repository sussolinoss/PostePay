package dev.sussolino.postepay.commands;

import dev.sussolino.postepay.api.PostePayAPI;
import dev.sussolino.postepay.file.CurrencyYaml;
import dev.sussolino.postepay.profile.Profile;
import dev.sussolino.postepay.utils.color.ColorUtils;
import dev.sussolino.postepay.utils.command.AntiSocial;
import dev.sussolino.postepay.utils.config.Language;
import dev.sussolino.postepay.utils.reflection.Grrpow;
import dev.sussolino.postepay.utils.reflection.SpartanAC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Grrpow
@SpartanAC
public class BalanceCommand extends AntiSocial implements TabCompleter, Listener {

    //balance <currency> || /balance <player> <currency>

    @Override
    public void execute(Player p, String[] args) {
        if (args.length == 0) {
            Inventory currencies = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, Language.INVENTORY.getString());

            final int[] i = {0};

            CurrencyYaml.getCurrencies().forEach(currency -> {
                if (i[0] <= 27) {
                    var item = item(Material.SUNFLOWER, currency);
                    currencies.addItem(item);
                    i[0]++;
                }
            });

            p.openInventory(currencies);
        }

        else if (args.length == 1) {
            String currency = args[0].toLowerCase();

            if (!CurrencyYaml.getCurrencies().contains(currency)) {
                p.sendMessage(Language.ERRORS_INVALID__CURRENCY.getString());
                return;
            }

            double balance = PostePayAPI.getProfile(p.getName(), currency).getBalance();

            String msg = Language.BALANCE.getString().replace("{balance}", String.valueOf(balance));

            p.sendMessage(msg);
        }
        else if (args.length == 2) {
            String currency = args[0].toLowerCase();

            if (!CurrencyYaml.getCurrencies().contains(currency)) {
                p.sendMessage(Language.ERRORS_INVALID__CURRENCY.getString());
                return;
            }

            Player t = Bukkit.getPlayerExact(args[1]);

            if (t == null) t = Bukkit.getOfflinePlayer(args[1]).getPlayer();

            if (t == null) {
                p.sendMessage(Language.ERRORS_INVALID__PLAYER.getString());
                return;
            }

            double balance = PostePayAPI.getProfile(t.getName(), currency).getBalance();

            String msg = Language.BALANCE.getString().replace("{balance}", String.valueOf(balance));

            p.sendMessage(msg);
        }
    }

    /**
     *
     *     TAB COMPLETER
     *
     */

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> TAB = new ArrayList<>();
        switch (strings.length) {
            case 1 -> TAB.addAll(CurrencyYaml.getCurrencies());
            case 2 -> Bukkit.getOnlinePlayers().forEach(p -> TAB.add(p.getName()));
        }
        return TAB;
    }


    /**
     *
     *     ITEM UTIL
     *
     */


    private ItemStack item(Material material, String display) {
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ColorUtils.color(display));
        i.setItemMeta(meta);
        return i;
    }

    /**
     *
     *     CURRENCIES INVENTORY EVENT
     *
     */

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        var item = e.getCurrentItem();

        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String currency = item.getItemMeta().getDisplayName();

        if (item.getType().equals(Material.SUNFLOWER) && CurrencyYaml.getCurrencies().contains(currency)) {

            String playerName = e.getView().getPlayer().getName();

            Player p = Bukkit.getPlayerExact(playerName);
            Profile profile = PostePayAPI.getProfile(playerName, currency);

            p.closeInventory();

            p.sendMessage(Language.BALANCE.getString().replace("{balance}",String.valueOf(profile.getBalance())));

            e.setCancelled(true);
        }
    }
}

