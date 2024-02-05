package net.letusflow.coordsmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class CoordsCommand implements CommandExecutor {
    private final Logger logger;
    private final Connection conn;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            logger.info(player.getUniqueId().toString());
            if (strings.length > 0) {
                try {

                    switch (strings[0]) {
                        case "list":
                            player.sendMessage("Gonna list some coords");

                            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM coordinates WHERE uuid = ?");
                            pstmt.setString(1, player.getUniqueId().toString());
                            ResultSet rs = pstmt.executeQuery();

                            while (rs.next()) {
                                player.sendMessage(rs.getString("note") + ": " + rs.getInt("coordx") + " " + rs.getInt("coordy") + " " + rs.getInt("coordz"));
                            }
                            return true;
                        case "add":
                            player.sendMessage("Gonna add some coords");

                            //PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ");

                            break;
                        case "remove":
                            player.sendMessage("Gonna remove some coords");
                            break;
                    }
                } catch (SQLException e) {
                    logger.severe(e.getMessage());
                }


            }
        }
        commandSender.sendMessage("Your command is a failure and your are probably one as well >:)");
        return false;
    }

    public CoordsCommand(Logger logger, Connection conn) {
        this.logger = logger;
        this.conn = conn;
    }
}
