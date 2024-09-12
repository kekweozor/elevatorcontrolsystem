package sysc3303_elevator;

public interface ElevatorState {
	  public void advance(Elevator elevator) throws InterruptedException;
}
