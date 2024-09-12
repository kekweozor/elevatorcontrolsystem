package sysc3303_elevator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * @author Quinn Parrott
 *
 */
public class FloorFormatReader {
	private BufferedReader inputStream;
	public static final String floorRegex = "\\d{2}:\\d{2}:\\d{2}\\.\\d\\s\\d+\\s\\w+\\s\\d+";
	public static final String errorRegex = "\\d{2}:\\d{2}:\\d{2}\\.\\d,\\d+,\\w+";
	private Optional<LocalTime> firstEventTime;

	public FloorFormatReader(InputStream inputStream) {
		this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
		this.firstEventTime = Optional.empty();
	}

	public Pair<Optional<FloorEvent>, Optional<ElevatorErrorEvent>> next() throws IOException {
		String line = inputStream.readLine();

		if (line == null) {
			throw new IOException();
		}

		if (line.matches(floorRegex)) {
			var attributes = line.split(" ");
			LocalTime localTime = getLocalTime(attributes[0]);

			return (new Pair<>(Optional.of(new FloorEvent(localTime, Integer.parseInt(attributes[1]),
					attributes[2].toLowerCase().charAt(0) == 'u' ? Direction.Up : Direction.Down,
					Integer.parseInt(attributes[3]))), Optional.empty()));

		} else if (line.matches(errorRegex)) {

			String[] parts = line.split(",");

			LocalTime localTime = getLocalTime(parts[0]);

			var millis = MILLIS.between(this.firstEventTime.get(), localTime);

			return (new Pair<>(Optional.empty(),
					Optional.of(new ElevatorErrorEvent(Integer.parseInt(parts[1]),
							parts[2].toLowerCase().contains("door") ? ElevatorError.DoorStuck
									: ElevatorError.StuckBetweenFloors,
							millis))));
		} else {
			return null;
		}
	}

	public LocalTime getLocalTime(CharSequence timestamp) {

		DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm:ss[.n]");
		LocalTime localTime = LocalTime.parse(timestamp, parser);
		if (this.firstEventTime.isEmpty()) {
			this.firstEventTime = Optional.of(localTime);
		}

		return localTime;
	}

	public Pair<ArrayList<FloorEvent>, ArrayList<ElevatorErrorEvent>> toList() {
		var floorEvents = new ArrayList<FloorEvent>();
		var errorEvents = new ArrayList<ElevatorErrorEvent>();
		while (true) {
			try {
				var event = this.next();
				if (event != null) {
					if (!event.first().isEmpty()) {
						floorEvents.add(event.first().get());
					} else {
						errorEvents.add(event.second().get());
					}
				}
			} catch (IOException e) {
				break;
			}
		}
		return (new Pair<>(floorEvents, errorEvents));
	}
}
