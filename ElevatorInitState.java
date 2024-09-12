
package sysc3303_elevator;

public class ElevatorInitState implements ElevatorState {
    public ElevatorInitState(Elevator elevator) {
        elevator.setStatus(ElevatorStatus.Init);
    }

    @Override
    public void advance(Elevator elevator) {
        var response = new ElevatorResponse(elevator.getDestinationFloors().getCurrentFloor(), ElevatorStatus.Idle, Direction.Down);
        elevator.notifyObservers(response);

        elevator.setState(new IdleState(elevator));
    }
}
