package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 * @author D. Wiell
 *
 */
public interface ProgressListener {

	void progressMade(Progress progress);
	
	static ProgressListener NULL_PROGRESS_LISTENER = new ProgressListener() {
		public void progressMade(Progress progress) {}
	};
	
}


