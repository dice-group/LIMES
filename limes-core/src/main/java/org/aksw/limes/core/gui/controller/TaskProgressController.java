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
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class TaskProgressController {
    
    /**
     * holds the tasks
     */
    private static final ExecutorService executorService = Executors
            .newCachedThreadPool();

    /**
     * corresponding view
     */
    private TaskProgressView view;
    
    /**
     * set with the tasks to be executed
     */
    private Set<Task<?>> tasks;

    /**
     * constructor
     * @param view corresponding view
     * @param tasks tasks to execute
     */
    public TaskProgressController(TaskProgressView view,
                                  Task<?>... tasks) {
        view.setController(this);
        this.view = view;
        this.tasks = new HashSet<Task<?>>();
    }

    /**
     * returns executorService
     * @return executorService
     */
    public static ExecutorService getExecutorservice() {
        return executorService;
    }

    /**
     * adds a new task with the desired callbacks on either success or error
     * @param task task to be executed
     * @param successCallback called on success
     * @param errorCallback called on failure
     * @param <T> throwable
     */
    public <T> void addTask(Task<T> task, Consumer<T> successCallback,
                            Consumer<Throwable> errorCallback) {
	tasks.add(task);
        task.setOnSucceeded(event -> {
            T result;
            try {
                result = task.get();
            } catch (Exception e) {
                return;
            }
            successCallback.accept(result);

            tasks.remove(task);
            if (tasks.isEmpty()) {
        	view.setFinishedSuccessfully(true);
                view.close();
            }
        });

        task.setOnFailed(event -> {
            view.setFinishedSuccessfully(false);
            cancel();
            errorCallback.accept(task.getException());
        });
        executorService.submit(task);
    }

    /**
     * cancels all tasks
     */
    public void cancel() {
	view.setCancelled(true);
        view.setFinishedSuccessfully(false);
        for (Task<?> task : tasks) {
            task.cancel();
        }
        view.close();
    }
}
