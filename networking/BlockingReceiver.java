package sysc3303_elevator.networking;

public interface BlockingReceiver<T> {
    T take() throws InterruptedException;
}
