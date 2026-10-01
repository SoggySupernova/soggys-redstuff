package net.soggysupernova.yawr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.soggysupernova.yawr.YetAnotherWirelessRedstone;
import net.soggysupernova.yawr.block.entity.ModBlockEntities;
import net.soggysupernova.yawr.block.entity.custom.WirelessEndpointBlockEntity;
import net.soggysupernova.yawr.item.ModItems;
import net.soggysupernova.yawr.util.BlockPosAndDimension;
import org.jspecify.annotations.Nullable;

public class WirelessEndpointBlock extends BaseEntityBlock {
    protected WirelessEndpointBlock(Properties properties) {

        super(properties);

        registerDefaultState(defaultBlockState().setValue(POWER, 0));
    }



    public static final IntegerProperty POWER = BlockStateProperties.POWER;


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return BaseEntityBlock.<WirelessEndpointBlockEntity, T>createTickerHelper(type, ModBlockEntities.WIRELESS_ENDPOINT_BLOCK_ENTITY, WirelessEndpointBlockEntity::tick);
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new WirelessEndpointBlockEntity(worldPosition, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof WirelessEndpointBlockEntity counterBlockEntity)) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }


        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                counterBlockEntity.clearReceivers();
                level.playSound(player, pos, SoundEvents.LANTERN_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
                return InteractionResult.SUCCESS; // swing arm
            }
        }


        if (!itemStack.is(ModItems.WIRELESS_LINKER)) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }



        level.playSound(player, pos, SoundEvents.LANTERN_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);

        counterBlockEntity.incrementClicks();

        if (level.isClientSide()) {
            player.sendOverlayMessage(Component.literal("Set transmitter coordinates to " + pos.toShortString() + ""));

        }


        CustomData a = itemStack.get(DataComponents.CUSTOM_DATA);

        if (a instanceof CustomData && a.copyTag().contains("dim")) {
            var b = a.copyTag();
            boolean isSameBlock = b.get("x").toString().equals(String.valueOf(pos.getX())) && b.get("y").toString().equals(String.valueOf(pos.getY())) && b.get("z").toString().equals(String.valueOf(pos.getZ())) && b.get("dim").toString().equals("\""+level.dimension().identifier().toString()+"\"");
            YetAnotherWirelessRedstone.LOGGER.info(String.valueOf(b.get("dim").toString()));
            YetAnotherWirelessRedstone.LOGGER.info((level.dimension().identifier().toString()));
            if (isSameBlock && level.isClientSide()) {
                player.sendOverlayMessage(Component.literal("Can't link an endpoint to itself!"));
            }

            if (!isSameBlock) {
                counterBlockEntity.setIsReceiver(true);
                if (level.isClientSide()) {
                    player.sendOverlayMessage(Component.literal("Successfully linked endpoints!"));
                }
            }
        }



        var newstack = itemStack.split(1);
        newstack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        CompoundTag compound = new CompoundTag();
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
        compound.putString("dim", level.dimension().identifier().toString());
        newstack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        player.getInventory().placeItemBackInInventory(newstack, false, null);




        //counterBlockEntity.addReceiver(new BlockPosAndDimension(pos.getX(), pos.getY(), pos.getZ(), level.dimension().identifier().toString()));

        return InteractionResult.SUCCESS;
    }


}
