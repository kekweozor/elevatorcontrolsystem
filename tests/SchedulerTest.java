package sysc3303_elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.Direction;
import sysc3303_elevator.ElevatorResponse;
import sysc3303_elevator.ElevatorStatus;
import sysc3303_elevator.FloorEvent;
import sysc3303_elevator.Message;
import sysc3303_elevator.Scheduler;
import sysc3303_elevator.networking.BlockingMultiplexer;
import sysc3303_elevator.networking.TaggedMsg;

class SchedulerTest {

	@Test
	void test() throws Throwable {
		var event1 = new FloorEvent(null, 0, Direction.Down, 0);
		var event2 = new FloorEvent(null, 1, Direction.Down, 0);
		var msg1 = new ElevatorResponse(1, ElevatorStatus.Idle, Direction.Down);
		var msg2 = new ElevatorResponse(4, ElevatorStatus.Idle, Direction.Down);

		var elevatorMux = new BlockingMultiplexer<Integer, FloorEvent, ElevatorResponse>() {
			public int count = 0;
			public int takeCount = 0;

			@Override
			public void put(TaggedMsg<Integer, FloorEvent> e) throws InterruptedException {
				count++;
			}

			@Override
			public TaggedMsg<Integer, ElevatorResponse> take() throws InterruptedException {
				takeCount++;
				switch (takeCount - 1) {
					case 0: {
						return new TaggedMsg<Integer,ElevatorResponse>(0, msg1);
					}
					case 1: {
						return new TaggedMsg<Integer,ElevatorResponse>(0, msg2);
					}
					default:
						while (true) {
							Thread.sleep(1000);
						}
				}

			}

		};
		var floorMux = new BlockingMultiplexer<Integer, Message, FloorEvent>() {
			public int count = 0;
			public int takeCount = 0;

			@Override
			public void put(TaggedMsg<Integer, Message> e) throws InterruptedException {
				count++;
			}

			@Override
			public TaggedMsg<Integer, FloorEvent> take() throws InterruptedException {
				takeCount++;
				switch (takeCount - 1) {
					case 0: {
						return new TaggedMsg<Integer,FloorEvent>(0, event1);
					}
					case 1: {
						return new TaggedMsg<Integer,FloorEvent>(0, event2);
					}
					default:
						while (true) {
							Thread.sleep(1000);
						}
				}

			}

		};

		var e1 = new Scheduler<>(elevatorMux, floorMux);

		var t1 = new Thread(e1);

		t1.start();
		while (elevatorMux.takeCount < 3 || floorMux.takeCount < 3) {
			Thread.sleep(100);
		}
		t1.interrupt();
		t1.join();

		assertEquals(3, floorMux.takeCount);
		assertEquals(1, elevatorMux.count);
		assertEquals(3, elevatorMux.takeCount);
		assertEquals(2, floorMux.count);
	}

}
