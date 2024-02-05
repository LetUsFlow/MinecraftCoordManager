package net.letusflow.coordmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CoordsCommand implements CommandExecutor, TabCompleter {
    private final Logger logger;
    private final Connection conn;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

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
                            if (strings.length == 5) {
                                try {
                                    pstmt = conn.prepareStatement("INSERT INTO coordinates (uuid, note, coordx, coordy, coordz) VALUES (?,?,?,?,?)");
                                    pstmt.setString(1, player.getUniqueId().toString());
                                    pstmt.setString(2, strings[1]); // note

                                    if (strings[2].equals("~")) { // x
                                        pstmt.setInt(3, (int)player.getX());
                                    } else {
                                        pstmt.setInt(3, Integer.parseInt(strings[2]));
                                    }
                                    if (strings[3].equals("~")) { // y
                                        pstmt.setInt(4, (int)player.getY());
                                    } else {
                                        pstmt.setInt(4, Integer.parseInt(strings[3]));
                                    }
                                    if (strings[4].equals("~")) { // z
                                        pstmt.setInt(5, (int)player.getZ());
                                    } else {
                                        pstmt.setInt(5, Integer.parseInt(strings[4]));
                                    }

                                    pstmt.executeUpdate(); // TODO: handle return value
                                    return true;

                                } catch (NumberFormatException nfe) {
                                    player.sendMessage("Your command is a failure and your are probably one as well >:)");
                                    player.sendMessage("(Could not parse given coordinates as )");
                                }

                            }
                            break;
                        case "remove":
                            if (strings.length == 2) {
                                player.sendMessage("Gonna remove some coords");
                                pstmt = conn.prepareStatement("DELETE FROM coordinates WHERE uuid=? AND note=?");
                                pstmt.setString(1, player.getUniqueId().toString());
                                pstmt.setString(2, strings[1]);
                                pstmt.executeUpdate(); // TODO: handle return value
                                return true;
                            }
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        System.out.println(Arrays.toString(args));
        if (args.length == 1) {
            return List.of("list", "add", "remove");
        }
        if (args.length >= 2 && args[0].equals("add")) {
            if (args.length == 2) {
                return List.of(); // ignore note
            }
            if (args.length == 3) {
                if (args[2].isEmpty()) {
                    return List.of("~ ~ ~", "~ ~ ", "~ ");
                }

                try {
                    int coordx = Integer.parseInt(args[2]);
                    return List.of(coordx + " ~ ~", coordx + " ~ ");
                } catch (NumberFormatException nfe) {
                    return List.of();
                }
            }

            if (args.length == 4) {
                if (args[3].isEmpty()) {
                    return List.of("~ ~", "~ ");
                }

                try {
                    Integer.parseInt(args[2]);
                    int coordy = Integer.parseInt(args[3]);
                    return List.of(coordy + " ~");
                } catch (NumberFormatException nfe) {
                    return List.of();
                }
            }

            if (args.length == 5 && args[4].isEmpty()) {
                return List.of("~");
            }
        }
        return List.of();
    }
}
