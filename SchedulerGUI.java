package sysc3303_elevator;

import java.awt.*;
import java.util.*;

import javax.swing.*;

public class SchedulerGUI<I> extends JFrame implements SchedulerUpdateListener<I> {

	private JTextArea textArea;
	private JPanel status;
	private FlowLayout layout = new FlowLayout();

	JPanel floorPanel = new JPanel();
	JPanel directionsPanel = new JPanel();
	JPanel statePanel = new JPanel();
	JPanel elevatorPanel = new JPanel();
	JPanel doorPanel = new JPanel();
	JLabel label = new JLabel("Elevator #");
	JLabel label1 = new JLabel("Current Elevator State");
	JLabel label2 = new JLabel("Current Elevator Direction");
	JLabel label3 = new JLabel("Current Elevator Floor");
	JLabel label4 = new JLabel("Elevator Door Status");

	private ArrayList<JTextField> states = new ArrayList<JTextField>();
	private ArrayList<JTextField> floors = new ArrayList<JTextField>();
	private ArrayList<JTextField> directions = new ArrayList<JTextField>();

	private ArrayList<I> channelIDs = new ArrayList<>();
	int iterator = 0;

	public SchedulerGUI() {
		super("Elevator SchedulerGUI");

		Box box = Box.createVerticalBox();
		textArea = new JTextArea(5, 40);
		textArea.setEditable(false);

		status = new JPanel();
		status.setLayout(layout);

		elevatorPanel.setLayout(new BoxLayout(elevatorPanel, BoxLayout.Y_AXIS));
		floorPanel.setLayout(new BoxLayout(floorPanel, BoxLayout.Y_AXIS));
		directionsPanel.setLayout(new BoxLayout(directionsPanel, BoxLayout.Y_AXIS));
		statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));
		doorPanel.setLayout(new BoxLayout(doorPanel, BoxLayout.Y_AXIS));

		elevatorPanel.add(label);
		statePanel.add(label1);
		directionsPanel.add(label2);
		floorPanel.add(label3);
		doorPanel.add(label4);

		status.add(elevatorPanel);
		status.add(statePanel);
		status.add(directionsPanel);
		status.add(floorPanel);

		box.add(status);
		getContentPane().add(box);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void sendLogMessage(String str) {
		textArea.append(str);
	}

	private void addElevatorToView() {
		elevatorPanel.add(new JLabel("Elevator " + (iterator + 1)));
		statePanel.add(states.get(iterator));
		directionsPanel.add(directions.get(iterator));
		floorPanel.add(floors.get(iterator));

		this.pack();
	}

	@Override
	public void updateElevatorStatus(I channelId, String floor, String direction, String state) {
		if (!channelIDs.contains(channelId)) {
			channelIDs.add(channelId);
			iterator = channelIDs.indexOf(channelId);
			floors.add(new JTextField(floor));
			directions.add(new JTextField(direction));
			states.add(new JTextField(state));
			addElevatorToView();
		} else {
			iterator = channelIDs.indexOf(channelId);
			floors.get(iterator).setText(floor);
			directions.get(iterator).setText(direction);
			states.get(iterator).setText(state);
		}
	}
}
