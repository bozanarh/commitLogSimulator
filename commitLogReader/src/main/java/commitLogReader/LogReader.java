package commitLogReader;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LogReader implements Runnable {

	private ConcurrentLinkedQueue<String> queue;
	private String cid;

	public LogReader(String cid, ConcurrentLinkedQueue<String> queue) {
		this.queue = queue;
		this.cid = cid;
	}

	/**
	 * Reader thread - reading data from the queue and printing
	 * on the stdin;
	 */
	public void run() {
		while (true) {
			if (queue.size() > 0) {
				String msg = queue.remove();
				//System.out.printf("Reader(%s): %d => %s\n", cid, Thread.currentThread().getId(), msg);
				System.out.printf("%s\n", msg);
			}
		}
	}

}
