package org.openforis.concurrency;

public interface ProgressListener {

	void progressMade();
	
	static ProgressListener NULL_PROGRESS_LISTENER = new ProgressListener() {
		@Override
		public void progressMade() {
		}
	};
}
