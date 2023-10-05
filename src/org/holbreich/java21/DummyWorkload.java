package org.holbreich.java21;

public class DummyWorkload implements Runnable{

	private final String message;
	
	public DummyWorkload(String message) {
		super();
		this.message = message;
	}
	
	public DummyWorkload() {
		this(null);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			System.out.println(e);
		}
		if(message!=null) {
			System.out.print(message);
		}
			
	}

}
