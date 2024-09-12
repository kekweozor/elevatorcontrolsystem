package sysc3303_elevator;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author Quinn Parrott
 *
 */
public record FloorEvent(LocalTime time, int srcFloor, Direction direction, int destFloor) implements Serializable { }
