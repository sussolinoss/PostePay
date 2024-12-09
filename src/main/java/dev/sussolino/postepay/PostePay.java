package dev.sussolino.postepay;

import dev.sussolino.postepay.commands.BankCommand;
import dev.sussolino.postepay.file.CurrencyYaml;
import dev.sussolino.postepay.file.LanguageYaml;
import dev.sussolino.postepay.file.PlayersYaml;
import dev.sussolino.postepay.file.SettingsYaml;
import dev.sussolino.postepay.placeholder.PlaceHolderAPI;
import dev.sussolino.postepay.utils.database.Database;
import dev.sussolino.postepay.utils.reflection.ReflectionUtil;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class PostePay extends JavaPlugin {

    public static PostePay INSTANCE;
    public static Database DATABASE;
    public static PlaceHolderAPI PLACE_HOLDER;

    public static boolean DATABASE_ENABLED = true;

    private final String LOGO =
            """
                    /\\  _`\\              /\\ \\__       /\\  _`\\                   \s
                    \\ \\ \\L\\ \\___     ____\\ \\ ,_\\    __\\ \\ \\L\\ \\ __     __  __   \s
                     \\ \\ ,__/ __`\\  /',__\\\\ \\ \\/  /'__`\\ \\ ,__/'__`\\  /\\ \\/\\ \\  \s
                      \\ \\ \\/\\ \\L\\ \\/\\__, `\\\\ \\ \\_/\\  __/\\ \\ \\/\\ \\L\\.\\_\\ \\ \\_\\ \\ \s
                       \\ \\_\\ \\____/\\/\\____/ \\ \\__\\ \\____\\\\ \\_\\ \\__/.\\_\\\\/`____ \\\s
                        \\/_/\\/___/  \\/___/   \\/__/\\/____/ \\/_/\\/__/\\/_/ `/___/> \\
                                                                           /\\___/
                                                                           \\/__/\s
            """;

    @SneakyThrows
    public Database getDatabase() {
        final String db = SettingsYaml.getConfig().getString("database");
        final FileConfiguration config = SettingsYaml.getConfig();

        boolean sql = SettingsYaml.getConfig().getBoolean("sql.enabled");
        boolean mysql = SettingsYaml.getConfig().getBoolean("mysql.enabled");

        if (sql && mysql) {
            Bukkit.getLogger().severe(
                    LOGO +
                            """
                            
                            (!) You can select only one type of database (!)
                            """);
            Bukkit.shutdown();
            return null;
        }
        if (!sql && !mysql) {
            DATABASE_ENABLED = false;
            return null;
        }

        if (sql) return new Database(INSTANCE.getDataFolder() + "/" + db + ".db");
        else return new Database(
                config.getString("mysql.host"),
                config.getString("mysql.port"),
                db,
                config.getString("mysql.username"),
                config.getString("mysql.password"));
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        INSTANCE = this;

        Bukkit.getLogger().info(
                LOGO +
                    """
        
                               The best solution for your Server!
                                      made by sussolino
                                         (with love)
        
                    """);


        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
           PLACE_HOLDER = new PlaceHolderAPI();
           PLACE_HOLDER.register();
        }

        init();
    }


    @Override
    public void onDisable() {
        INSTANCE = null;

        PLACE_HOLDER.unregister();

        if (DATABASE_ENABLED) DATABASE.close();
    }

    private void init() {
        // -- Files --
        SettingsYaml.init();
        CurrencyYaml.init();
        LanguageYaml.init();
        PlayersYaml.init();
        // -- Files --

        getCommand("bank").setExecutor(new BankCommand());
        getCommand("bank").setTabCompleter(new BankCommand());

        ReflectionUtil.register("dev.sussolino.postepay", this);

        // -- Database --
        DATABASE = DATABASE_ENABLED ? getDatabase() : null;
        // -- Database --
    }
}
