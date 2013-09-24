package Lab1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class simulator {

	private int[] bandwidthHistory = new int[565];
	private int[] requestedQuality = new int[565];

	private void ReadFile() throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(
				"/home/hughe531/TDDD66/report.2011-01-30_1323CET.log"));
		String line = null;
		int index = 0;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			bandwidthHistory[index] = CalculateBandwidth(Integer
					.parseInt(parts[4]), Integer.parseInt(parts[5]));
			index++;
		}
		PrintResults(bandwidthHistory);
	}

	private void PrintResults(int [] a) {
		System.out.println(Arrays.toString(a));
	}

	private int CalculateBandwidth(int bytes, int time) {
		int bandwidth = 0;

		bandwidth = (bytes / time) * 8;

		return bandwidth;

	}

	private void setQuality(videoPlayer player) {
		int previousQuality = -1;
		for (int i = 0; i < bandwidthHistory.length; i++) {
			if (player.checkQuality(bandwidthHistory[i]) > previousQuality){
				requestedQuality[i] = previousQuality + 1;
			}else if (player.checkQuality(bandwidthHistory[i]) < previousQuality - 2) {
				requestedQuality[i] = previousQuality - 2;
			} else {
				requestedQuality[i] = player.checkQuality(bandwidthHistory[i]);
			}
			previousQuality = requestedQuality[i];
			
		}
	}
	
	private void bufferOperation(videoPlayer player){
		
		boolean waitForMinBuf = false;
		
		for (int i = 0; i < requestedQuality.length; i++) {
			int currentSize = player.getCurrentBufSize();
			System.out.println("Current buffer size = " +player.currentBufSize);
			if(currentSize < 4){
				player.setCurrentBufSize(currentSize +4);
				player.setOperation("Pause");
				System.out.println(" Player operation = " + player.getOperation() +" Requested quality = " +requestedQuality[i]);
			}else if (currentSize <= 6 && waitForMinBuf == false){
				player.setCurrentBufSize(currentSize +4);
				player.setOperation("Play");
				System.out.println(" Player operation = " + player.getOperation() +" Requested quality = " +requestedQuality[i]);
				player.setCurrentBufSize(player.currentBufSize - 1);
			}else{
				player.setOperation("Play");
				System.out.println(" Player operation = " + player.getOperation() +" Requested quality = " +requestedQuality[i]);
				waitForMinBuf = true;
				player.setCurrentBufSize(player.currentBufSize - 1);
			}
			
			
			
		}
	}

	public static void main(String[] args) throws IOException {
		simulator sim = new simulator();
		videoPlayer player = new videoPlayer();
		sim.ReadFile();
		sim.setQuality(player);
		sim.PrintResults(sim.requestedQuality);
		sim.bufferOperation(player);

	}

}