package net.soggysupernova.yawr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
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
        registerDefaultState(defaultBlockState().setValue(IS_RECEIVER, false));
    }



    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty IS_RECEIVER = BooleanProperty.create("is_receiver");


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return BaseEntityBlock.createTickerHelper(type, ModBlockEntities.WIRELESS_ENDPOINT_BLOCK_ENTITY, WirelessEndpointBlockEntity::tick);
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new WirelessEndpointBlockEntity(worldPosition, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        builder.add(IS_RECEIVER);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isCrouching()) {
            YetAnotherWirelessRedstone.LOGGER.info("hi");
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }



    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof WirelessEndpointBlockEntity counterBlockEntity)) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }



            if (player.isCrouching()) {
                // If holding wireless linker, reset it
                // Actually this doesn't work because sneaking bypass block use
                // Real logic now in WirelessLinkerItem

                    counterBlockEntity.clearReceivers();
                    counterBlockEntity.setIsReceiver(false);
                    if (level.isClientSide()) {
                        player.sendOverlayMessage(Component.literal("Cleared all connections"));
                    }
                    level.playSound(player, pos, SoundEvents.LANTERN_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
                    return InteractionResult.SUCCESS; // swing arm





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

                if (!level.isClientSide()) {
                var transmitterBlock = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(b.get("dim").toString().replaceAll("^\"|\"$", "")))).getBlockEntity(new BlockPos(Integer.parseInt(String.valueOf(b.get("x"))),Integer.parseInt(String.valueOf(b.get("y"))),Integer.parseInt(String.valueOf(b.get("z")))));
                if (transmitterBlock instanceof WirelessEndpointBlockEntity theblockentity) {
                    theblockentity.addReceiver(new BlockPosAndDimension(pos.getX(), pos.getY(), pos.getZ(), level.dimension().identifier().toString()));
                }
                }


                if (level.isClientSide()) {
                    player.sendOverlayMessage(Component.literal("Successfully linked endpoints!"));
                }
            }
        }


        if (!counterBlockEntity.isReceiver()) {
            var newstack = itemStack.split(1);
            newstack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            newstack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("("+pos.getX()+", "+pos.getY()+", "+pos.getZ()+"), "+level.dimension().identifier().toString())));
            CompoundTag compound = new CompoundTag();
            compound.putInt("x", pos.getX());
            compound.putInt("y", pos.getY());
            compound.putInt("z", pos.getZ());
            compound.putString("dim", level.dimension().identifier().toString());
            newstack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
            player.getInventory().placeItemBackInInventory(newstack, false, null);
        }



        //counterBlockEntity.addReceiver(new BlockPosAndDimension(pos.getX(), pos.getY(), pos.getZ(), level.dimension().identifier().toString()));

        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean shouldRedstoneWireConnectTo(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        /*
        YetAnotherWirelessRedstone.LOGGER.info(String.valueOf(pos.relative(direction.getOpposite())));
        var ioi = level.getBlockEntity(pos.relative(direction.getOpposite())); // get block position based on redstone dust position
        if (ioi instanceof WirelessEndpointBlockEntity oio) {
            return oio.isReceiver();
        }
        return false;

         */

        return state.getValue(IS_RECEIVER);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(IS_RECEIVER);
    }


    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof WirelessEndpointBlockEntity as) {
            return as.getReceiverCount(); // Overloadable(?) on purpose
        }
        return 0;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (state.getValue(IS_RECEIVER)) {
            return state.getValue(POWER);
        }
        return super.getSignal(state, level, pos, direction);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!state.getValue(IS_RECEIVER)) {
            var sig = 0;
            if (level.hasNeighborSignal(pos)) {
                sig = level.getBestNeighborSignal(pos);
            }
            if (level.getBlockEntity(pos) instanceof WirelessEndpointBlockEntity e && !level.isClientSide()) {
                var receivers = e.getReceivers();
                for (int i = 0; i < receivers.size(); i++) {
                    var receiverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(receivers.get(i).getDimension())));
                    var receiverBlockPos = new BlockPos(receivers.get(i).getX(), receivers.get(i).getY(), receivers.get(i).getZ());
                    var receiverBlockState = receiverLevel.getBlockState(receiverBlockPos).setValue(POWER, sig);
                    receiverLevel.setBlock(receiverBlockPos, receiverBlockState, WirelessEndpointBlock.UPDATE_ALL);
                }
            }
        }

        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }
}
