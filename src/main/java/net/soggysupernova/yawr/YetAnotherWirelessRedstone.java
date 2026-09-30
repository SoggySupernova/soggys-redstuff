package net.soggysupernova.yawr;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.soggysupernova.yawr.block.ModBlocks;
import net.soggysupernova.yawr.block.entity.ModBlockEntities;
import net.soggysupernova.yawr.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class YetAnotherWirelessRedstone implements ModInitializer {
	public static final String MOD_ID = "yet-another-wireless-redstone";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
