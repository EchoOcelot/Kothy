package io.github.echoocelot.kothy.api;


import io.github.echoocelot.kothy.Kothy;
import io.github.echoocelot.kothy.handler.FutureGameDBHandler;
import io.github.echoocelot.kothy.handler.ResultsDBHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import io.papermc.paper.plugin.configuration.PluginMeta;

import java.awt.Color;

public class KothyMessaging {
    static Kothy plugin = JavaPlugin.getPlugin(Kothy.class);
    static PluginMeta meta = plugin.getPluginMeta();
    public static final Color PLUGIN_COLOR = new Color(0x7851A9);
    public static final Component PLUGIN_WORDMARK_COMPONENT = Component.text("Kothy", TextColor.color(PLUGIN_COLOR.getRGB()));
    public static final Component PREFIX_COMPONENT = PLUGIN_WORDMARK_COMPONENT.append(Component.text(" » ").color(NamedTextColor.DARK_GRAY));

    public static void sendMessage(@NotNull Player player, @NotNull String message) {
        Component formattedMessage = PREFIX_COMPONENT.append(Component.text(message, NamedTextColor.YELLOW));
        player.sendMessage(formattedMessage);
    }

    public static void sendErrorMessage(@NotNull Player player, @NotNull String message) {
        Component formattedMessage = PREFIX_COMPONENT.append(Component.text(message, NamedTextColor.RED));
        player.sendMessage(formattedMessage);
    }

    public static void sendErrorMessageToSender(@NotNull CommandSender sender, @NotNull String message) {
        Component formattedMessage = PREFIX_COMPONENT.append(Component.text(message, NamedTextColor.RED));
        sender.sendMessage(formattedMessage);
    }

    public static void sendInfoMessage(@NotNull Player player) {
        Component infoMessage = Component.text()
                .append(Component.text("Kothy\n", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("Author: ", NamedTextColor.GOLD))
                .append(Component.text("EchoOcelot\n", NamedTextColor.GRAY))
                .append(Component.text("Version: ", NamedTextColor.GOLD))
                .append(Component.text(meta.getVersion() + "\n", NamedTextColor.GRAY))
                .append(Component.text("Past Games: ", NamedTextColor.GOLD))
                .append(Component.text(ResultsDBHandler.getNumResults() + "\n", NamedTextColor.GRAY))
                .append(Component.text("Scheduled Games: ", NamedTextColor.GOLD))
                .append(Component.text(FutureGameDBHandler.getNumGames() + "\n", NamedTextColor.GRAY))
                .append(Component.text("Wiki", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/"))).appendSpace()
                .build();
        player.sendMessage(infoMessage);
    }
}
