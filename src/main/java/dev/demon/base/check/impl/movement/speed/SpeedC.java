package dev.demon.base.check.impl.movement.speed;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.MathHelper;
import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import dev.demon.util.location.CustomLocation;

import java.util.Arrays;
import java.util.List;

@Data(name = "Speed",
        subName = "C",
        checkType = CheckType.MOVEMENT,
        description = "Basic xz limit check")

public class SpeedC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {
            //later lazy...
        }
    }
}
