package sysc3303_elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.Direction;
import sysc3303_elevator.FloorEvent;
import sysc3303_elevator.FloorFormatReader;

/**
 * @author Quinn Parrott
 *
 */
class FloorFormatReaderTest {

	@Test
	void testSimple() throws IOException {
		ByteArrayInputStream e = new ByteArrayInputStream("14:05:15.0 2 up 4\n".getBytes());
		assertEquals(Optional.of(new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Up, 4)), (new FloorFormatReader(e)).next().first());
	}

	@Test
	void testMultiple() throws IOException {
		ByteArrayInputStream e = new ByteArrayInputStream("14:05:15.0 2 up 4\n14:05:15.0 1 down 3".getBytes());
		var reader = new FloorFormatReader(e);
		assertEquals(Optional.of(new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Up, 4)), reader.next().first());
		assertEquals(Optional.of(new FloorEvent(LocalTime.of(14, 5, 15, 0), 1, Direction.Down, 3)), reader.next().first());

	}

	@Test
	void toList() throws IOException {
		ByteArrayInputStream e = new ByteArrayInputStream("14:05:15.0 2 up 4\n14:05:15.0 1 down 3".getBytes());
		var reader = new FloorFormatReader(e);
		assertArrayEquals(new FloorEvent[]{
			new FloorEvent(LocalTime.of(14, 5, 15, 0), 2, Direction.Up, 4),
			new FloorEvent(LocalTime.of(14, 5, 15, 0), 1, Direction.Down, 3),
		},
			reader.toList().first().toArray()
		);
	}

}
