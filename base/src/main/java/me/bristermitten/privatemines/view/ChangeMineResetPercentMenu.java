package me.bristermitten.privatemines.view;

import me.bristermitten.privatemines.PrivateMines;
import me.bristermitten.privatemines.config.PMConfig;
import me.bristermitten.privatemines.config.menu.MenuConfig;
import me.bristermitten.privatemines.config.menu.MenuSpec;
import me.bristermitten.privatemines.data.PrivateMine;
import me.bristermitten.privatemines.service.MineStorage;
import me.bristermitten.privatemines.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChangeMineResetPercentMenu {

    private static MenuSpec original;

    /*
        Loads the configuration for the change resets menu
     */

    ChangeMineResetPercentMenu(Player p, PrivateMines plugin, PMConfig config, MenuConfig menuConfig, MineStorage storage) {
        if (original == null) {
            original = new MenuSpec();
            original.loadFrom(menuConfig.configurationForName("Reset-Percents"));
        }

        MenuSpec spec = new MenuSpec();
        spec.copyFrom(original);
        spec.register(plugin);
        PrivateMine mine = storage.get(p);

        Integer[] resetPercentage = config.getResetPercentages().toArray(new Integer[]{});

        p.openInventory(spec.genMenu((percent, i) -> {
                    i.setType(Material.REDSTONE);
                    ItemMeta itemMeta = i.getItemMeta();

                    if (itemMeta != null && itemMeta.hasDisplayName()) {
                        String displayName = itemMeta.getDisplayName();
                        String name = displayName.replace("%percent%",
                                Util.parsePercent(percent));
                        itemMeta.setDisplayName(name);
                    }
                    List<String> lore = null;
                    if (itemMeta != null) {
                        lore = Objects.requireNonNull(itemMeta.getLore()).stream().map(s -> s.replace("%percent%",
                                String.valueOf(percent))).collect(Collectors.toList());
                    }
                    if (itemMeta != null) {
                        itemMeta.setLore(lore);
                    }
                    i.setItemMeta(itemMeta);
                    return i;
                },
                decreasepercent -> e -> {
                    if (p.hasPermission("privatemine.percent." + decreasepercent)) {
                        p.sendMessage(ChatColor.GREEN + "Decreased reset percent by  " + decreasepercent + "%");
                        if (mine != null) {
                            mine.decreaseResetPercentage(decreasepercent);
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "No access to this percent!");
                    }
                }, resetPercentage));
    }
}
