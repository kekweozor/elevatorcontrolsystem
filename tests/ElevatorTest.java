package sysc3303_elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.ButtonLampState;
import sysc3303_elevator.Direction;
import sysc3303_elevator.DoorClosedState;
import sysc3303_elevator.DoorOpenState;
import sysc3303_elevator.Elevator;
import sysc3303_elevator.ElevatorErrorEvent;
import sysc3303_elevator.ElevatorResponse;
import sysc3303_elevator.ElevatorSubsystem;
import sysc3303_elevator.FloorEvent;
import sysc3303_elevator.MovingState;
import sysc3303_elevator.ShutDownState;
import sysc3303_elevator.StuckState;
import sysc3303_elevator.networking.BlockingReceiver;
import sysc3303_elevator.networking.BlockingSender;

class ElevatorTest {


	@Test
	void test() throws Throwable {

		var event1 = new FloorEvent(null, 2, Direction.Down, 1);
		var event2 = new FloorEvent(null, 3, Direction.Up, 4);

		var inbound = new BlockingReceiver<FloorEvent>() {
			public int takeCount = 0;
			public FloorEvent take() throws InterruptedException {
				takeCount++;
				switch (takeCount - 1) {
				case 0: {
					return event1;
				}
				case 1: {
					return event2;
				}
				default:
					throw new InterruptedException();
				}

			};
		};

		var outbound = new BlockingSender<ElevatorResponse>() {
			public int count = 0;
			@Override
			public void put(ElevatorResponse e) throws InterruptedException {
				count++;
			}
		};
		
		var list = new ArrayList<ElevatorErrorEvent>();

		var e1 = new ElevatorSubsystem(
				5,
				1,
				inbound,
				outbound,
				list
		);

		var t1 = new Thread(e1, "Elev-Sub");


		t1.start();
		t1.join();

		assertEquals(3, inbound.takeCount);
		assertEquals(1, outbound.count);
	}

	@Test
    public void testProcessFloorEvent() throws InterruptedException {

		var event1 = new FloorEvent(null, 5, Direction.Down, 3);

        Elevator elevator = new Elevator(10);
        assertEquals(elevator.getDestinationFloors().getCurrentFloor(), Integer.valueOf(1));

        elevator.processFloorEvent(event1);

		assertArrayEquals(new Integer[] {
			3,
			5,
		}, elevator.getDestinationFloors().getQueue().toArray());
        assertEquals(elevator.getButtonLampStates()[3], ButtonLampState.ON);
        assertEquals(elevator.getState().getClass(), MovingState.class);

        elevator.getState().advance(elevator);
        assertEquals(elevator.getDestinationFloors().getCurrentFloor(), Integer.valueOf(3));
        assertEquals(elevator.getButtonLampStates()[3], ButtonLampState.OFF);
        assertEquals(elevator.getState().getClass(), DoorOpenState.class);

		assertArrayEquals(new Integer[] {
			5,
		}, elevator.getDestinationFloors().getQueue().toArray());

        elevator.getState().advance(elevator);
        assertEquals(elevator.getButtonLampStates()[5], ButtonLampState.OFF);
        assertEquals(elevator.getState().getClass(), DoorClosedState.class);

        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), MovingState.class);

        elevator.getState().advance(elevator);

        assertEquals(elevator.getButtonLampStates()[5], ButtonLampState.OFF);
        assertEquals(elevator.getState().getClass(), DoorOpenState.class);
        
        elevator.getState().advance(elevator);
        
        assertEquals(elevator.getDestinationFloors().getCurrentFloor(), Integer.valueOf(5));
        assertTrue(elevator.getDestinationFloors().peek().isEmpty());
    }
	
	@Test
    public void testFaults() throws InterruptedException {

		var event1 = new FloorEvent(null, 2, Direction.Up, 6);

        Elevator elevator = new Elevator(10);
        assertEquals(elevator.getDestinationFloors().getCurrentFloor(), Integer.valueOf(1));

        elevator.processFloorEvent(event1);

		assertArrayEquals(new Integer[] {
			2,
			6,
		}, elevator.getDestinationFloors().getQueue().toArray());
		
        assertEquals(elevator.getButtonLampStates()[6], ButtonLampState.ON);
		assertEquals(elevator.getState().getClass(), MovingState.class);
        
        elevator.getState().advance(elevator);
        assertEquals(elevator.getDestinationFloors().getCurrentFloor(), Integer.valueOf(2));
        assertEquals(elevator.getState().getClass(), DoorOpenState.class);
        
        elevator.setdoorStuck(true); //trigger fault - door stuck open
        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), StuckState.class);
        
        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), DoorClosedState.class);
        
        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), MovingState.class);
        
        
        elevator.setstuckBetweenFloors(true); //trigger fault - elevator stuck between floors
        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), StuckState.class);
        
        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), ShutDownState.class);
        
        elevator.getState().advance(elevator);
        assertEquals(elevator.getState().getClass(), ShutDownState.class); //State will not change as elevator is shutdown
        
    }

}
