package commitLogReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.directory.NoSuchAttributeException;


public class CommitLogger {
	private static Map<String, ConcurrentLinkedQueue<String>> queuesMap = new HashMap<String, ConcurrentLinkedQueue<String>>();
	private HashMap<String, Integer> readersMap;
	
	
	/**
	 * Read configuration file and store data in readerMap
	 * readersMap contain cid and queue shared by all threads with the 
	 * same cid
	 * 
	 * @param fname - name of the file
	 * @throws IOException
	 */
	private void readConfig(String fname) throws IOException {
		File f = new File(fname);
		BufferedReader br = new BufferedReader(new FileReader(f));
		readersMap = new HashMap<String, Integer>();
		if (br != null) {
			String currLine;
			while((currLine = br.readLine()) != null ){
				currLine = currLine.replaceAll("\\s","");
				if( currLine.charAt(0) == '#') continue;
				String[] tokens = currLine.split("=");
				String cid = tokens[0];
				Integer numReadersInCid = Integer.valueOf(tokens[1]);
				readersMap.put(cid, numReadersInCid);			}
		}
		br.close();
	}
	
	/**
	 * create reader threads and assign to each of them corresponding
	 * queue they will read from
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void start() throws InterruptedException, IOException {
	
		Set<Entry<String, Integer>> readersSet = readersMap.entrySet();
		int i=0;
		for (Entry<String,Integer> e : readersSet) {
			ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
			String cid = e.getKey();
			queuesMap.put(cid, queue);
			Runnable r = new LogReader(cid, queue);
			new Thread(r).start();
		}
				
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if (br != null) {
			String currLine;
			while((currLine = br.readLine()) != null ){

				String[] tokens = currLine.split(":");

				ConcurrentLinkedQueue<String> queue = queuesMap.get(tokens[0]);
				queue.add(currLine);        	
			}
		}
	}
	
	
	public static void main(String []args) throws InterruptedException, IOException, NoSuchAttributeException {
		if (args.length == 0) throw new NoSuchAttributeException("provide config file");
		CommitLogger cl = new CommitLogger();
		cl.readConfig(args[0]);
		cl.start();
	}
}
