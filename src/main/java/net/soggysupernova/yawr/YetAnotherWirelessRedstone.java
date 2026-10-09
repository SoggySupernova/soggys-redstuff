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


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);




	// Changing this to true will disable checks that prevent multiple transmitters being linked to one receiver.
	// The receiver will output the power from whichever transmitter was updated last, allowing for unique behavior.
	// However, this can cause desync, half-initialized blocks, and undefined behavior.
	public static final boolean ALLOW_MULTIPLE_TRANSMITTERS_TO_ONE_RECEIVER = false;


	private static final boolean ENABLE_LOGGING = true; // todo: make this do something

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
