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
import net.soggysupernova.yawr.block.WirelessEndpointBlock;
import net.soggysupernova.yawr.util.BlockPosAndDimension;
import net.soggysupernova.yawr.block.entity.ModBlockEntities;
import org.jspecify.annotations.Nullable;

import java.util.Vector;

public class WirelessEndpointBlockEntity extends BlockEntity {
    private int clicks = 0;






    private Vector<BlockPosAndDimension> receivers = new Vector<>();

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

    public void clearReceivers() {
        for (int i = 0; i < this.getReceiverCount(); i++) {
            YetAnotherWirelessRedstone.LOGGER.info("Transmitter block cleared (shift click with torch or block break)! Looping through and resetting receivers...");
            var receiverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(receivers.get(i).getDimension())));
            var receiverBlockPos = new BlockPos(receivers.get(i).getX(), receivers.get(i).getY(), receivers.get(i).getZ());
            var receiverBlockState = receiverLevel.getBlockState(receiverBlockPos).setValue(WirelessEndpointBlock.IS_RECEIVER, false).setValue(WirelessEndpointBlock.POWER,0);
            receiverLevel.setBlock(receiverBlockPos, receiverBlockState, WirelessEndpointBlock.UPDATE_ALL);
            // todo: clear their stored transmitter locations
        }
        this.receivers = new Vector<>();
        level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
    }

    public void removeReceiver(BlockPosAndDimension recv) {
        var receiverLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(recv.getDimension())));
        var receiverBlockPos = new BlockPos(recv.getX(), recv.getY(), recv.getZ());
        var receiverBlockState = receiverLevel.getBlockState(receiverBlockPos).setValue(WirelessEndpointBlock.IS_RECEIVER, false).setValue(WirelessEndpointBlock.POWER,0);
        receiverLevel.setBlock(receiverBlockPos, receiverBlockState, WirelessEndpointBlock.UPDATE_ALL);
        // todo: clear its stored transmitter location

        this.receivers.remove(recv);
        level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
    }


    private boolean isReceiver = false;

    public boolean isReceiver() {
        return isReceiver;
    }

    public void setIsReceiver(boolean receiver) {
        isReceiver = receiver;
        level.setBlock(this.worldPosition, this.getBlockState().setValue(WirelessEndpointBlock.IS_RECEIVER, receiver), WirelessEndpointBlock.UPDATE_ALL);
        if (receiver == false) {
            // todo: clear this's transmitter location
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


    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel l) {
            l.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, false);
        }
        this.clearReceivers(); // reset receivers states
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
}
