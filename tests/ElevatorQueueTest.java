package sysc3303_elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.Direction;
import sysc3303_elevator.ElevatorQueue;

public class ElevatorQueueTest {

	@Test
    public void testUp() throws InterruptedException {
        var queue = new ElevatorQueue(2);

        assertEquals(Direction.Down, queue.getDirection());
        assertArrayEquals(new Integer[] {}, queue.getQueue().toArray());

        queue.add(5);
        assertEquals(Direction.Up, queue.getDirection());
        assertArrayEquals(new Integer[] {
            5
        }, queue.getQueue().toArray());

        queue.add(0);
        assertArrayEquals(new Integer[] {
            5,
            0
        }, queue.getQueue().toArray());

        queue.add(3);
        assertArrayEquals(new Integer[] {
            3,
            5,
            0,
        }, queue.getQueue().toArray());

        queue.add(1);
        assertArrayEquals(new Integer[] {
            3,
            5,
            1,
            0,
        }, queue.getQueue().toArray());

        queue.add(6);
        assertArrayEquals(new Integer[] {
            3,
            5,
            6,
            1,
            0,
        }, queue.getQueue().toArray());

    }
	@Test
    public void testDependentSimple() throws InterruptedException {
        var queue = new ElevatorQueue(2);

        queue.add(5, 2);
        assertArrayEquals(new Integer[] {
            5
        }, queue.getQueue().toArray());

        assertEquals(5, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
            2
        }, queue.getQueue().toArray());

        assertEquals(2, queue.peek().get());
        queue.next();

        assertEquals(Optional.empty(), queue.peek());
    }

	@Test
    public void testDependent() throws InterruptedException {
        var queue = new ElevatorQueue(2);

        assertEquals(Direction.Down, queue.getDirection());
        assertArrayEquals(new Integer[] {}, queue.getQueue().toArray());

        queue.add(5, 2);
        assertEquals(Direction.Up, queue.getDirection());
        assertArrayEquals(new Integer[] {
            5
        }, queue.getQueue().toArray());

        queue.add(0, 5);
        assertArrayEquals(new Integer[] {
            5,
            0
        }, queue.getQueue().toArray());

        queue.add(3, 1);
        assertArrayEquals(new Integer[] {
            3,
            5,
            0,
        }, queue.getQueue().toArray());

        assertEquals(3, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
            5,
            1,
            0,
        }, queue.getQueue().toArray());

        assertEquals(5, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
            2,
            1,
            0,
        }, queue.getQueue().toArray());

        assertEquals(2, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
            1,
            0,
        }, queue.getQueue().toArray());

        assertEquals(1, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
            0,
        }, queue.getQueue().toArray());

        assertEquals(0, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
            5,
        }, queue.getQueue().toArray());

        assertEquals(5, queue.peek().get());
        queue.next();
        assertArrayEquals(new Integer[] {
        }, queue.getQueue().toArray());
        assertEquals(Optional.empty(), queue.peek());
    }

	@Test
    public void testDown() throws InterruptedException {
        var queue = new ElevatorQueue(2);

        assertEquals(Direction.Down, queue.getDirection());
        assertArrayEquals(new Integer[] {}, queue.getQueue().toArray());

        queue.add(0);
        assertEquals(Direction.Down, queue.getDirection());
        assertArrayEquals(new Integer[] {
            0
        }, queue.getQueue().toArray());

        queue.add(5);
        assertArrayEquals(new Integer[] {
            0,
            5,
        }, queue.getQueue().toArray());

        queue.add(3);
        assertArrayEquals(new Integer[] {
            0,
            3,
            5,
        }, queue.getQueue().toArray());

        queue.add(1);
        assertArrayEquals(new Integer[] {
            1,
            0,
            3,
            5,
        }, queue.getQueue().toArray());

        queue.add(6);
        assertArrayEquals(new Integer[] {
            1,
            0,
            3,
            5,
            6,
        }, queue.getQueue().toArray());

    }

	@Test
    public void testNext() throws InterruptedException {
        var queue = new ElevatorQueue(2);

        queue.add(0);
        queue.add(5);
        queue.add(3);
        queue.add(1);
        queue.add(6);
        queue.add(7);

        assertArrayEquals(new Integer[] {
            1,
            0,
            3,
            5,
            6,
            7,
        }, queue.getQueue().toArray());
        assertEquals(Direction.Down, queue.getDirection());

        assertEquals(Optional.of(1), queue.peek());
        assertEquals(Direction.Down, queue.getDirection());

        queue.next();
        assertEquals(Optional.of(0), queue.peek());
        assertEquals(Direction.Down, queue.getDirection());

        queue.next();
        assertEquals(Optional.of(3), queue.peek());
        assertEquals(Direction.Up, queue.getDirection());

        queue.next();
        assertEquals(Optional.of(5), queue.peek());
        assertEquals(Direction.Up, queue.getDirection());

        queue.add(0);
        assertEquals(Optional.of(5), queue.peek());
        assertEquals(Direction.Up, queue.getDirection());

        queue.add(1);
        assertEquals(Optional.of(5), queue.peek());
        assertEquals(Direction.Up, queue.getDirection());
        assertArrayEquals(new Integer[] {
            5,
            6,
            7,
            1,
            0,
        }, queue.getQueue().toArray());

        queue.next();
        assertEquals(Optional.of(6), queue.peek());
        assertEquals(Direction.Up, queue.getDirection());

        queue.next();
        assertEquals(Optional.of(7), queue.peek());
        assertEquals(Direction.Up, queue.getDirection());

        queue.next();
        assertEquals(Optional.of(1), queue.peek());
        assertEquals(Direction.Down, queue.getDirection());

        queue.next();
        assertEquals(Optional.of(0), queue.peek());
        assertEquals(Direction.Down, queue.getDirection());
    }

	@Test
    public void testAdvanceUp() throws InterruptedException {
        var queue = new ElevatorQueue(2);
        queue.add(4);
        assertEquals(2, queue.getCurrentFloor());

        queue.advance();
        assertEquals(3, queue.getCurrentFloor());

        queue.advance();
        assertEquals(4, queue.getCurrentFloor());

        queue.advance();
        assertEquals(4, queue.getCurrentFloor());
    }

	@Test
    public void testAdvanceDown() throws InterruptedException {
        var queue = new ElevatorQueue(2);
        queue.add(0);
        assertEquals(2, queue.getCurrentFloor());

        queue.advance();
        assertEquals(1, queue.getCurrentFloor());

        queue.advance();
        assertEquals(0, queue.getCurrentFloor());

        queue.advance();
        assertEquals(0, queue.getCurrentFloor());
    }
}
