package sysc3303_elevator;

public class Logger {
	private static final boolean DEBUG = false;
	private static final boolean PERFORMANCE = false;

	public synchronized static void debugln(String msg) {
		if (PERFORMANCE) {
			return;
		}
		if (DEBUG) {
			System.out.println(String.format("%18s: -%s", Thread.currentThread().getName(), msg));
		}
	}

	public synchronized static void println(String msg) {
		if (PERFORMANCE) {
			return;
		}
		System.out.println(String.format("%18s: %s", Thread.currentThread().getName(), msg));
	}

	public synchronized static void outputPerf(String msg) {
		if (PERFORMANCE) {
			System.out.println(msg);
		}
	}
}
