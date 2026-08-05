package me.lukiiy.slumo;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.lukiiy.flow.FDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Kits {
    public static final ItemStack CUSTOM_SHEARS;

    static {
        ItemStack stack = ItemStack.of(Material.SHEARS);

        stack.setData(DataComponentTypes.UNBREAKABLE);
        stack.setData(DataComponentTypes.ITEM_NAME, Component.text("Shears (trolling material)").color(FDefaults.LIGHT_YELLOW));
        stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        CUSTOM_SHEARS = stack;
    }

    public static final Kit TROLL = new Kit.Builder("Trolling").item(0, CUSTOM_SHEARS).offhand(new ItemStack(Material.WHITE_WOOL, 64)).build();
    public static final Kit PVP = new Kit.Builder("Sword").armor(0, new ItemStack(Material.DIAMOND_BOOTS)).armor(1, new ItemStack(Material.DIAMOND_LEGGINGS)).armor(2, new ItemStack(Material.DIAMOND_CHESTPLATE)).armor(3, new ItemStack(Material.DIAMOND_HELMET)).item(0, new ItemStack(Material.DIAMOND_SWORD)).build();
    public static final Kit SUMO = new Kit.Builder("Sumo").build();
}