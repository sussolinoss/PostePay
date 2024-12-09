package dev.sussolino.postepay.utils.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable(tableName = "balance")
@Getter
@Setter
public class Balance {
    @DatabaseField(generatedId = true, canBeNull = false, columnName = "id")
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "player_name")
    private PlayerStats player;

    @DatabaseField(canBeNull = false)
    private String currency;

    @DatabaseField(canBeNull = false)
    private double balance;

    public Balance() {}

    public Balance(PlayerStats player, String currency, double balance) {
        this.player = player;
        this.currency = currency;
        this.balance = balance;
    }
}
