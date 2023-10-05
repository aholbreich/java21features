package org.holbreich.java21;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadsExamles {

	final static AtomicInteger atomicInteger = new AtomicInteger();

	public static final void main(String[] args) throws InterruptedException {
		newTheadAPIExamples();
		runVirtualThreads(10_000); 	  //Around  130 ms on Intel Core i5-13500 and 32 GB ram
		runVirtualThreads(100_000);   //Around  181 ms 
		runVirtualThreads(2_000_000); //Around 3146 ms getting into several millions becomes too much overhead. 
		
		runClassicThreads(10_000);    // 10019 ms Classing OS threds to see the difference
	}

	private static void newTheadAPIExamples() {
		Thread thread = Thread.ofVirtual().name("constructed").unstarted(new DummyWorkload("New virtual thread constructor"));
		System.out.println("is virtual: " + thread.isVirtual()); // yes
		System.out.println("is deamon: " + thread.isDaemon()); // yes
		try {
			thread.setPriority(-1);
		} catch (IllegalArgumentException e) {
			System.out.println("Virtual thread don't support prio change: " + e.getMessage());
		}
		
		Thread.startVirtualThread(new DummyWorkload("Easy start"));
		
		Runnable runnable = () -> System.out.println("Some work");
		Thread.ofVirtual().start(runnable);  // in contrast platform threads are created via Thread.ofPlatform()
		
	}
	
	private static void runVirtualThreads(int numberOfThreads) throws InterruptedException {
		System.out.println("Test run with "+numberOfThreads+"virtual threads");
		Instant start = Instant.now();

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			for (int i = 0; i < numberOfThreads; i++) {
				executor.submit(new DummyWorkload());
			}
		}

		Instant finish = Instant.now();
		System.out.println("Total elapsed with "+numberOfThreads+" virtual threads : " + Duration.between(start, finish).toMillis());
	}

	private static void runClassicThreads(int numberOfThreads) throws InterruptedException {
		System.out.println("Test run with"+numberOfThreads+"classic threads");

		Instant start = Instant.now();

		var executor = Executors.newFixedThreadPool(100);
		for (int i = 0; i < numberOfThreads; i++) {
			executor.submit(new DummyWorkload());
		}
		// Pre Java 21 (in fact 19) style executers closing handling
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);

		Instant finish = Instant.now();
		System.out.println("Total elapsed with "+numberOfThreads+"  classic threads : " + Duration.between(start, finish).toMillis());
	}



}
