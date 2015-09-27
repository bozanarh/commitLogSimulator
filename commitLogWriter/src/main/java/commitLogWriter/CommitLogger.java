package commitLogWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.directory.NoSuchAttributeException;

public class CommitLogger {
	private static Uid[] uids;
	private static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	private HashMap<String, Integer> writersMap;
	private int sleepTime;	

	public CommitLogger(int sleepTime){
		this.sleepTime = sleepTime;
	}
	
	/**
	 * read cid and number of thread sharing the same cid
	 * from the config file (fname)
	 * 
	 * @param fname
	 * @throws IOException
	 */
	private void readConfig(String fname) throws IOException {
		File f = new File(fname);
		BufferedReader br = new BufferedReader(new FileReader(f));
		writersMap = new HashMap<String, Integer>();
		if (br != null) {
			String currLine;
			while((currLine = br.readLine()) != null ){
				currLine = currLine.replaceAll("\\s","");
				if( currLine.charAt(0) == '#') continue;
				String[] tokens = currLine.split("=");
				Integer numReadersInCid = Integer.valueOf(tokens[1]);
				writersMap.put(tokens[0],  numReadersInCid);			}
		}
		br.close();
	}
	
	/*
	 * Writers write data into a queue and main thread
	 * takes data from the queue and prints in on stdout
	 * 
	 */
	private void save() throws FileNotFoundException, InterruptedException {
	
		while (true) {
			if (queue.size() > 0){
				String msg = queue.remove();
				System.out.println(msg);
			}else{
				Thread.sleep(1);
			}
		}
	}

	/*
	 * Create writer threads and pass to each one queue which writers with the
	 * same cid share
	 * 
	 */
	public void start() throws InterruptedException, FileNotFoundException {
		int numCids = writersMap.size();
		uids = new Uid[numCids];
		
		Set<Entry<String, Integer>> writersSet = writersMap.entrySet();
		int i=0;
		for (Entry<String,Integer> e : writersSet) {
			Integer numWritersSharingCid = e.getValue();
			uids[i] = new Uid();
			
			for( int j=0; j< numWritersSharingCid; j++) {	
				Runnable r = new Writer(e.getKey(), queue, uids[i], sleepTime);
				new Thread(r).start();
			}
			++i;
		}
		
		save();
	}

	public static void main(String []args) throws InterruptedException, NoSuchAttributeException, IOException {
		if (args.length == 0 || args[0] == null) throw new NoSuchAttributeException("provide config file");
		int maxSleepTime = 60;
		if (args.length >= 2 && args[1] != null) maxSleepTime= Integer.valueOf(args[1]);
		CommitLogger logger = new CommitLogger(maxSleepTime);
		logger.readConfig(args[0]);
		logger.start();
	}
}
