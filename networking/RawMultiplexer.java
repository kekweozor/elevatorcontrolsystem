package sysc3303_elevator.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sysc3303_elevator.Pair;

public class RawMultiplexer<S, R> extends BlockingMultiplexer<Integer, S, R> implements Runnable {

    private HashMap<Integer, BlockingSender<S>> senders;
    private ManyBlockingReceiver<R> receiver;
    private Thread receiverThread;

    public RawMultiplexer(List<Pair<BlockingSender<S>, BlockingReceiver<R>>> channels) {
        List<Pair<Integer, BlockingReceiver<R>>> elevatorList = new ArrayList<>();
        this.senders = new HashMap<>();
        int channel_id = 0;
        for (var channel : channels) {
            elevatorList.add(new Pair<>(channel_id, channel.second()));
            this.senders.put(channel_id, channel.first());
            channel_id += 1;
        }
        this.receiver = new ManyBlockingReceiver<>(elevatorList);
        this.receiverThread = new Thread(this.receiver);
    }

    @Override
    public void put(TaggedMsg<Integer, S> msg) throws InterruptedException {
        this.senders.get(msg.id()).put(msg.content());
        ;
    }

    @Override
    public TaggedMsg<Integer, R> take() throws InterruptedException {
        return receiver.take();
    }

    @Override
    public void run() {
        this.receiverThread.start();
        try {
            this.receiverThread.join();
        } catch (InterruptedException e) {
            this.receiverThread.interrupt();
        }
    }
}
