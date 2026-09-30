package net.soggysupernova.yawr;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.soggysupernova.yawr.datagen.ModBlockLootTableProvider;
import net.soggysupernova.yawr.datagen.ModBlockTagsProvider;
import net.soggysupernova.yawr.datagen.ModModelProvider;

public class YetAnotherWirelessRedstoneDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
	}
}
