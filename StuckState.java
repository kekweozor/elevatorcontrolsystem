package sysc3303_elevator;

public class StuckState implements ElevatorState {
	public StuckState(Elevator elevator) {

		Logger.println("ALERT!!! Elevator Stuck");

		if (elevator.isdoorStuck()) {
			elevator.setStatus(ElevatorStatus.DoorStuck);
		} else {
			elevator.setStatus(ElevatorStatus.StuckBetweenFloors);
		}
	}

	@Override
	public void advance(Elevator elevator) throws InterruptedException {
		var queue = elevator.getDestinationFloors();
		
		var response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
		elevator.notifyObservers(response);
		Thread.sleep(5000); // Time to deal with fault

		if (elevator.isdoorStuck()) {
			elevator.setdoorStuck(false);
			if (elevator.getDoorState().equals(DoorState.CLOSED)) {

				elevator.setDoorState(DoorState.OPEN);
				elevator.setState(new DoorOpenState(elevator));

			} else {
				elevator.setDoorState(DoorState.CLOSED);
				elevator.setState(new DoorClosedState(elevator));
			}
			return;
		} else {
			elevator.setState(new ShutDownState(elevator));
			response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
			elevator.notifyObservers(response);
			return;
		}
	}
}
