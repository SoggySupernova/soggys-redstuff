package net.soggysupernova.yawr.block.entity.custom;


import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
        return receivers;
    }

    public void setReceivers(Vector<BlockPosAndDimension> receivers) {
        this.receivers = receivers;
    }

    public void clearReceivers() {
        this.receivers = new Vector<>();
    }

    public void addReceiver(BlockPosAndDimension recv) {
        this.receivers.add(recv);
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
