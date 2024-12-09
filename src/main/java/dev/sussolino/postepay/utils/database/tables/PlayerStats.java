package dev.sussolino.postepay.utils.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import com.j256.ormlite.dao.ForeignCollection;

@DatabaseTable(tableName = "players")
@Getter
@Setter
public class PlayerStats {

    @DatabaseField(id = true) private String name;

    @ForeignCollectionField(eager = true) private ForeignCollection<Balance> balances;

    public PlayerStats() {}

    public PlayerStats(final String name) {
        this.name = name;
    }
}
