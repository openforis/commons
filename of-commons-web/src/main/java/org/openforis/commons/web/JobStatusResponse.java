package org.openforis.commons.web;


/**
 * 
 * @author S. Ricci
 *
 */
public class JobStatusResponse extends Response {

	private String jobId;
	private org.openforis.concurrency.Worker.Status jobStatus;
	private String jobErrorMessage;
	private int jobProgress;
	
	public String getJobId() {
		return jobId;
	}
	
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	public org.openforis.concurrency.Worker.Status getJobStatus() {
		return jobStatus;
	}
	
	public void setJobStatus(org.openforis.concurrency.Worker.Status jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public String getJobErrorMessage() {
		return jobErrorMessage;
	}
	
	public void setJobErrorMessage(String jobErrorMessage) {
		this.jobErrorMessage = jobErrorMessage;
	}
	
	public int getJobProgress() {
		return jobProgress;
	}
	
	public void setJobProgress(int jobProgress) {
		this.jobProgress = jobProgress;
	}
}
