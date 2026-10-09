package net.soggysupernova.yawr.block.entity.custom;


import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.soggysupernova.yawr.YetAnotherWirelessRedstone;
import net.soggysupernova.yawr.block.ModBlocks;
import net.soggysupernova.yawr.block.WirelessEndpointBlock;
import net.soggysupernova.yawr.util.BlockPosAndDimension;
import net.soggysupernova.yawr.block.entity.ModBlockEntities;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Vector;

public class WirelessEndpointBlockEntity extends BlockEntity {
    private int clicks = 0;






    private Vector<BlockPosAndDimension> receivers = new Vector<>();

    private BlockPosAndDimension transmitter = null;

    public int getClicks() {
        return this.clicks;
    }

    public Vector<BlockPosAndDimension> getReceivers() {
        return this.receivers;
    } // what the difference between this.receivers and receivers

    public void setReceivers(Vector<BlockPosAndDimension> receivers) {
        this.receivers = receivers;
        level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
    }

    public void clearReceivers(boolean doZeBlockUpdate) {
        if (!level.isClientSide()) {
            for (int i = 0; i < this.getReceiverCount(); i++) {
                YetAnotherWirelessRedstone.LOGGER.info("Transmitter block cleared (shift click with torch or block break)! Looping through and resetting receivers...");
                var receiverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(receivers.get(i).getDimension())));
                var receiverBlockPos = new BlockPos(receivers.get(i).getX(), receivers.get(i).getY(), receivers.get(i).getZ());
                // Make sure it isn't already deleted (Also somehow fixes transmitters not being removed)
                if (receiverLevel.getBlockState(receiverBlockPos).getBlock() == ModBlocks.WIRELESS_ENDPOINT) {
                    var receiverBlockState = receiverLevel.getBlockState(receiverBlockPos).setValue(WirelessEndpointBlock.IS_RECEIVER, false).setValue(WirelessEndpointBlock.POWER, 0);
                    receiverLevel.setBlock(receiverBlockPos, receiverBlockState, WirelessEndpointBlock.UPDATE_ALL);
                    var receiverBlockEntity = receiverLevel.getBlockEntity(receiverBlockPos);
                    // Clear stored transmitter location
                    if (receiverBlockEntity instanceof WirelessEndpointBlockEntity e) {
                        e.setTransmitter(null);
                    }
                }

            }
            this.receivers = new Vector<>();
            if (doZeBlockUpdate) {
                level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
            }
        } // Serverside check fixes a weird thing where creating a link from a receiver back to its transmitter would cause a crash when trying to reset the original transmitter
        // how to recreate it:
        // comment out if statement, place three endpoints, get an uninitialized linker
        // right click first one, right click second one, shift-right-click third one
        // right click second one, right click first one, shift-right-click first one
    }

    public void removeReceiver(BlockPosAndDimension recv) {

            var receiverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(recv.getDimension())));
            var receiverBlockPos = new BlockPos(recv.getX(), recv.getY(), recv.getZ());
            // Makes sure the receiver wasn't just mined (this can called by resetThisBlock which can be called from block deletion)
            if (receiverLevel.getBlockEntity(receiverBlockPos) instanceof WirelessEndpointBlockEntity && receiverLevel.getBlockState(receiverBlockPos).getBlock() == ModBlocks.WIRELESS_ENDPOINT && !level.isClientSide()) {
                var receiverBlockState = receiverLevel.getBlockState(receiverBlockPos).setValue(WirelessEndpointBlock.IS_RECEIVER, false).setValue(WirelessEndpointBlock.POWER, 0);
                receiverLevel.setBlock(receiverBlockPos, receiverBlockState, WirelessEndpointBlock.UPDATE_ALL);

                // clear its stored transmitter location
                // Right now this is only called when the receiver is reset, which means its transmitter location will already be cleared, but perhaps this will not be true in the future
                var receiverBlockEntity = receiverLevel.getBlockEntity(receiverBlockPos);

                if (receiverBlockEntity instanceof WirelessEndpointBlockEntity e) {
                    e.setTransmitter(null);
                }

            }

            YetAnotherWirelessRedstone.LOGGER.info("Removing receiver "+recv.toString());
            this.receivers.remove(recv);
            YetAnotherWirelessRedstone.LOGGER.info("Vector with receiver removed: "+this.receivers.toString());
            level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());

    }


    private boolean isReceiver = false;

    public boolean isReceiver() {
        return isReceiver;
    }

    public void setIsReceiver(boolean receiver, boolean doZeBlockUpdate) {
        isReceiver = receiver;
        if (doZeBlockUpdate) {level.setBlock(this.worldPosition, this.getBlockState().setValue(WirelessEndpointBlock.IS_RECEIVER, receiver), WirelessEndpointBlock.UPDATE_ALL);}
        if (!receiver) {

            if (this.transmitter != null && !level.isClientSide()) {
                YetAnotherWirelessRedstone.LOGGER.info("Setting isReceiver to false with transmitter data"+this.transmitter.serialize());
                var transmitterLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(this.transmitter.getDimension())));
                var transmitterBlockPos = new BlockPos(this.transmitter.getX(), this.transmitter.getY(), this.transmitter.getZ());
                var transmitterBlockEntity = transmitterLevel.getBlockEntity(transmitterBlockPos);
                if (transmitterBlockEntity instanceof WirelessEndpointBlockEntity e) {
                    YetAnotherWirelessRedstone.LOGGER.info("Found transmitter block entity"+e+transmitterBlockPos);
                    e.removeReceiver(new BlockPosAndDimension(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), level.dimension().identifier().toString()));
                }


                transmitterLevel.updateNeighborsAt(transmitterBlockPos, this.getBlockState().getBlock());
            }


           this.setTransmitter(null);

        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WirelessEndpointBlockEntity blockEntity) {
        if (level instanceof ServerLevel l) {
            l.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, true);
        }

    }


    public int getReceiverCount() {
        return receivers.size();
    }


    public void resetThisBlock(boolean doZeBlockUpdate) {
        YetAnotherWirelessRedstone.LOGGER.info("Resetting block (mined or shiftclicked!)");
        this.clearReceivers(doZeBlockUpdate); // reset receivers states
        this.setIsReceiver(false, doZeBlockUpdate); // Remove my location from my transmitter's list
    }


    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel l) {
            l.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, false);
        }
        resetThisBlock(false); // Reset stuff WITHOUT updating myself
        super.preRemoveSideEffects(pos, state);
    }

    public void addReceiver(BlockPosAndDimension recv) {
        this.receivers.add(recv);
        level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
    }

    public void incrementClicks() {
        this.clicks++;
        this.setChanged();
    }
    public WirelessEndpointBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRELESS_ENDPOINT_BLOCK_ENTITY, pos, state);
    }





    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("clicks", this.clicks);
        for (int i = 0; i < getReceivers().size(); i++) {
            output.putString("receiver"+i, getReceivers().get(i).serialize());
        }
        output.putInt("receiverCount",getReceivers().size());
        output.putBoolean("isReceiver",isReceiver());
        if (getTransmitter() != null) {
            output.putString("transmitter", getTransmitter().serialize());
        } else {
            output.putString("transmitter", "");
        }

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.clicks = input.getIntOr("clicks", 0);
        var receiverCount = input.getIntOr("receiverCount", 0);
        Vector<BlockPosAndDimension> receivers = new Vector<>();
        for (int i = 0; i < receiverCount; i++) {
            var string = input.getStringOr("receiver"+i,"");
            receivers.add(BlockPosAndDimension.deserialize(string));
        }
        this.receivers = receivers;
        this.isReceiver = input.getBooleanOr("isReceiver",false);
        var thething = input.getStringOr("transmitter", "");
        if (thething.equals("")) {
            this.transmitter = null;
        } else {
            this.transmitter = BlockPosAndDimension.deserialize(thething);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (level == null) return;

        BlockState state = getBlockState();

        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }


    public BlockPosAndDimension getTransmitter() {
        return transmitter;
    }

    public void setTransmitter(BlockPosAndDimension transmitter) {
        this.transmitter = transmitter;
    }
}
