package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 *
 */
public class ProcessStepProgressListener implements ProgressListener {
	
	private ProcessProgressListener processProgressListener;
	private ProgressListener outerProgressListener;

	public ProcessStepProgressListener(ProcessProgressListener totalProgressListener,
			ProgressListener outerProgressListener) {
		super();
		this.processProgressListener = totalProgressListener;
		this.outerProgressListener = outerProgressListener;
	}

	public void progressMade() {}
	
	public void progressMade(Progress stepProgress) {
		processProgressListener.stepProgressMade(stepProgress);
		outerProgressListener.progressMade(processProgressListener.getProgress());
	}
}