package dev.demon.util.track;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTransaction;
import dev.demon.Anticheat;

public class TransactionTracker extends Tracker {

    @Override
    public void onPreServerTick(long now) {
        // handle the pre server tick

        // transaction ID to send
        short action = Anticheat.getInstance().getTaskHandler().nextPreTick();

        // construct the packet
        WrappedOutTransaction wrapped = new WrappedOutTransaction(0, action, false);

        // add the pre packet id to the pre map
        this.user.getProcessorManager().getLagProcessor().getPreTransactionMap()
                .put(action, now);

        // send the pre packet
        TinyProtocolHandler.sendPacket(this.getUser().getPlayer(), wrapped.getObject());

    }

    @Override
    public void onPostServerTick(long now) {
        // handle the post server tick

        // transaction ID to send
        short action = Anticheat.getInstance().getTaskHandler().nextPostTick();

        // construct the packet
        WrappedOutTransaction wrapped = new WrappedOutTransaction(0, action, false);

        // add the pre packet id to the post map
        this.user.getProcessorManager().getLagProcessor().getPostTransactionMap()
                .put(action, now);

        // send the post packet
        TinyProtocolHandler.sendPacket(this.getUser().getPlayer(), wrapped.getObject());
    }
}