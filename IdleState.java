/**
 *
 */
package sysc3303_elevator;

public class IdleState implements ElevatorState {
    public IdleState(Elevator elevator) {
        elevator.setStatus(ElevatorStatus.Idle);
    }

    @Override
    public void advance(Elevator elevator) {
        // Do nothing, elevator is idle waiting for events
    }
}
