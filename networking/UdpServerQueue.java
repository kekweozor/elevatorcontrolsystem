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

public class UdpServerQueue<S, R> extends BlockingMultiplexer<UdpServerQueue.UdpClientIdentifier, S, R>
		implements Runnable {
	public static record UdpClientIdentifier(int port, InetAddress address) {
	}

	private DatagramSocket socket;
	private BlockingQueue<TaggedMsg<UdpClientIdentifier, R>> queue;

	public UdpServerQueue(int port) throws SocketException {
		this.socket = new DatagramSocket(port);
		this.queue = new LinkedBlockingQueue<>();
	}

	public void run() {
		while (true) {
			byte data[] = new byte[10000];
			var receivePacket = new DatagramPacket(data, data.length);
			try {
				socket.receive(receivePacket);
				var objectInput = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
				Object obj = objectInput.readObject();
				this.queue.put(new TaggedMsg<UdpClientIdentifier, R>(
						new UdpClientIdentifier(receivePacket.getPort(), receivePacket.getAddress()), (R) obj));
			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	@Override
	public void put(TaggedMsg<UdpClientIdentifier, S> e) throws InterruptedException {
		var stream = new ByteArrayOutputStream();
		try {
			(new ObjectOutputStream(stream)).writeObject(e.content());
			var data = stream.toByteArray();
			var ident = e.id();
			socket.send(new DatagramPacket(data, data.length, ident.address(), ident.port()));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public TaggedMsg<UdpClientIdentifier, R> take() throws InterruptedException {
		return this.queue.take();
	}
}
