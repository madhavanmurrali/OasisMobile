package com.oasishome.server;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.google.appengine.api.ThreadManager;

public class OasisUpdater {
	private final static ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	private static ScheduledFuture<?> updatehandler = null;

	public static void startUpdater() {
		
		
		
		final Runnable updateThread = new Runnable() {
			public void run() {
				System.out.println("calling read apis");
				SyncService.syncDevices();
				// call the apis // udpate
			}
		};
		
		Thread udpateThreadThroAppEngine =  ThreadManager.createThreadForCurrentRequest(updateThread);

		updatehandler = scheduler.scheduleAtFixedRate(udpateThreadThroAppEngine, 10, 600,
				SECONDS);

	}

	// destroy timer code ..
	public static void stopUpdater() {
		System.out.println("stoppping");
		updatehandler.cancel(true);
	}
}
