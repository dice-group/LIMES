package org.aksw.limes.core.gui.controller;

import org.aksw.limes.core.gui.view.WizardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;

/**
 * Controller class for {@link WizardView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class WizardController {

	/**
	 * gets run after all steps are completed
	 */
	private final Runnable finishCallback;
	/**
	 * gets run if cancel is pressed
	 */
	private final Runnable cancelCallback;
	/**
	 * corresponding view
	 */
	private final WizardView view;
	/**
	 * controllers for steps
	 */
	private final IEditController[] editControllers;
	/**
	 * current page
	 */
	private int currentPage = -1;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Constructor
	 * 
	 * @param finishCallback
	 *            called on finish
	 * @param cancelCallback
	 *            called on cancellation
	 * @param view
	 *            corresponding view
	 * @param editControllers
	 *            controllers for steps
	 */
	public WizardController(Runnable finishCallback, Runnable cancelCallback, WizardView view,
			IEditController... editControllers) {
		this.finishCallback = finishCallback;
		this.cancelCallback = cancelCallback;
		view.setController(this);
		this.view = view;
		assert editControllers.length != 0;
		this.editControllers = editControllers;
		this.setPage(0);
	}

	/**
	 * sets a new page checks if it is valid and loads the appropriate
	 * controller
	 * 
	 * @param newPage
	 */
	private void setPage(int newPage) {
		// This should not happen
		if (newPage < 0 || newPage > this.editControllers.length) {
			return;
		}
		// If we go to the next page, validate and save
		if (this.currentPage != -1 && newPage > this.currentPage) {
			if (this.editControllers[this.currentPage].validate()) {
				this.editControllers[this.currentPage].save();
			} else {
				// if it's not valid return, error will be displayed
				return;
			}
		}
		// After last page save and close
		if (newPage == this.editControllers.length) {
			this.currentPage = -1;
			this.view.close();
			this.finish();
		} else {
			this.currentPage = newPage;
			final IEditController editController = this.editControllers[newPage];
			editController.load();
			// if loading screen is showing we have to checked whether the
			// loading was successful or cancelled
			if (this.editControllers[this.currentPage].getTaskProgressView() != null) {

				// if finished successfully
				this.editControllers[this.currentPage].getTaskProgressView().getFinishedSuccessfully()
						.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
							if (newValue) {
								WizardController.this.view.setEditView(editController.getView(), newPage != 0,
										newPage < WizardController.this.editControllers.length - 1);
								WizardController.this.editControllers[WizardController.this.currentPage]
										.setTaskProgressView(null);
							}
						});

				// if cancelled
				this.editControllers[this.currentPage].getTaskProgressView().getCancelled()
						.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
							if (newValue) {
								WizardController.this.currentPage--;
								WizardController.this.editControllers[WizardController.this.currentPage]
										.setTaskProgressView(null);
							}
						});
			} else {
				this.view.setEditView(editController.getView(), newPage != 0,
						newPage < this.editControllers.length - 1);

			}
		}
	}

	/**
	 * goes one page back
	 */
	public void back() {
		this.setPage(this.currentPage - 1);
	}

	/**
	 * goes to the next page or finishes
	 */
	public void nextOrFinish() {
		this.setPage(this.currentPage + 1);
	}

	/**
	 * calls the finishCallback
	 */
	private void finish() {
		this.finishCallback.run();
	}

	/**
	 * calls the cancelCallback
	 */
	public void cancel() {
		this.cancelCallback.run();
	}
}
