package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 * @author D. Wiell
 *
 */
public interface ProgressListener {

	void progressMade();
	
	static ProgressListener NULL_PROGRESS_LISTENER = new ProgressListener() {
		public void progressMade() {}
	};
	
}


