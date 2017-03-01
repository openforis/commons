package org.openforis.concurrency;

import org.openforis.concurrency.Worker.Status;

/**
 * 
 * @author S. Ricci
 *
 */
public class WorkerStatusChangeEvent {

	private Worker source;
	private Status from;
	private Status to;
	
	public WorkerStatusChangeEvent(Worker source, Status from, Status to) {
		super();
		this.source = source;
		this.from = from;
		this.to = to;
	}

	public Worker getSource() {
		return source;
	}

	public Status getFrom() {
		return from;
	}

	public Status getTo() {
		return to;
	}

}
