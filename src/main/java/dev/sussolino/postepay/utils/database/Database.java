package dev.sussolino.postepay.utils.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.sussolino.postepay.utils.database.tables.PlayerStats;
import dev.sussolino.postepay.utils.database.tables.Balance;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class Database {

    private final Dao<PlayerStats, String> statsDao;
    private final Dao<Balance, Integer> balanceDao;
    private final ConnectionSource connectionSource;

    @SneakyThrows
    public Database(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        TableUtils.createTableIfNotExists(connectionSource, PlayerStats.class);
        TableUtils.createTableIfNotExists(connectionSource, Balance.class);

        statsDao = DaoManager.createDao(connectionSource, PlayerStats.class);
        balanceDao = DaoManager.createDao(connectionSource, Balance.class);
    }

    public Database(String path) throws SQLException {
        this(new JdbcConnectionSource("jdbc:sqlite:" + path));
    }

    public Database(String url, String username, String password) throws SQLException {
        this(new JdbcConnectionSource(url, username, password));
    }

    public Database(String host, String port, String database,  String username, String password) throws SQLException {
        this("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true", username, password);
    }

    @SneakyThrows
    public void updatePlayerStats(PlayerStats stats) {
        statsDao.createOrUpdate(stats);
    }

    @SneakyThrows
    @NotNull
    public PlayerStats getStats(final String name) {
        PlayerStats stats = statsDao.queryForId(name);
        if (stats == null) {
            stats = new PlayerStats(name);
            updatePlayerStats(stats);
        }
        return stats;
    }

    @SneakyThrows
    public void updateBalance(Balance balance) {
        balanceDao.createOrUpdate(balance);
    }

    @SneakyThrows
    @NotNull
    public Balance getBalance(PlayerStats playerStats, String currency) {
        List<Balance> balances = balanceDao.queryBuilder().where()
                .eq("player_name", playerStats.getName())
                .and()
                .eq("currency", currency)
                .query();
        if (balances.isEmpty()) {
            Balance newBalance = new Balance(playerStats, currency, 0);
            updateBalance(newBalance);
            return newBalance;
        }
        else return balances.get(0);
    }

    @SneakyThrows
    public void close() {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }
}
