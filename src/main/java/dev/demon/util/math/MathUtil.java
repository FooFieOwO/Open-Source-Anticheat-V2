package dev.demon.util.math;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.LinkedList;
import java.util.List;

public class MathUtil {
    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public static float wrapAngleTo180_float(float value) {
        value %= 360F;

        if (value >= 180.0F)
            value -= 360.0F;

        if (value < -180.0F)
            value += 360.0F;

        return value;
    }

    public static boolean isScientificNotation(Number num) {
        return num.doubleValue() < .001D;
    }

    public static boolean hasNotation(double value) {
        return !(value > 0.0001);
    }

    public static double getCollisionModulo(double value) {
        return Math.abs(value % 0.015625D);
    }

    public static List<Entity> getEntitiesWithinRadius(Location location, double radius) {
        double x = location.getX();
        double z = location.getZ();

        World world = location.getWorld();
        List<Entity> entities = new LinkedList<>();

        for (int locX = (int) Math.floor((x - radius) / 16.0D);
             locX <= (int) Math.floor((x + radius) / 16.0D); locX++) {
            for (int locZ = (int) Math.floor((z - radius) / 16.0D);
                 locZ <= (int) Math.floor((z + radius) / 16.0D); locZ++) {
                if (!world.isChunkLoaded(locX, locZ)) continue;

                for (Entity entity : world.getChunkAt(locX, locZ).getEntities()) {
                    if (entity == null || entity.getLocation()
                            .distanceSquared(location) > radius * radius) continue;
                    entities.add(entity);
                }
            }
        }

        return entities;
    }
}
