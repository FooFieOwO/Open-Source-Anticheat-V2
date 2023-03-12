package dev.demon.base.process.processors;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import dev.demon.Anticheat;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.process.Processor;
import dev.demon.base.process.ProcessorInfo;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;
import dev.demon.util.time.EventTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.util.Vector;

@Getter
@Setter
@ProcessorInfo(
        name = "Block"
)
public class BlockProcessor extends Processor {

    private final EventTimer lastConfirmedBlockPlaceTimer;
    private Material materialPlaced;

    public BlockProcessor(User user) {
        super(user);
        this.lastConfirmedBlockPlaceTimer = new EventTimer(20, user);
    }

    //For confirming if a player has placed a block

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_BLOCK_PLACE: {

                WrappedInBlockPlacePacket wrapped = new WrappedInBlockPlacePacket(event.getPacketObject(),
                        getUser().getPlayer());

                if (getUser().getPlayer().getWorld() == null) {
                    return;
                }

                Material material = Anticheat.getInstance().getNmsManager().getInstance().getType(
                        getUser().getPlayer().getWorld(), wrapped.getBlockPosition().getX(),
                        wrapped.getBlockPosition().getY() + 1, wrapped.getBlockPosition().getZ());


                Material below = Anticheat.getInstance().getNmsManager().getInstance().getType(
                        getUser().getPlayer().getWorld(), wrapped.getBlockPosition().getX(),
                        wrapped.getBlockPosition().getY() - 2, wrapped.getBlockPosition().getZ());

                Material above = Anticheat.getInstance().getNmsManager().getInstance().getType(
                        getUser().getPlayer().getWorld(), wrapped.getBlockPosition().getX(),
                        wrapped.getBlockPosition().getY() + 2, wrapped.getBlockPosition().getZ());

                if (material.isBlock()
                        && wrapped.getItemStack() != null
                        && wrapped.getItemStack().getType().isBlock()) {

                    if (this.getUser().getPlayer().getItemInHand().getType() == Material.AIR) return;

                    boolean fix = below != Material.AIR && above != Material.AIR;

                    double blockX = wrapped.getBlockPosition().getX();
                    double blockZ = wrapped.getBlockPosition().getZ();

                    double currentX = getUser().getProcessorManager().getMovementProcessor().getTo().getPosX();
                    double currentZ = getUser().getProcessorManager().getMovementProcessor().getTo().getPosZ();

                    double offsetX = Math.abs(currentX - blockX);
                    double offsetZ = Math.abs(currentZ - blockZ);

                    double distanceXZ = Math.hypot(offsetX, offsetZ);

                    if (distanceXZ > 6 || !fix && material != Material.AIR) {
                        return;
                    }

                    this.lastConfirmedBlockPlaceTimer.reset();
                    this.materialPlaced = material;
                }

                break;
            }
        }
    }
}
