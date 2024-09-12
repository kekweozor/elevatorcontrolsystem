package sysc3303_elevator.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpClientQueue<T, S> implements Runnable {
	
	private InetAddress address;
	private int port;
	private DatagramSocket socket;
	private BlockingQueue<T> queue;

	public UdpClientQueue(InetAddress address, int port) throws SocketException {
		this.address = address;
		this.port = port;
		this.socket = new DatagramSocket();
		this.queue = new LinkedBlockingQueue<>();
	}
	
	public BlockingSender<S> getSender() {
		return new BlockingSender<S>() {
			@Override
			public void put(S e) throws InterruptedException {
				var stream = new ByteArrayOutputStream();
				try {
					(new ObjectOutputStream(stream)).writeObject(e);
					var data = stream.toByteArray();
					socket.send(new DatagramPacket(data, data.length, address, port));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}};
	}

	public BlockingReceiver<T> getReceiver() {
		return new BlockingChannelBuilder.ReceiverWrapper<>(this.queue);
	}
	
	public void run() {
		while (true) {
			byte data[] = new byte[10000];
			var receivePacket = new DatagramPacket(data, data.length, this.address, this.port);
			try {
				socket.receive(receivePacket);
				var objectInput = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
				this.queue.put((T) objectInput.readObject());
			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
