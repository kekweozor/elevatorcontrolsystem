package sysc3303_elevator.networking;

public abstract class BlockingMultiplexer<I, S, R> implements BlockingSender<TaggedMsg<I, S>>, BlockingReceiver<TaggedMsg<I, R>> {
}
