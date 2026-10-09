package net.soggysupernova.yawr.util;

import io.netty.handler.codec.base64.Base64Encoder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.soggysupernova.yawr.YetAnotherWirelessRedstone;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;

public class BlockPosAndDimension {

    private int x;

    private int y;

    private int z;

    private String dimension; // should really be a resoucrekey or something

    public BlockPosAndDimension(int x, int y, int z, String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dim;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getDimension() {
        return dimension;
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public BlockPosAndDimension fromBlockPosAndDimension(BlockPos pos, ResourceKey<Level> level) {
        return new BlockPosAndDimension(pos.getX(), pos.getY(), pos.getZ(), level.identifier().toString()); // todo: test this
    }


   public String serialize() {
        var encoder = Base64.getEncoder();
        YetAnotherWirelessRedstone.LOGGER.info("Serialiezed to "+encoder.encodeToString((this.x + " " + this.y + " " + this.z + " " + this.dimension).getBytes()));
        return encoder.encodeToString((this.x + " " + this.y + " " + this.z + " " + this.dimension).getBytes()); // only the finest humanslop here
   }

    public static BlockPosAndDimension deserialize(String string) {
        YetAnotherWirelessRedstone.LOGGER.info("Deserizling string "+string);
        var decoder = Base64.getDecoder();
        var decodedString = new String(decoder.decode(string), StandardCharsets.UTF_8);
        YetAnotherWirelessRedstone.LOGGER.info("Deserialized to "+decodedString);
        var decodedString2 = decodedString.split(" ");
        int x = Integer.parseInt(decodedString2[0]);
        int y = Integer.parseInt(decodedString2[1]);
        int z = Integer.parseInt(decodedString2[2]);
        var dim = decodedString2[3];
        return new BlockPosAndDimension(x,y,z,dim);
    }


    @Override
    public String toString() {
        return "BlockPosAndDimension{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", dimension='" + dimension + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosAndDimension that = (BlockPosAndDimension) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, dimension);
    }
}

