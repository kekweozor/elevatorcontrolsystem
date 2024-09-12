package sysc3303_elevator;

public record ElevatorErrorEvent(int id,ElevatorError error, long waiTime) { }
