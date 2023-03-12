package dev.demon.base.process.processors;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTransaction;
import cc.funkemunky.api.utils.objects.evicting.ConcurrentEvictingMap;
import dev.demon.Anticheat;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.process.Processor;
import dev.demon.base.process.ProcessorInfo;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;
import dev.demon.util.runnable.Queue;
import dev.demon.util.time.EventTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.NumberConversions;

import java.util.*;

@Getter
@Setter
@ProcessorInfo(
        name = "Lag"
)
public class LagProcessor extends Processor {

    private double offsetY, offsetX, offsetZ;

    private final EventTimer serverTeleportTimer;

    private long lastTransaction;

    private long transactionPing, lastTransactionPing;

    private int tick;
    private int pingTicks, keepAlivePingTicks, postPingTick;
    private int packetDrop;
    private long postPing;

    private final Map<Short, Long> preTransactionMap = new ConcurrentEvictingMap<>(100);
    private final Map<Short, Long> postTransactionMap = new ConcurrentEvictingMap<>(100);

    private final Map<Short, List<dev.demon.util.runnable.Queue>> queueMap = new HashMap<>();


    //Yes I know its from the old open source ac lol
    public LagProcessor(User user) {
        super(user);

        this.serverTeleportTimer = new EventTimer(20, user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case SERVER_TRANSACTION: {

                break;
            }

            case CLIENT_TRANSACTION: {

                long now = event.getTimestamp();
                this.lastTransaction = now;

                final WrappedInTransactionPacket wrapper =
                        new WrappedInTransactionPacket(event.getPacketObject(), getUser().getPlayer());

                short action = wrapper.getAction();

                boolean isPre = this.preTransactionMap.containsKey(action);
                boolean isPost = this.postTransactionMap.containsKey(action);

                // log post ping
                if (isPost) {

                    this.postPing = (now - this.postTransactionMap.get(action));

                    // post ping tick
                    this.postPingTick = (int) ((this.postPing / 50.0) + 1);
                }

                // log pre ping
                if (isPre) {

                    // the ping...
                    this.lastTransactionPing = this.transactionPing;
                    this.transactionPing = (now - this.preTransactionMap.get(action));

                    // difference between 2 transactions
                    this.packetDrop = (int) Math.abs(this.transactionPing - this.lastTransactionPing);

                    // will act the same as Math.floor, uses less CPU
                    this.pingTicks = (int) ((this.transactionPing / 50.0) + 1);

                }

                // remove from both maps
                if (isPost) {
                    this.postTransactionMap.remove(action);
                }

                if (isPre) {
                    this.preTransactionMap.remove(action);
                }

                if (this.queueMap.containsKey(action)) {
                    List<Queue> queues = this.queueMap.remove(action);

                    // run every action queued for that tick
                    queues.forEach(Queue::run);
                }
            }
        }
    }

    public void queue(boolean onPost, Queue queue) {
        short action = onPost ? Anticheat.getInstance().getTaskHandler().nextPostTick()
                : Anticheat.getInstance().getTaskHandler().nextPreTick();

        if (onPost) {
            action -= 1;
        } else {
            action -= (69 + this.queueMap.size());
        }

        if (!this.queueMap.containsKey(action)) {
            this.queueMap.put(action, new ArrayList<>());
        }

        this.queueMap.get(action).add(queue);

        if (!onPost) {
            TinyProtocolHandler.sendPacket(this.getUser().getPlayer(),
                    new WrappedOutTransaction(0, action, false).getObject());
        }
    }


    public int getPingTicks(long ping) {
        // we could use (ping / 50.0) + 1, but this will return a more accurate result
        return NumberConversions.floor(ping / 50.0D) + 3;
    }
}