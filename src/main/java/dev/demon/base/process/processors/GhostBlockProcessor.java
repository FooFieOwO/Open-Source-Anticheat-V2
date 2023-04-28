package dev.demon.base.process.processors;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutSpawnEntityLivingPacket;
import cc.funkemunky.api.utils.RunUtils;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.process.Processor;
import dev.demon.base.process.ProcessorInfo;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;
import dev.demon.util.location.CustomLocation;
import dev.demon.util.math.MathUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Locale;

@Getter
@Setter
@ProcessorInfo(
        name = "GhostBlock"
)
public class GhostBlockProcessor extends Processor {

    private int ghostBlockTicks;
    private Location location = null;
    private Location spawnLocation = null;

    private int invalidChunkTicks;

    private boolean flag = false;
    private int lastTeleport;

    public GhostBlockProcessor(User data) {
        super(data);
    }


    //TODO: make my eyes not hurt from this!

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {

                if (getUser().getPlayer().getWorld() == null
                        || getUser().generalCancel()) return;

                CustomLocation location = getUser().getProcessorManager().getMovementProcessor().getTo();

                boolean ground = getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();

                boolean serverGround = getUser().getProcessorManager().getCollisionProcessor().isServerGround();
                boolean lastServerGround = getUser().getProcessorManager().getCollisionProcessor().isLastServerGround();

                if (getUser().getProcessorManager().getMovementProcessor().getTick() <= 100) {
                    this.spawnLocation = new Location(location.getWorld(), location.getPosX(),
                            location.getPosY(), location.getPosZ(), location.getYaw(), location.getPitch());
                }

                this.fixChunkMotion(getUser());

                if (ground && !serverGround && !lastServerGround) {

                    if (getUser().getProcessorManager()
                            .getBlockProcessor().getLastConfirmedBlockPlaceTimer().hasNotPassed(1)) {
                        this.ghostBlockTicks = 0;
                    }

                    if (++this.ghostBlockTicks > 1) {

                        if (this.location != null) {
                            RunUtils.task(() -> getUser().getPlayer().teleport(this.location));
                            this.fail("%PLAYER% failed ghost block processor (Lag Back A)");

                        } else {
                            RunUtils.task(() -> getUser().getPlayer().teleport(this.spawnLocation));
                            this.fail("%PLAYER% failed ghost block processor (Lag Back B)");
                        }

                        this.ghostBlockTicks = 0;
                    }
                }

                if (ground && serverGround) {
                    this.location = new Location(location.getWorld(), location.getPosX(),
                            location.getPosY(), location.getPosZ(), location.getYaw(), location.getPitch());
                }

                break;
            }
        }
    }

    private void fixChunkMotion(User user) {
        double deltaY = user.getProcessorManager().getMovementProcessor().getDeltaY();

        final boolean invalid = deltaY + 0.09800000190734881 <= 0.001
                && deltaY + 0.09800000190734881 >= -0.00001 && Math.abs(deltaY) > 0.07
                && Math.abs(deltaY) < 0.09801f;

        if (invalid) {

            this.invalidChunkTicks++;

            if (this.invalidChunkTicks > 4) {

                Location groundLocation = this.location != null ? this.location : user.getPlayer().getLocation();

                RunUtils.task(() ->
                        user.getPlayer().teleport(groundLocation.clone().add(0, 1.0D, 0)));
            }

            if (this.invalidChunkTicks > 14) {
                user.kickPlayer(
                        "attempting to abuse unloaded chunks/being in an unloaded chunk too long");
            }
        }
    }
}
