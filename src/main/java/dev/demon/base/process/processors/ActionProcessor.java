package dev.demon.base.process.processors;

import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutPositionPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.process.Processor;
import dev.demon.base.process.ProcessorInfo;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;
import dev.demon.util.time.EventTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@ProcessorInfo(
        name = "Action"
)
public class ActionProcessor extends Processor {

    private double offsetY, offsetX, offsetZ;

    private final EventTimer serverTeleportTimer, lastAttachTimer, lastVelocityTimer;

    private Vector velocityVector;

    private int lastConfirmedVelocityTick, lastVelocityTick;

    private double velocityH;

    private final HashMap<Long, Short> velocityQueue = new HashMap<>();

    public ActionProcessor(User user) {
        super(user);

        this.serverTeleportTimer = new EventTimer(20, user);
        this.lastAttachTimer = new EventTimer(20, user);
        this.lastVelocityTimer = new EventTimer(20, user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {

            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                this.lastConfirmedVelocityTick++;
                this.lastVelocityTick++;

                break;
            }

            case SERVER_POSITION: {
                WrappedOutPositionPacket positionAndLook
                        = new WrappedOutPositionPacket(event.getPacketObject(), getUser().getPlayer());

                double x = positionAndLook.getX();
                double y = positionAndLook.getY();
                double z = positionAndLook.getZ();

                this.offsetY = Math.abs(getUser().getProcessorManager().getMovementProcessor().getFrom().getPosY() - y);
                this.offsetX = Math.abs(getUser().getProcessorManager().getMovementProcessor().getFrom().getPosX() - x);
                this.offsetZ = Math.abs(getUser().getProcessorManager().getMovementProcessor().getFrom().getPosZ() - z);

                if (getOffsetX() > 0
                        || getOffsetY() > 0
                        || getOffsetZ() > 0) {
                    this.serverTeleportTimer.reset();
                }

                break;
            }

            case SERVER_ATTACH: {

                this.lastAttachTimer.reset();

                break;
            }

            case SERVER_VELOCITY: {
                WrappedOutVelocityPacket velocityPacket =
                        new WrappedOutVelocityPacket(event.getPacketObject(), getUser().getPlayer());

                if (velocityPacket.getId() == getUser().getPlayer().getEntityId()) {

                    double x = velocityPacket.getX();
                    double y = velocityPacket.getY();
                    double z = velocityPacket.getZ();

                    this.velocityH = Math.hypot(x, z) * 2;

                    this.velocityVector = new Vector(x, y, z);

                    this.lastVelocityTimer.reset();
                    this.lastVelocityTick = 0;

                    getUser().getProcessorManager().getLagProcessor().queue(false,
                            () -> this.lastConfirmedVelocityTick = 0);

                    getUser().getProcessorManager().getLagProcessor().queue(true,
                            () -> this.lastConfirmedVelocityTick = 0);

                }

                break;
            }
        }
    }
}
