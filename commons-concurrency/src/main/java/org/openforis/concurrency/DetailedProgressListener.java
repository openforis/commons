package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 *
 */
public interface DetailedProgressListener extends ProgressListener {

	void progressMade(Progress progress);
	
	public class Progress {
		
		private int completionPercent;
		private Long elapsedTime;
		private Long remainingTime;
		
		public Progress(int completionPercent) {
			this(completionPercent, null, null);
		}

		public Progress(int completionPercent, Long elapsedTime, Long remainingTime) {
			super();
			this.completionPercent = completionPercent;
			this.elapsedTime = elapsedTime;
			this.remainingTime = remainingTime;
		}

		public int getCompletionPercent() {
			return completionPercent;
		}
		
		public Long getElapsedTime() {
			return elapsedTime;
		}
		
		public Long getRemainingTime() {
			return remainingTime;
		}
	}
	
}
