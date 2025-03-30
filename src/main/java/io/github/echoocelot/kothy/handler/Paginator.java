package io.github.echoocelot.kothy.handler;

import io.github.echoocelot.kothy.api.KothyMessaging;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Paginator {

    public static void paginateList(String title, List<String> list, double numPerPage, int page, Player p, String command, int maxTitleLength) {
        String formattedTitle = formatTitle(title, maxTitleLength);

        int startIndex = (page - 1) * (int) numPerPage;
        int endIndex = Math.min(startIndex + (int) numPerPage, list.size());

        if (startIndex >= list.size() || startIndex < 0) {
            KothyMessaging.sendErrorMessage(p, "Page does not exist!");
            return;
        }

        int numOfPages = (int) Math.ceil(list.size() / numPerPage);

        // Build the full message as a single Component
        Component fullMessage = Component.text()
                .append(Component.text(formattedTitle + "\n", NamedTextColor.GOLD))
                .build();

        for (int i = startIndex; i < endIndex; i++) {
            fullMessage = fullMessage.append(Component.text(list.get(i) + "\n", NamedTextColor.AQUA));
        }

        Component navigation = getNavigation(page, numOfPages, command);

        fullMessage = fullMessage.append(navigation);

        p.sendMessage(fullMessage);
    }

    public static void paginateList(String title, String label, List<String> list, double numPerPage, int page, Player p, String command, int maxTitleLength) {
        String formattedTitle = formatTitle(title, maxTitleLength);

        int startIndex = (page - 1) * (int) numPerPage;
        int endIndex = Math.min(startIndex + (int) numPerPage, list.size());

        if (startIndex >= list.size() || startIndex < 0) {
            KothyMessaging.sendErrorMessage(p, "Page does not exist!");
            return;
        }

        int numOfPages = (int) Math.ceil(list.size() / numPerPage);

        // Build the full message as a single Component
        Component fullMessage = Component.text()
                .append(Component.text(formattedTitle + "\n", NamedTextColor.GOLD))
                .append(Component.text(label + "\n", NamedTextColor.GOLD))
                .build();

        for (int i = startIndex; i < endIndex; i++) {
            fullMessage = fullMessage.append(Component.text(list.get(i) + "\n", NamedTextColor.AQUA));
        }

        Component navigation = getNavigation(page, numOfPages, command);

        fullMessage = fullMessage.append(navigation);

        p.sendMessage(fullMessage);
    }


    private static Component getNavigation(int page, int totalPages, String command) {
        Component navigation = Component.empty();

        if (page > 1) {
            Component prevButton = Component.text("[Previous Page] ", NamedTextColor.GOLD)
                    .clickEvent(ClickEvent.runCommand(command + " " + (page - 1)));
            navigation = navigation.append(prevButton);
        }

        Component pageNum = Component.text(" ---.[Page " + page + " of " + totalPages + "].--- ", NamedTextColor.GOLD);
        navigation = navigation.append(pageNum);

        if (page < totalPages) {
            Component nextButton = Component.text("[Next Page]", NamedTextColor.GOLD)
                    .clickEvent(ClickEvent.runCommand(command + " " + (page + 1)));
            navigation = navigation.append(nextButton);
        }

        return navigation;
    }

    public static String formatTitle(String title, int maxTitleLength) {
        int titleLength = title.length();
        int dashLength = (maxTitleLength - titleLength - 4) / 2;  // Subtract 4 for the dots and brackets

        if (dashLength < 0) dashLength = 0;  // Prevent negative dashes if title is too long

        String dashes = "-".repeat(dashLength);
        return dashes + ".[" + title + "]." + dashes;
    }


}
