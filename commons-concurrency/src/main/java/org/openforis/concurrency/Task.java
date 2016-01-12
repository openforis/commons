package org.openforis.concurrency;


/**
 * A unit of work in the system.
 * 
 * Tasks are not reusable.
 * 
 * @author M. Togna
 * @author S. Ricci
 */
public abstract class Task extends Worker {

	private long totalItems;
	private long processedItems;
	private long skippedItems;

	public Task() {
		this.totalItems = -1;
		this.processedItems = 0;
		this.skippedItems = 0;
	}
	
	@Override
	protected void beforeExecuteInternal() throws Throwable {
		super.beforeExecuteInternal();
		this.totalItems = countTotalItems();
	}
	
	protected long countTotalItems() {
		return -1;
	};

	protected void setProcessedItems(long processedItems) {
		this.processedItems = processedItems;
	}

	public void setTotalItems(long totalItems) {
		this.totalItems = totalItems;
	}
	
	protected long incrementProcessedItems() {
		return ++this.processedItems;
	}

	protected long incrementSkippedItems() {
		return ++this.skippedItems;
	}

	public long getRemainingItems() {
		return totalItems - (processedItems + skippedItems);
	}

	@Override
	public int getProgressPercent() {
		switch ( getStatus() ) {
		case COMPLETED:
			return 100;
		case PENDING:
			return 0;
		default:
			if ( totalItems > 0 ) {
				int result = Double.valueOf(Math.ceil( (double) ( ( processedItems + skippedItems ) * 100d / totalItems ) ) ).intValue();
				return result;
			} else {
				return 0;
			}
		}
	}
	
	public long getProcessedItems() {
		return this.processedItems;
	}

	public long getSkippedItems() {
		return this.skippedItems;
	}

	public long getTotalItems() {
		return this.totalItems;
	}

}
