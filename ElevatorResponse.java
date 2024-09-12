package sysc3303_elevator;

import java.io.Serializable;

public record ElevatorResponse(int currentFloor, ElevatorStatus state, Direction direction) implements Serializable {}