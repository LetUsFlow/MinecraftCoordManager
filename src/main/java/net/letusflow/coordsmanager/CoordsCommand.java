package net.letusflow.coordsmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.logging.Logger;

public class CoordsCommand implements CommandExecutor {
    private final Logger logger;
    private final Connection conn;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            player.sendMessage("Your UUID is " + player.getUniqueId());
        }
        return false;
    }

    public CoordsCommand(Logger logger, Connection conn) {
        this.logger = logger;
        this.conn = conn;
    }
}
