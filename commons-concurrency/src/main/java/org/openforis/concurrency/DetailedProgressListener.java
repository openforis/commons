package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 *
 */
public interface DetailedProgressListener extends ProgressListener {

	void progressMade(Progress progress);
	
}
