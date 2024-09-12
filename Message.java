package sysc3303_elevator;

import java.io.Serializable;

public record Message(String messageToSend) implements Serializable {}
