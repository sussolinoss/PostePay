package dev.sussolino.postepay.placeholder;

import dev.sussolino.postepay.api.PostePayAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlaceHolderAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "postepay";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sussolino";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Nullable
    public String onPlaceholderRequest(Player p, @NotNull String arg) {
        if (p == null) {
            return "";
        }

        if (arg.contains("balance_")) {
            String economy = arg.substring(8);

            if (!economy.isEmpty()) return String.valueOf(0.0);

            return String.valueOf(PostePayAPI.getProfile(p.getName(), economy).getBalance());
        }
        return "";
    }
}
