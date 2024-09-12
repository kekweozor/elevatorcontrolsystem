package sysc3303_elevator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Elevator Class
 *
 * Class responsible for receiving messages from the Elevator elevatorSubsystem,
 * process it and then notify the elevatorSubsystem.
 *
 * @author Tao Lufula, 101164153
 */
public class Elevator implements Runnable {
	private boolean isMoving;
	private boolean isMotorOn;
	private DoorState doorState;
	private ButtonLampState[] buttonLampStates;
	private ElevatorQueue destionationQueue;
	private ElevatorState state;
	private ElevatorStatus status;
	private List<ElevatorObserver> observers;
	private boolean stuckBetweenFloors;
	private boolean doorStuck;
	private final int TIME_BETWEEN_FLOORS = 7383; // milliseconds
	private final int TIME_BETWEEN_FLOORS_THRESHOLD = 7500; // maximum time for moving between floors or closing door in
	private final int DOOR_OPENING_CLOSING_TIME = 1500; // milliseconds
	private final int LOAD_UNLOAD_TIME = 6483; // milliseconds
	private final int DOOR_OPENING_CLOSING_TIME_THRESHOLD = 1700;
	private Optional<Thread> timer = Optional.empty();
	private List<ElevatorErrorEvent> errorList;
	private long startTime; // elevator internal timer

	/**
	 * Constructor for Elevator Class
	 *
	 */
	public Elevator(int numberOfFloors) {

		this.isMoving = false;
		this.isMotorOn = false;
		this.doorState = DoorState.CLOSED;
		this.destionationQueue = new ElevatorQueue(1);

		this.buttonLampStates = new ButtonLampState[numberOfFloors];
		Arrays.fill(buttonLampStates, ButtonLampState.OFF);

		this.state = new ElevatorInitState(this);
		this.observers = new ArrayList<>();
		this.stuckBetweenFloors = false;
		this.doorStuck = false;
		this.errorList = new ArrayList<>();

		startTime = System.currentTimeMillis();
	}

	public boolean isMotorOn() {
		return isMotorOn;
	}

	public void setMotorOn(boolean motorOn) {
		this.isMotorOn = motorOn;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean moving) {
		isMoving = moving;
	}

	public DoorState getDoorState() {
		return doorState;
	}

	public void setDoorState(DoorState doorState) {
		Logger.println("Door:   " + doorState.toString());
		this.doorState = doorState;
	}

	public Direction getDirection() {
		return this.destionationQueue.getDirection();
	}

	public ButtonLampState[] getButtonLampStates() {
		return buttonLampStates;
	}

	public void setButtonLampStates(ButtonLampState[] buttonLampStates) {
		this.buttonLampStates = buttonLampStates;
	}

	public ElevatorQueue getDestinationFloors() {
		return this.destionationQueue;
	}

	public void setState(ElevatorState state) {
		Logger.debugln("State: " + state.getClass().getSimpleName());
		this.state = state;
	}

	public ElevatorState getState() {
		return this.state;
	}

	public void addObserver(ElevatorObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(ElevatorObserver observer) {
		observers.remove(observer);
	}

	public void notifyObservers(ElevatorResponse message) {
		for (ElevatorObserver observer : observers) {
			observer.onEventProcessed(message);
		}
	}

	public void setStatus(ElevatorStatus status) {
		Logger.println("Status: " + status);
		this.status = status;
	}

	public ElevatorStatus getStatus() {
		return status;
	}

	public synchronized boolean isstuckBetweenFloors() {
		return stuckBetweenFloors;
	}

	public synchronized void setstuckBetweenFloors(boolean stuckBetweenFloors) {
		this.stuckBetweenFloors = stuckBetweenFloors;
	}

	public synchronized boolean isdoorStuck() {
		return doorStuck;
	}

	public synchronized void setdoorStuck(boolean doorStuck) {
		this.doorStuck = doorStuck;
	}

	public synchronized void addError(ElevatorErrorEvent error) {
		this.errorList.add(error);
	}

	/**
	 * @return the tIME_BTEWEEN_FLOORS
	 */
	public int getTIME_BETWEEN_FLOORS() {
		return TIME_BETWEEN_FLOORS;
	}

	/**
	 * @return the DOOR_CLOSING_TIME
	 */
	public int getDOOR_OPENING_CLOSING_TIME() {
		return DOOR_OPENING_CLOSING_TIME;
	}

	/**
	 * @return the lOAD_UNLOAD_TIME
	 */
	public int getLOAD_UNLOAD_TIME() {
		return LOAD_UNLOAD_TIME;
	}

	/**
	 * method to process events from elevator subsystem and return a complete
	 * message
	 *
	 * @param event
	 * @return Message
	 *
	 * @author Tao Lufula, 101164153
	 */
	public void processFloorEvent(FloorEvent event) {
		if (!isstuckBetweenFloors()) {
			var queue = this.getDestinationFloors();
			int destFloor = event.destFloor();
			int srcFloor = event.srcFloor();

			if (destFloor != 0 && srcFloor != destFloor) {
				queue.add(srcFloor, destFloor);
				this.getButtonLampStates()[destFloor] = ButtonLampState.ON;

				if (queue.getCurrentFloor() != queue.peek().get()) {
					this.setState(new MovingState(this));
				} else {
					queue.next();
					Logger.debugln("Opening doors");
					this.setDoorState(DoorState.OPEN);
					this.setState(new DoorOpenState(this));
				}

			} else {
				Logger.println("Invalid floor event");
			}
		} else {
			Logger.debugln("Cannot process floor Events, Elevator is shutDown");
		}
	}

	
	public Boolean checkAndDealWithFaults(ElevatorStatus status) {
		if (status.equals(ElevatorStatus.DoorOpen) || (status.equals(ElevatorStatus.DoorClose))) {
			if (isdoorStuck()) {
				setState(new StuckState(this));	
				return true;
			}
		} else if (status.equals(ElevatorStatus.Moving)) {
			if (isstuckBetweenFloors()) {
				setState(new StuckState(this));
				return true;
			}
		} 
		
		return false;
	}

	public void startTimer(ElevatorStatus status) throws InterruptedException {
		stopTimer();
		timer = Optional.of(new Thread(() -> {
			try {
				int previousFloor = destionationQueue.getCurrentFloor();

				if (status.equals(ElevatorStatus.DoorOpen)) {
					Thread.sleep(DOOR_OPENING_CLOSING_TIME_THRESHOLD);
					if (state.getClass().equals(DoorClosedState.class)) {
						return;
					}
					setdoorStuck(true);

				} else if (status.equals(ElevatorStatus.DoorClose)) {
					Thread.sleep(DOOR_OPENING_CLOSING_TIME_THRESHOLD);
					if (state.getClass().equals(DoorOpenState.class)) {
						return;
					}
					setdoorStuck(true);

				} else if (status.equals(ElevatorStatus.Moving)) {
					Thread.sleep(TIME_BETWEEN_FLOORS_THRESHOLD);
					if (previousFloor == destionationQueue.getCurrentFloor()) {
						setstuckBetweenFloors(true);
					}
				} else {

					return; // add more actions
				}
			} catch (InterruptedException e) {
				return;
			}

		}));
		timer.get().start();
	}

	public void stopTimer() throws InterruptedException {
		if (timer.isPresent()) {
			timer.get().interrupt();
			timer.get().join();
		}
		timer = Optional.empty();
	}

	public void processErrorEvent() {
		if (errorList.isEmpty()) {

			Logger.debugln("There are no errors to process at the moment");
			return;
		}

		for (int i = 0; i < errorList.size(); i++) {
			ElevatorErrorEvent er = errorList.get(i);
			var error = er.error();
			var waitTime = er.waiTime();
			long elapsedTime = System.currentTimeMillis() - startTime;

			if (elapsedTime >= waitTime) {
				if (error == ElevatorError.DoorStuck) {
					this.setdoorStuck(true);
				} else if (error == ElevatorError.StuckBetweenFloors) {
					this.setstuckBetweenFloors(true);
				} else {
					Logger.debugln("Unknown Error");
				}

				errorList.remove(er);
			} else {
				continue;
			}
		}
	}

	public void run() {
		while (true) {
			try {
				processErrorEvent();
				state.advance(this);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

}
