package net.soggysupernova.yawr.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.soggysupernova.yawr.YetAnotherWirelessRedstone;

import java.util.function.Function;


import static net.soggysupernova.yawr.block.ModBlocks.WIRELESS_ENDPOINT;

public class ModItems {



    public static ResourceKey<Item> getRK(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).get();
    }


    public static final Item WIRELESS_LINKER = registerItem("wireless_linker", WirelessLinkerItem::new);
    private static Item registerItem(String name, Function<Item.Properties, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(YetAnotherWirelessRedstone.MOD_ID, name), function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(YetAnotherWirelessRedstone.MOD_ID, name)))));
    }

    /** Wireless Linker
    When clicked on a wireless endpoint, the BlockPos and dimension of the block is stored in the item's custom_data component, and enchantment_glint_override is set to 1.
     Reasons why item components:
     1. Serialization/synchronization is already taken care of
     2. Prevents item stacking of linkers that are linked to different blocks

     wow i sound like claude



     why doc comment: i like green
     */

    public static void registerModItems() {
        YetAnotherWirelessRedstone.LOGGER.info("REgistering");
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(output -> {
            output.accept(WIRELESS_LINKER);
            output.accept(WIRELESS_ENDPOINT);
        });
    }
}
