package net.letusflow.coordsmanager;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

public final class Main extends JavaPlugin {
    private Connection conn;

    @Override
    public void onEnable() {
        File dbfile = new File(getDataFolder(), "coords.db");

        // create new config folder if not exists
        if (!dbfile.getParentFile().exists()) {
            dbfile.getParentFile().mkdirs();
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbfile.getPath())) {
            if (conn != null) {
                this.conn = conn;

                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS coordinates (\n" +
                        "uuid STRING NOT NULL PRIMARY KEY,\n" +
                        "note STRING NOT NULL,\n" +
                        "coordx INTEGER NOT NULL,\n" +
                        "coordy INTEGER NOT NULL,\n" +
                        "coordz INTEGER NOT NULL\n" +
                        ");");

                getCommand("coords").setExecutor(new CoordsCommand(getLogger(), conn));
            }

        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }

    }

    @Override
    public void onDisable() {
        try {
            conn.close();
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }
}
