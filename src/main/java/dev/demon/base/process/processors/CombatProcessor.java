package dev.demon.base.process.processors;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.process.Processor;
import dev.demon.base.process.ProcessorInfo;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;
import dev.demon.util.time.EventTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

@Getter
@Setter
@ProcessorInfo(
        name = "Combat"
)
public class CombatProcessor extends Processor {

    private EventTimer lastAttackTimer;

    private Entity lastAttackedEntity;

    public CombatProcessor(User user) {
        super(user);
        this.lastAttackTimer = new EventTimer(20, user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_USE_ENTITY: {

                WrappedInUseEntityPacket useEntityPacket =
                        new WrappedInUseEntityPacket(event.getPacketObject(), getUser().getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    this.lastAttackTimer.reset();
                    this.lastAttackedEntity = useEntityPacket.getEntity();

                }

                break;
            }
        }
    }
}
