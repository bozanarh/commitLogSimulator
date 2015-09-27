package commitLogWriter;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Writer implements Runnable {

	private Uid uid;
	private String cid;
	
	//TODO: DE-hard code this; put it into config file
	private final int HIGH_WATERMARK=100;
	private ConcurrentLinkedQueue<String> queue;
	private Integer maxSleepTime;

	public Writer(String cid, ConcurrentLinkedQueue<String> queue, Uid uid, Integer maxSleepTime) {
		this.cid =cid;
		this.queue = queue;
		this.uid = uid;
		this.maxSleepTime = maxSleepTime; //max sleep time in seconds
	}

	private synchronized int getMyUid(){
		int id = uid.getValue();
		uid.setValue(++id);
		return id;
	}

	/*
	 * We simulate random arrivals of log commits using uniform distribution
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			//each Threads random sleep time
			int sleepTime = (int)(Math.random() * maxSleepTime);
			
			if(queue.size() < HIGH_WATERMARK) {
				int myUid = getMyUid();
				StringBuilder msg = new StringBuilder();
				msg.append(cid)
				.append(":")
				.append(myUid)
				.append(":")
				.append("Hello from thd: ")
				.append(Thread.currentThread().getId());
				queue.add(msg.toString());
		
			} else {
				//if high watermal has been reached go to sleep
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

}
