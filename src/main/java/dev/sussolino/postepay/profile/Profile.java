package dev.sussolino.postepay.profile;

import dev.sussolino.postepay.PostePay;
import dev.sussolino.postepay.api.PostePayAPI;
import dev.sussolino.postepay.file.PlayersYaml;
import dev.sussolino.postepay.utils.database.tables.Balance;
import dev.sussolino.postepay.utils.database.tables.PlayerStats;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Profile {

    private final String name;
    private final String currency;

    private final FileConfiguration yaml;

    public Profile(String playerName, String currency) {
        this.name = playerName;
        this.currency = currency;
        this.yaml = PlayersYaml.getConfig();
    }

    public double getBalance() {
        if (PostePay.DATABASE_ENABLED) {
            PlayerStats stats = PostePay.DATABASE.getStats(name);
            Balance BALANCE = PostePay.DATABASE.getBalance(stats, currency);
            return BALANCE.getBalance();
        }
        return yaml.getDouble(name + "." + currency);
    }

    public void setBalance(double balance) {
        if (PostePay.DATABASE_ENABLED) {
            PlayerStats stats = PostePay.DATABASE.getStats(name);
            Balance BALANCE = PostePay.DATABASE.getBalance(stats, currency);
            BALANCE.setBalance(balance);

            PostePay.DATABASE.updatePlayerStats(stats);
            PostePay.DATABASE.updateBalance(BALANCE);
            return;
        }
        yaml.set(name + "." + currency, balance);
        PlayersYaml.reload();
    }
    public void add(double amount) {
        final double total = getBalance() + amount;
        if (PostePay.DATABASE_ENABLED) {
            PlayerStats stats = PostePay.DATABASE.getStats(name);
            Balance BALANCE = PostePay.DATABASE.getBalance(stats, currency);

            BALANCE.setBalance(total);

            PostePay.DATABASE.updatePlayerStats(stats);
            PostePay.DATABASE.updateBalance(BALANCE);
            return;
        }
        yaml.set(name + "." + currency, total);
        PlayersYaml.reload();
    }

    public void take(double amount) {
        if (PostePay.DATABASE_ENABLED) {
            PlayerStats stats = PostePay.DATABASE.getStats(name);
            Balance BALANCE = PostePay.DATABASE.getBalance(stats, currency);
            BALANCE.setBalance(BALANCE.getBalance() - amount);

            PostePay.DATABASE.updatePlayerStats(stats);
            PostePay.DATABASE.updateBalance(BALANCE);
            return;
        }
        setBalance(getBalance() - amount);
    }

    public void pay(String receiver, double amount) {
        if (PostePay.DATABASE_ENABLED) {
            PlayerStats stats = PostePay.DATABASE.getStats(name);
            Balance BALANCE = PostePay.DATABASE.getBalance(stats, currency);
            Balance RECEIVER = PostePay.DATABASE.getBalance(PostePay.DATABASE.getStats(name), currency);

            RECEIVER.setBalance(RECEIVER.getBalance() + amount);
            BALANCE.setBalance(BALANCE.getBalance() - amount);

            PostePay.DATABASE.updatePlayerStats(stats);
            PostePay.DATABASE.updateBalance(BALANCE);

            PostePay.DATABASE.updatePlayerStats(stats);
            PostePay.DATABASE.updateBalance(BALANCE);
            return;
        }
        Profile pc = PostePayAPI.getProfile(receiver, currency);

        take(amount);

        pc.setBalance(pc.getBalance() + amount);
    }
}
