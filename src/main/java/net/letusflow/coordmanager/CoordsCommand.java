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
                            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM coordinates WHERE uuid = ?");
                            pstmt.setString(1, player.getUniqueId().toString());
                            ResultSet rs = pstmt.executeQuery();

                            int ct = 0;
                            while (rs.next()) {
                                player.sendMessage(rs.getString("note") + ": " + rs.getInt("coordx") + " " + rs.getInt("coordy") + " " + rs.getInt("coordz"));
                                ct++;
                            }

                            if (ct == 0) {
                                player.sendMessage("You don't have any stored coordinates");
                            }

                            return true;
                        case "add":
                            if (strings.length == 5) {
                                try {
                                    pstmt = conn.prepareStatement("INSERT INTO coordinates (uuid, note, coordx, coordy, coordz) VALUES (?,?,?,?,?)");
                                    pstmt.setString(1, player.getUniqueId().toString());
                                    pstmt.setString(2, strings[1]); // note

                                    int posX;
                                    int posY;
                                    int posZ;

                                    if (strings[2].equals("~")) { // x
                                        posX = (int)player.getX();
                                    } else {
                                        posX = Integer.parseInt(strings[2]);
                                    }
                                    if (strings[3].equals("~")) { // y
                                        posY = (int)player.getY();
                                    } else {
                                        posY = Integer.parseInt(strings[3]);
                                    }
                                    if (strings[4].equals("~")) { // z
                                        posZ = (int)player.getZ();
                                    } else {
                                        posZ = Integer.parseInt(strings[4]);
                                    }

                                    pstmt.setInt(3,posX);
                                    pstmt.setInt(4,posY);
                                    pstmt.setInt(5,posZ);

                                    if (pstmt.executeUpdate() != 0) {
                                        player.sendMessage(String.format("Successfully added coordinates %d %d %d :)", posX, posY, posZ));
                                    } else {
                                        player.sendMessage("Couldn't add specified coordinates :(");
                                        player.sendMessage("The note used in your command probably alread exists");
                                    }
                                    return true;

                                } catch (NumberFormatException nfe) {
                                    player.sendMessage("Could not parse given coordinates");
                                }

                            }
                            break;
                        case "remove":
                            if (strings.length == 2) {
                                pstmt = conn.prepareStatement("DELETE FROM coordinates WHERE uuid=? AND note=?");
                                pstmt.setString(1, player.getUniqueId().toString());
                                pstmt.setString(2, strings[1]);

                                if (pstmt.executeUpdate() != 0) {
                                    player.sendMessage("Successfully removed specified coordinates :)");
                                } else {
                                    player.sendMessage("Couldn't remove specified coordinates :(");
                                    player.sendMessage("You probably have a typo in the command");
                                }
                                return true;
                            }
                            break;
                        case "clear":
                            pstmt = conn.prepareStatement("DELETE FROM coordinates WHERE uuid=?");
                            pstmt.setString(1, player.getUniqueId().toString());

                            if (pstmt.executeUpdate() != 0) {
                                player.sendMessage("Successfully removed all coordinates :)");
                            } else {
                                player.sendMessage("Couldn't remove any coordinates BECAUSE YOU DIDN'T HAVE ANY STORED >:(");
                            }
                            return true;
                    }
                } catch (SQLException e) {
                    logger.severe(e.getMessage());
                }


            }
        }
        commandSender.sendMessage("Your command is a failure and your are probably one as well >:)");
        commandSender.sendMessage("aka Syntax Error");
        return false;
    }

    public CoordsCommand(Logger logger, Connection conn) {
        this.logger = logger;
        this.conn = conn;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return List.of("list", "add", "remove", "clear");
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
