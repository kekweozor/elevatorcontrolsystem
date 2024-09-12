package sysc3303_elevator.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.Direction;
import sysc3303_elevator.Floor;
import sysc3303_elevator.FloorEvent;
import sysc3303_elevator.Message;
import sysc3303_elevator.networking.BlockingChannelBuilder;
import sysc3303_elevator.networking.BlockingReceiver;
import sysc3303_elevator.networking.BlockingSender;

/**
 * FloorTest
 * test if the floor class is correctly passingfloorevents to the scheduler
 * @author Hamza
 * @version 1.0
 *
 */

public class FloorTest {
	/**
	 * testValidation1
	 * pass a valid floorEvent method validateRequest
	 * @throws IOException
	 */
	@Test
	void testValidation1() throws IOException {
		BlockingSender<FloorEvent> floorToScheduler = BlockingChannelBuilder.FromBlockingQueue(new ArrayBlockingQueue<FloorEvent>(5)).first();
		BlockingReceiver<Message> schedulerToFloor = BlockingChannelBuilder.FromBlockingQueue(new ArrayBlockingQueue<Message>(5)).second();
		ArrayList<FloorEvent> eventList = new ArrayList<>();
		FloorEvent floorevent = new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Up, 4);
		eventList.add(floorevent);
		Floor floor = new Floor(floorToScheduler ,schedulerToFloor , eventList);
		Direction direction = eventList.get(0).direction();
		int newFloor = eventList.get(0).destFloor();
		int currFloor = eventList.get(0).srcFloor();
		assertEquals(floor.validateRequest(direction, newFloor, currFloor), true);

	}
	/**
	 * testValidation2
	 * pass a valid floorEvent method validateRequest
	 * @throws IOException
	 */
	@Test
	void testValidation2() throws IOException {
		BlockingSender<FloorEvent> floorToScheduler = BlockingChannelBuilder.FromBlockingQueue(new ArrayBlockingQueue<FloorEvent>(5)).first();
		BlockingReceiver<Message> schedulerToFloor = BlockingChannelBuilder.FromBlockingQueue(new ArrayBlockingQueue<Message>(5)).second();
		ArrayList<FloorEvent> eventList = new ArrayList<>();
		FloorEvent floorevent = new FloorEvent(LocalTime.of(14, 5, 15, 0), 6, Direction.Up, 4);
		eventList.add(floorevent);
		Floor floor = new Floor(floorToScheduler ,schedulerToFloor , eventList);
		Direction direction = eventList.get(0).direction();
		int newFloor = eventList.get(0).destFloor();
		assertEquals(floor.validateRequest(direction, newFloor, newFloor), false);

	}
	@Test
	/**
	 * testQueuePassing1
	 * Test if floorToScheduler is sending the floorEvents to the Queue
	 * @throws IOException
	 */
	void testQueuePassing1() throws IOException {
		var floorToSchedulerQueue = new ArrayBlockingQueue<FloorEvent>(5);
		BlockingSender<FloorEvent> floorToScheduler = BlockingChannelBuilder.FromBlockingQueue(floorToSchedulerQueue).first();
		BlockingReceiver<Message> schedulerToFloor = BlockingChannelBuilder.FromBlockingQueue(new ArrayBlockingQueue<Message>(5)).second();
		ArrayList<FloorEvent> eventList = new ArrayList<>();
		FloorEvent floorevent = new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Up, 4);
		Floor floor = new Floor(floorToScheduler ,schedulerToFloor , eventList);
		floor.floorToScheduler(floorevent);
		assertEquals(floorToSchedulerQueue.isEmpty(), false);
		}
	@Test
	/**
	 * testQueuePassing2
	 * Test if floorToScheduler is sending the expected floorEvent to the queue
	 * @throws IOException
	 */
	void testQueuePassing2() throws IOException {
		var floorToSchedulerQueue = new ArrayBlockingQueue<FloorEvent>(5);
		BlockingSender<FloorEvent> floorToScheduler = BlockingChannelBuilder.FromBlockingQueue(floorToSchedulerQueue).first();
		BlockingReceiver<Message> schedulerToFloor = BlockingChannelBuilder.FromBlockingQueue(new ArrayBlockingQueue<Message>(5)).second();
		ArrayList<FloorEvent> eventList = new ArrayList<>();
		FloorEvent floorevent = new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Up, 4);
		FloorEvent floorevent2 = new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Down, 1);
		Floor floor = new Floor(floorToScheduler ,schedulerToFloor , eventList);
		floor.floorToScheduler(floorevent);
		floor.floorToScheduler(floorevent2);
		assertTrue(floorToSchedulerQueue.contains(floorevent));
		}
}
