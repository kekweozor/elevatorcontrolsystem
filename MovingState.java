package sysc3303_elevator;

public class MovingState implements ElevatorState {
	public MovingState(Elevator elevator) {
		try {
			Thread.sleep(2000); // starting up the motor
		} catch (InterruptedException e) {
			return;
		} 
		elevator.setStatus(ElevatorStatus.Moving);
	}

	@Override
	public void advance(Elevator elevator) throws InterruptedException {
		var queue = elevator.getDestinationFloors();
		int destinationFloor = queue.peek().get();

		// Start moving
		elevator.setMoving(true);
		Logger.debugln("Elevator doors are " + elevator.getDoorState() + ", motor is ON. Car button " + destinationFloor
				+ " lamp is " + elevator.getButtonLampStates()[destinationFloor] + " Elevator is moving "
				+ elevator.getDirection());
		while (queue.getCurrentFloor() != destinationFloor) {
			var response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
			elevator.notifyObservers(response);

			elevator.startTimer(ElevatorStatus.Moving);

			Thread.sleep(elevator.getTIME_BETWEEN_FLOORS());
			queue.advance();
			Logger.println("Floor:  " + queue.getCurrentFloor());

			if (elevator.checkAndDealWithFaults(ElevatorStatus.Moving)) {
				return;
			}
			;

		}
		var response = new ElevatorResponse(queue.getCurrentFloor(), elevator.getStatus(), elevator.getDirection());
		elevator.notifyObservers(response);
		elevator.getButtonLampStates()[queue.peek().get()] = ButtonLampState.OFF;
		Logger.debugln("Elevator reached destination floor: " + destinationFloor + ". Car button lamp is "
				+ elevator.getButtonLampStates()[destinationFloor]
				+ ". Motor is OFF. Elevator is not moving... Opening doors");
		queue.next();
		elevator.setMotorOn(false);
		elevator.setMoving(false);

		// opening doors
		Logger.debugln("Openning doors");
		elevator.startTimer(ElevatorStatus.DoorClose);
		Thread.sleep(elevator.getDOOR_OPENING_CLOSING_TIME());

		if (elevator.checkAndDealWithFaults(ElevatorStatus.Moving)) {
			return;
		}

		elevator.setDoorState(DoorState.OPEN);

		elevator.setState(new DoorOpenState(elevator));
	}
}