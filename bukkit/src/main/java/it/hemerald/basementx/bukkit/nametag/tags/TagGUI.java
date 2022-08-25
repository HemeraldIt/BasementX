package it.hemerald.basementx.bukkit.nametag.tags;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.item.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class TagGUI implements InventoryProvider {

    private static SmartInventory inventory;

    private final BasementBukkit basement;

    public static SmartInventory getInventory(BasementBukkit basement) {
        if (inventory == null) {
            inventory = SmartInventory.builder()
                    .provider(new TagGUI(basement))
                    .size(6, 9)
                    .title(ChatColor.DARK_GRAY + "Tags")
                    .build();
        }

        return inventory;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        getTagItems(player, contents).thenAccept(tagItems -> {
                    ClickableItem[] items = tagItems.toArray(new ClickableItem[0]);

                    pagination.setItems(items);
                    pagination.setItemsPerPage(27);
                    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1)
                            .blacklist(1, 8)
                            .blacklist(2, 0)
                            .blacklist(3, 0)

                            .blacklist(2, 8)
                            .blacklist(3, 8)

                            .blacklist(4, 1)
                            .blacklist(4, 2)
                            .blacklist(4, 3)
                            .blacklist(4, 4)
                            .blacklist(4, 5)
                            .blacklist(4, 6)
                            .blacklist(4, 7)
                            .blacklist(4, 8)

                            .blacklist(5, 1)
                            .blacklist(5, 2)
                            .blacklist(5, 3)
                            .blacklist(5, 4)
                            .blacklist(5, 5)
                            .blacklist(5, 6)
                            .blacklist(5, 7)
                            .blacklist(5, 8)
                    );


                    if (tagItems.size() > 27) {
                        contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Indietro").build(), e -> {
                            inventory.open(player, pagination.previous().getPage());
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                        }));

                        contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Avanti").build(), e -> {
                            inventory.open(player, pagination.next().getPage());
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                        }));
                    }
                }
        );

        ItemBuilder itemFactory = new ItemBuilder(Material.BARRIER);
        itemFactory.setName(ChatColor.RED + "Indietro");
        contents.set(5, 4, ClickableItem.of(itemFactory.build(), e -> e.getWhoClicked().closeInventory()));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }


    private CompletableFuture<List<ClickableItem>> getTagItems(Player player, InventoryContents contents) {
        return basement.getNameTagModule().getTag(player).thenApply(tag -> {
            List<ClickableItem> items = Lists.newArrayList();
            items.add(getItem(player, contents, tag, ChatColor.GOLD + "\u2B52 ", "firstday"));
            return items;
        });
    }

    private ClickableItem getItem(Player player, InventoryContents contents, String playerTag, String tag, String permission) {
        boolean unlocked = player.hasPermission("basement.tag." + permission);

        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.NAME_TAG.parseItem());

        boolean active = playerTag.equals(tag);

        if (unlocked) {
            itemBuilder.setName((active ? ChatColor.GREEN : ChatColor.GRAY) + tag);

            itemBuilder.addLore("", active ? ChatColor.GREEN + "Attivo" : ChatColor.RED + "Disattivo");
        } else {
            itemBuilder.setName(ChatColor.RED + tag);
            itemBuilder.addLore("", ChatColor.GRAY + "Sbloccala su " + ChatColor.AQUA + "store.mineblock.it");
        }

        return ClickableItem.of(itemBuilder.build(), event -> {
            if (unlocked) {
                Player click = (Player) event.getWhoClicked();
                basement.getPlugin().getServer().getScheduler().runTaskAsynchronously(basement.getPlugin(), () -> {
                    basement.getNameTagModule().setTag(click, active ? "" : tag);
                    basement.getNameTagModule().updateTab(click);
                    basement.getPlugin().getServer().getScheduler().runTask(basement.getPlugin(), () ->
                            inventory.open(click, contents.pagination().getPage()));
                });
            } else
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "TAGS! "
                        + ChatColor.WHITE + "Puoi utilizzare questa Tag solo dopo averla acquistata su " + ChatColor.DARK_AQUA + "store.mineblock.it");
        });
    }
}
