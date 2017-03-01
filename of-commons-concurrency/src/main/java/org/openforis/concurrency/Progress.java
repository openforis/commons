package org.openforis.concurrency;

public class Progress {
	
	private long processedItems;
	private long totalItems;
	private Long elapsedTime;
	private Long remainingTime;
	
	public Progress(long processedItems, long totalItems) {
		this(processedItems, totalItems, null, null);
	}

	public Progress(long processedItems, long totalItems, Long elapsedTime, Long remainingTime) {
		super();
		this.processedItems = processedItems;
		this.totalItems = totalItems;
		this.elapsedTime = elapsedTime;
		this.remainingTime = remainingTime;
	}

	public int getCompletionPercent() {
		return totalItems <= 0 ? -1 : Double.valueOf(Math.floor(((double) processedItems / totalItems) * 100)).intValue();
	}
	
	public long getProcessedItems() {
		return processedItems;
	}
	
	public void setProcessedItems(long processedItems) {
		this.processedItems = processedItems;
	}
	
	public long getTotalItems() {
		return totalItems;
	}
	
	public void setTotalItems(long totalItems) {
		this.totalItems = totalItems;
	}
	
	public Long getElapsedTime() {
		return elapsedTime;
	}
	
	public Long getRemainingTime() {
		return remainingTime;
	}
}