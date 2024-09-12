package sysc3303_elevator;

public class DoorClosedState implements ElevatorState {
	public DoorClosedState(Elevator elevator) {
		elevator.setStatus(ElevatorStatus.DoorClose);
	}

	@Override
	public void advance(Elevator elevator) throws InterruptedException {
		
		var queue = elevator.getDestinationFloors();
		var response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
		elevator.notifyObservers(response);
		
		
		if (queue.peek().isEmpty()) {
			elevator.setState(new IdleState(elevator));

			response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
			elevator.notifyObservers(response);
			return;
		}
		if (queue.getCurrentFloor() != queue.peek().get()) {
			elevator.setState(new MovingState(elevator));
			response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
			elevator.notifyObservers(response);
		} else {
			Logger.debugln("Opening doors");
			elevator.getButtonLampStates()[queue.peek().get()] = ButtonLampState.OFF;
			elevator.startTimer(ElevatorStatus.DoorClose);

			Thread.sleep(elevator.getDOOR_OPENING_CLOSING_TIME());

			if (elevator.checkAndDealWithFaults(ElevatorStatus.DoorClose)) {
				return;
			}
			elevator.setDoorState(DoorState.OPEN);
			queue.next();
			elevator.setState(new DoorOpenState(elevator));
		}
	}
}