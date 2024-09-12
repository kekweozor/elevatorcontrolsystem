package sysc3303_elevator.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.Pair;
import sysc3303_elevator.networking.BlockingChannelBuilder;
import sysc3303_elevator.networking.BlockingReceiver;
import sysc3303_elevator.networking.ManyBlockingReceiver;
import sysc3303_elevator.networking.TaggedMsg;

public class ManyBlockingReceiverTest {

	@Test
	void test() throws Throwable {
		var channel1 = BlockingChannelBuilder.FromBlockingQueue(new LinkedBlockingQueue<String>());
		var channel2 = BlockingChannelBuilder.FromBlockingQueue(new LinkedBlockingQueue<String>());
		var channel3 = BlockingChannelBuilder.FromBlockingQueue(new LinkedBlockingQueue<String>());

		var receivers = new ArrayList<Pair<Integer, BlockingReceiver<String>>>();
		receivers.add(new Pair<>(1, channel1.second()));
		receivers.add(new Pair<>(2, channel2.second()));
		receivers.add(new Pair<>(3, channel3.second()));

		var queue = new LinkedBlockingQueue<TaggedMsg<Integer, String>>();
		ManyBlockingReceiver<String> messageReceiver = new ManyBlockingReceiver<>(receivers);

		var receiverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				var t = new Thread(messageReceiver);
				t.start();

				while (true) {
					try {
						var msg = messageReceiver.take();
						queue.add(msg);
					} catch (InterruptedException _e) {
						t.interrupt();
						break;
					}
				}
			}
		});
		receiverThread.start();

		{
			channel1.first().put("first");
			var expected = new Pair<>(1, "first");
			var actual = queue.take();
			assertEquals(expected.first(), actual.id());
			assertEquals(expected.second(), actual.content());
		}

		{
			channel2.first().put("second");
			var expected = new Pair<>(2, "second");
			var actual = queue.take();
			assertEquals(expected.first(), actual.id());
			assertEquals(expected.second(), actual.content());
		}

		{
			channel3.first().put("third");
			var expected = new Pair<>(3, "third");
			var actual = queue.take();
			assertEquals(expected.first(), actual.id());
			assertEquals(expected.second(), actual.content());
		}

		{
			channel2.first().put("fourth");
			var expected = new Pair<>(2, "fourth");
			var actual = queue.take();
			assertEquals(expected.first(), actual.id());
			assertEquals(expected.second(), actual.content());
		}

		{
			channel1.first().put("fifth");
			var expected = new Pair<>(1, "fifth");
			var actual = queue.take();
			assertEquals(expected.first(), actual.id());
			assertEquals(expected.second(), actual.content());
		}

		{
			channel1.first().put("fifth");
			var expected = new Pair<>(1, "fifth");
			var actual = queue.take();
			assertEquals(expected.first(), actual.id());
			assertEquals(expected.second(), actual.content());
		}

		// Cleanup
		receiverThread.interrupt();

		assertThrows(InterruptedException.class, () -> {
			messageReceiver.take();
		});

	}
}
