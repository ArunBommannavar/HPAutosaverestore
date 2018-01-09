package edu.ciw.gl.hpcat.saverestore;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SaveRestoreBackup {
	  private final ScheduledExecutorService scheduler =
			   Executors.newSingleThreadScheduledExecutor();
			   protected int timeInterval = 7200; // Two hours in seconds
			SaveFiles sf = new SaveFiles();

			public void startAll(){
				  scheduler.scheduleAtFixedRate (sf, 0, timeInterval, TimeUnit.SECONDS);
			}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SaveRestoreBackup srb = new SaveRestoreBackup();
		srb.startAll();
	}
}
