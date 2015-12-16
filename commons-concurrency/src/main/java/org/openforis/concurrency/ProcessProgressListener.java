package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 *
 */
public class ProcessProgressListener {
	private int totalSteps = 0;
	private int currentStep = 0;
	private Progress[] progressPerStep;
	
	public ProcessProgressListener(int totalSteps) {
		this.totalSteps = totalSteps;
		this.progressPerStep = new Progress[totalSteps];
	}
	
	public void stepProgressMade(Progress stepProgress) {
		progressPerStep[currentStep] = stepProgress;
	}
	
	public void stepCompleted() {
		currentStep ++;
	}
	
	private long sum(Long... values) {
		long total = 0;
		for (Long value : values) {
			if (value != null) {
				total += value;
			}
		}
		return total;
	}
	
	public Progress getProgress() {
		Long[] stepProgressPercents = new Long[totalSteps];
		for (int i = 0; i <= currentStep; i++) {
			Progress stepProgress = progressPerStep[i];
			if (stepProgress != null) {
				stepProgressPercents[i] = Long.valueOf(stepProgress.getCompletionPercent());
			}
		}
		return new Progress(sum(stepProgressPercents), totalSteps * 100);
	}
	
}