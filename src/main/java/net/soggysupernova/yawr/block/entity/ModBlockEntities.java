package net.soggysupernova.yawr.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.soggysupernova.yawr.YetAnotherWirelessRedstone;
import net.soggysupernova.yawr.block.ModBlocks;
import net.soggysupernova.yawr.block.entity.custom.WirelessEndpointBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<WirelessEndpointBlockEntity> WIRELESS_ENDPOINT_BLOCK_ENTITY =
            register("wireless_endpoint", WirelessEndpointBlockEntity::new, ModBlocks.WIRELESS_ENDPOINT);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = YetAnotherWirelessRedstone.id(name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void registerModBlockEntities() {

    }
}
