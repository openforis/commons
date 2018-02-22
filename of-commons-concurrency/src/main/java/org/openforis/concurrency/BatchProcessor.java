package org.openforis.concurrency;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author S. Ricci
 * 
 */
public abstract class BatchProcessor<T> implements Closeable {

	private final int batchSize;
	private final Executor<T> executor;
	
	private final LinkedList<T> queue = new LinkedList<T>();
	
	public BatchProcessor(int batchSize, Executor<T> executor) {
		super();
		this.batchSize = batchSize;
		this.executor = executor;
	}
	
	public void process(Collection<T> items) {
		for (T item : items) {
			process(item);
		}
	}

	public void process(T item) {
		queue.add(item);
		if (queue.size() == batchSize) {
			flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		flush();
	}
	
	private void flush() {
		if (! queue.isEmpty()) {
			executor.execute(queue);
		}
	}
	
	public static interface Executor<T> {
		
		void execute(List<T> items);

	}
}
