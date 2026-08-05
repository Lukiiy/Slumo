package me.lukiiy.slumo;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.stream.IntStream;

public class Kit {
    private final String name;
    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final ItemStack offhand;

    private Kit(String name, ItemStack[] items, ItemStack[] armor, ItemStack offhand) {
        this.name = name;
        this.items = items;
        this.armor = armor;
        this.offhand = offhand;
    }

    public String getName() {
        return name;
    }

    public void apply(Player player) {
        PlayerInventory inv = player.getInventory();

        inv.clear();

        IntStream.range(0, items.length).filter(i -> items[i] != null).forEach(i -> inv.setItem(i, items[i].clone()));

        inv.setBoots(cloneOrNull(armor[0]));
        inv.setLeggings(cloneOrNull(armor[1]));
        inv.setChestplate(cloneOrNull(armor[2]));
        inv.setHelmet(cloneOrNull(armor[3]));
        inv.setItemInOffHand(cloneOrNull(offhand));
    }

    private static ItemStack cloneOrNull(ItemStack stack) {
        return stack == null ? null : stack.clone();
    }

    public static final class Builder {
        private final String name;
        private final ItemStack[] items = new ItemStack[36];
        private final ItemStack[] armor = new ItemStack[4];
        private ItemStack offhand;

        public Builder(String name) {
            this.name = name;
        }

        public Builder item(int slot, ItemStack stack) {
            items[slot] = cloneOrNull(stack);
            return this;
        }

        public Builder armor(int slot, ItemStack stack) {
            armor[slot] = cloneOrNull(stack);
            return this;
        }

        public Builder offhand(ItemStack stack) {
            offhand = cloneOrNull(stack);
            return this;
        }

        public Kit build() {
            return new Kit(name, items.clone(), armor.clone(), cloneOrNull(offhand));
        }
    }
}
