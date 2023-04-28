package dev.demon.util.nms.instances;

import dev.demon.base.user.User;
import dev.demon.util.box.BoundingBox;
import dev.demon.util.nms.Instance;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.MobEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Instance1_7_R4 extends Instance {

    @Override
    public Material getType(World world, double x, double y, double z) {
        return CraftMagicNumbers.getMaterial(((CraftWorld) world).getHandle().getType((int) x, (int) y, (int) z));
    }

    @Override
    public List<PotionEffect> potionEffectList(User user) {
        List<PotionEffect> effects = new ArrayList();

        for (Object obj : ((CraftPlayer) user.getPlayer()).getHandle().effects.values()) {
            if (obj instanceof MobEffect) {
                MobEffect handle = (MobEffect) obj;
                effects.add(new PotionEffect(PotionEffectType.getById(handle.getEffectId()), handle.getDuration(),
                        handle.getAmplifier(), handle.isAmbient()));
            }
        }

        return effects;
    }

    @Override
    public float getSlipperiness(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return MinecraftServer.getServer().getWorld().getType(x, y, z).frictionFactor;
    }


    @Override
    public void sendBlockUpdate(User user, double x, double y, double z) {
        PacketPlayOutBlockChange packetPlayOutBlockChange =
                new PacketPlayOutBlockChange((int) x, (int) y, (int) z,
                        (((CraftWorld) user.getPlayer().getWorld()).getHandle()));
        ((CraftPlayer) user.getPlayer()).getHandle().playerConnection.sendPacket(packetPlayOutBlockChange);
    }

    @Override
    public BoundingBox getEntityBoundingBox(Entity entity) {
        AxisAlignedBB aabb = ((CraftEntity) entity).getHandle().boundingBox;

        return new BoundingBox(
                (float) aabb.a, (float) aabb.b, (float) aabb.c, (float) aabb.d, (float) aabb.e, (float) aabb.f
        );
    }

    @Override
    public boolean getEntityBoundingBoxGround(Entity entity) {

        CraftEntity entityPlayer = ((CraftEntity) entity);

        AxisAlignedBB aabb = ((CraftEntity) entity).getHandle().boundingBox;

        AxisAlignedBB axisAlignedBB = aabb
                .grow(0.0625, 0.0625, 0.0625)
                .a(0.0, -0.55, 0.0);

        return entityPlayer.getHandle().world.c(axisAlignedBB);
    }

    @Override
    public void createPostHook(Runnable runnable) {
        MinecraftServer.getServer().a(runnable::run);
    }
}
