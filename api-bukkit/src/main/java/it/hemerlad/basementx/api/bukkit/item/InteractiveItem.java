package it.hemerlad.basementx.api.bukkit.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class InteractiveItem {

    private static final String KEY = "listener";

    private final ItemBuilder itemBuilder;
    private final String data;
    @Getter private final ItemClickListener listener;
    private final BiConsumer<ItemBuilder, Player> consumer;

    public InteractiveItem(ItemBuilder itemBuilder, String data, ItemClickListener listener) {
        this(itemBuilder, data, listener, (itemBuilder1, player) -> {
        });
    }

    public InteractiveItem(ItemBuilder itemBuilder, String data) {
        this(itemBuilder, data, ItemClickListener.EMPTY);
    }

    public static String getKey(ItemStack itemStack, ItemDataManager itemDataManager) {
        return itemDataManager.getData(itemStack, KEY);
    }

    public ItemStack getItemStack(ItemDataManager itemDataManager, Player player) {
        consumer.accept(itemBuilder, player);

        ItemStack itemStack = itemBuilder.build();
        itemDataManager.setData(itemStack, KEY, data);
        return itemStack;
    }
}
