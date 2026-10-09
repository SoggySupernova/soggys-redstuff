package net.soggysupernova.yawr.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.soggysupernova.yawr.block.WirelessEndpointBlock;
import net.soggysupernova.yawr.block.entity.custom.WirelessEndpointBlockEntity;

public class WirelessLinkerItem extends Item {

    public WirelessLinkerItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            var itemStack = player.getItemInHand(hand);
            var newstack = itemStack.split(1);
            newstack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
            newstack.set(DataComponents.LORE, ItemLore.EMPTY);
            CompoundTag compound = new CompoundTag();
            newstack.remove(DataComponents.CUSTOM_DATA);
            player.getInventory().placeItemBackInInventory(newstack, false, null);
            if (level.isClientSide()) {
                player.sendOverlayMessage(Component.literal("Reset held linker"));
            }
            return InteractionResult.FAIL;
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer().isCrouching()) {
            // Always reset the current item
            var itemStack = context.getItemInHand();
            var newstack = itemStack.split(1);
            newstack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
            newstack.set(DataComponents.LORE, ItemLore.EMPTY);
            CompoundTag compound = new CompoundTag();
            newstack.remove(DataComponents.CUSTOM_DATA);
            context.getPlayer().getInventory().placeItemBackInInventory(newstack, false, null);
            if (context.getLevel().isClientSide()) {
                context.getPlayer().sendOverlayMessage(Component.literal("Reset held linker"));
            }

            // If clicked a block, reset it
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof WirelessEndpointBlockEntity) {

                WirelessEndpointBlockEntity a = (WirelessEndpointBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
                a.setIsReceiver(false, true);
                a.clearReceivers(true);
                if (context.getLevel().isClientSide()) {
                    context.getPlayer().sendOverlayMessage(Component.literal("Cleared block connections and reset held linker"));
                }
                context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.LANTERN_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
                return InteractionResult.SUCCESS;


            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}
