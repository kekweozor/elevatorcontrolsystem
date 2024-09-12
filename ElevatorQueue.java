package sysc3303_elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ElevatorQueue {
    private ArrayList<Integer> queue;
    private HashMap<Integer, ArrayList<Integer>> queueDependent;
    private int currentFloor;

    public ElevatorQueue(int currentFloor) {
        this.queue = new ArrayList<>();
        this.queueDependent = new HashMap<>();
        this.currentFloor = currentFloor;
    }

    public synchronized Direction getDirection() {
        return currentFloor < this.peek().orElse(currentFloor) ? Direction.Up : Direction.Down;
    }

    public synchronized void add(int src, int dest) {
        var dependencies =  this.queueDependent.getOrDefault(src, new ArrayList<>());
        dependencies.add(dest);
        this.queueDependent.putIfAbsent(src, dependencies);
        this.add(src);
    }

    public synchronized void add(int src) {
        this.queue.add(src);

        var destinations = new HashSet<>(this.queue);
        for (var otherSources : this.queueDependent.keySet()) {
            destinations.add(otherSources);
        }

        this.queue = orderList(destinations);
    }

    private ArrayList<Integer> orderList(HashSet<Integer> destinations) {
        var direction = this.getDirection();

        var destArray = new ArrayList<>(destinations);
        Collections.sort(destArray);
        if (direction.equals(Direction.Down)) {
            Collections.reverse(destArray);
        }

        // Bump some values to the back if not continguous in the same direction
        var bumped = new ArrayList<Integer>();
        while (destArray.size() > 0) {
            var headValue = destArray.get(0);
            if (direction.equals(Direction.Up) ? headValue < this.currentFloor : headValue > this.currentFloor) {
                bumped.add(destArray.remove(0));
            } else {
                break;
            }
        }

        Collections.reverse(bumped);
        destArray.addAll(bumped);


        return destArray;
    }

    public synchronized Optional<Integer> peek() {
        if (this.queue.size() > 0) {
            return Optional.of(this.queue.get(0));
        }
        return Optional.empty();
    }

    public synchronized void next() {
        if (this.queue.size() > 0) {
            var nextFloor = this.queue.remove(0);
            var newSourceFloors = this.queueDependent.remove(nextFloor);
            if (newSourceFloors != null) {
                for (var newSourceFloor : newSourceFloors) {
                    this.add(newSourceFloor);
                }
            }
            this.currentFloor = nextFloor;
        }
    }

    public synchronized int getCurrentFloor() {
        return this.currentFloor;
    }

    public synchronized List<Integer> getQueue() {
        return Collections.unmodifiableList(this.queue);
    }

    public synchronized void advance() {
        if (peek().isEmpty()) {
            return;
        }

        if (getDirection().equals(Direction.Up)) {
            this.currentFloor = Math.min(peek().get(), currentFloor + 1);
        } else {
            this.currentFloor = Math.max(peek().get(), currentFloor - 1);
        }
    }
}
