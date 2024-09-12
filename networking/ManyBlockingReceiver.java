package sysc3303_elevator.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sysc3303_elevator.Pair;

public class ManyBlockingReceiver<T> implements BlockingReceiver<TaggedMsg<Integer, T>>, Runnable {

	private BlockingQueue<TaggedMsg<Integer, T>> queue;
	private List<Thread> receiverThreads;
	private boolean isClose = false;

	public ManyBlockingReceiver(List<Pair<Integer, BlockingReceiver<T>>> receivers) {
		this.queue = new LinkedBlockingQueue<>();
		this.receiverThreads = new ArrayList<>();
		for (var receiver: receivers) {
			this.receiverThreads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					var channelId = receiver.first();
					var blockingReceiver = receiver.second();

					while (true) {
						try {
							queue.put(new TaggedMsg<>(channelId, blockingReceiver.take()));
							internalNotify();
						} catch (InterruptedException e) {
							break;
						}
					}

				}
			}));
		}
	}

	private synchronized void internalNotify() {
		this.notifyAll();
	}


	@Override
	public synchronized TaggedMsg<Integer, T> take() throws InterruptedException {
		while (this.queue.isEmpty()) {
			if (this.isClose) {
				throw new InterruptedException();
			}
			this.wait(); // Early return here if thread is interrupted
		}
		return this.queue.take();
	}

	@Override
	public void run() {
		for (var receiver: receiverThreads) {
			receiver.start();
		}

		// Complicated way to wait for the root thread to `.interrupt()`
		for (var receiver: receiverThreads) {
			try {
				receiver.join();
			} catch (InterruptedException e) {
				break;
			}
		}

		// Cleanup all the receiver threads by interrupting them
		for (var receiver: receiverThreads) {
			if (!receiver.isInterrupted()) {
				receiver.interrupt();
			}
		}

		// Make `this.take()` throw InterruptedException
		this.isClose = true;
		internalNotify();
	}
}
