package org.openforis.concurrency;

public interface ProgressListener {

	void progressMade(long processedItems, long totalItems);
	
	static ProgressListener NULL_PROGRESS_LISTENER = new ProgressListener() {
		public void progressMade(long processedItems, long totalItems) {}
		
	};
}
