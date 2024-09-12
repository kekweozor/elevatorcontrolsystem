package sysc3303_elevator.networking;

public interface BlockingSender<T> {
    void put(T e) throws InterruptedException;
}
