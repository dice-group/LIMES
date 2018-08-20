package org.aksw.limes.core.gui.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.aksw.limes.core.gui.view.TaskProgressView;

import javafx.concurrent.Task;

/**
 * Class to show user progress if tasks are being executed in the background
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class TaskProgressController {

	/**
	 * holds the tasks
	 */
	private static final ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * corresponding view
	 */
	private final TaskProgressView view;

	/**
	 * set with the tasks to be executed
	 */
	private final Set<Task<?>> tasks;

	/**
	 * constructor
	 * 
	 * @param view
	 *            corresponding view
	 * @param tasks
	 *            tasks to execute
	 */
	public TaskProgressController(TaskProgressView view, Task<?>... tasks) {
		view.setController(this);
		this.view = view;
		this.tasks = new HashSet<>();
	}

	/**
	 * returns executorService
	 * 
	 * @return executorService
	 */
	public static ExecutorService getExecutorservice() {
		return executorService;
	}

	/**
	 * adds a new task with the desired callbacks on either success or error
	 * 
	 * @param task
	 *            task to be executed
	 * @param successCallback
	 *            called on success
	 * @param errorCallback
	 *            called on failure
	 * @param <T>
	 *            throwable
	 */
	public <T> void addTask(Task<T> task, Consumer<T> successCallback, Consumer<Throwable> errorCallback) {
		this.tasks.add(task);
		task.setOnSucceeded(event -> {
			T result;
			try {
				result = task.get();
			} catch (final Exception e) {
				return;
			}
			successCallback.accept(result);

			this.tasks.remove(task);
			if (this.tasks.isEmpty()) {
				this.view.setFinishedSuccessfully(true);
				this.view.close();
			}
		});

		task.setOnFailed(event -> {
			this.view.setFinishedSuccessfully(false);
			this.cancel();
			errorCallback.accept(task.getException());
		});
		executorService.submit(task);
	}

	/**
	 * cancels all tasks
	 */
	public void cancel() {
		this.view.setCancelled(true);
		this.view.setFinishedSuccessfully(false);
		for (final Task<?> task : this.tasks) {
			task.cancel();
		}
		this.view.close();
	}
}
