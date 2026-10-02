package net.soggysupernova.yawr.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.UseOnContext;
import net.soggysupernova.yawr.block.WirelessEndpointBlock;
import net.soggysupernova.yawr.block.entity.custom.WirelessEndpointBlockEntity;

public class WirelessLinkerItem extends Item {

    public WirelessLinkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof WirelessEndpointBlockEntity && context.getPlayer().isCrouching()) {
            var itemStack = context.getItemInHand();
            var newstack = itemStack.split(1);
            newstack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
            newstack.set(DataComponents.LORE, ItemLore.EMPTY);
            CompoundTag compound = new CompoundTag();
            newstack.remove(DataComponents.CUSTOM_DATA);
            context.getPlayer().getInventory().placeItemBackInInventory(newstack, false, null);
            // possible TOCTOU here? (todo)
            WirelessEndpointBlockEntity a = (WirelessEndpointBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            a.setIsReceiver(false);
            a.clearReceivers();
            if (context.getLevel().isClientSide()) {
                context.getPlayer().sendOverlayMessage(Component.literal("Cleared all connections"));
            }
            context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.LANTERN_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
            return InteractionResult.SUCCESS;




        }
        return super.useOn(context);
    }
}
