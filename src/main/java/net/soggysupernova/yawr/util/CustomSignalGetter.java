package net.soggysupernova.yawr.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.soggysupernova.yawr.YetAnotherWirelessRedstone;

import static net.minecraft.world.level.SignalGetter.DIRECTIONS;


// This is used when calculating the signal being received by a transmitter to exclude other wireless endpoint blocks to prevent the receiver block powering back the transmitter block infinitely
public class CustomSignalGetter {
    public static int getBestNeighborSignalExcludingBlockType(final Level level, final BlockPos pos, final Block excludedBlockType) {
        int best = 0;

        for (Direction direction : DIRECTIONS) {
            int signal = level.getSignal(pos.relative(direction), direction);
            // this took me so long to figure out why it was still working even when i modify this whole method to RETURN 0 and it was still returning 15
            if (signal >= 15 && !level.getBlockState(pos.relative(direction)).getBlock().equals(excludedBlockType)) {
                return 15;
            }




            if (signal > best && !level.getBlockState(pos.relative(direction)).getBlock().equals(excludedBlockType)) {
                best = signal;
            }


        }

        return best;
    }
}
