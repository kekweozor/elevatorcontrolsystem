package sysc3303_elevator.networking;

import java.util.concurrent.BlockingQueue;

import sysc3303_elevator.Pair;

public class BlockingChannelBuilder {
	public static class SenderWrapper<T> implements BlockingSender<T> {
		private BlockingQueue<T> queue;
		public SenderWrapper(BlockingQueue<T> queue) {
			this.queue = queue;
		}

		@Override
		public void put(T e) throws InterruptedException {
			this.queue.put(e);
		}
	}

	public static class ReceiverWrapper<T> implements BlockingReceiver<T> {
		private BlockingQueue<T> queue;
		public ReceiverWrapper(BlockingQueue<T> queue) {
			this.queue = queue;
		}

		@Override
		public T take() throws InterruptedException {
			return this.queue.take();
		}
	}

	public static <T> Pair<BlockingSender<T>, BlockingReceiver<T>> FromBlockingQueue(BlockingQueue<T> queue) {
		return new Pair<>(new SenderWrapper<>(queue), new ReceiverWrapper<>(queue));
	}
}
