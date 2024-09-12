package sysc3303_elevator;

public interface SchedulerUpdateListener<I> {
	void sendLogMessage(String str);
	void updateElevatorStatus(I channelId, String floor, String direction, String state);
	
}
