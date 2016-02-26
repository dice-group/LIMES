package org.aksw.limes.core.gui.controller;

import org.aksw.limes.core.gui.view.WizardView;

/**
 * Controller class for wizards
 * 
 * @author Manuel
 */
public class WizardController {
	private Runnable finishCallback;
	private Runnable cancelCallback;
	private WizardView view;
	private IEditController[] editControllers;
	private int currentPage = -1;

	public WizardController(Runnable finishCallback, Runnable cancelCallback,
			WizardView view, IEditController... editControllers) {
		this.finishCallback = finishCallback;
		this.cancelCallback = cancelCallback;
		view.setController(this);
		this.view = view;
		assert editControllers.length != 0;
		this.editControllers = editControllers;
		setPage(0);
	}

	private void setPage(int newPage) {
		if (newPage < 0 || newPage > editControllers.length) {
			return;
		}
		if (this.currentPage != -1) {
			editControllers[this.currentPage].save();
		}
		if (newPage == editControllers.length) {
			this.currentPage = -1;
			view.close();
			finish();
		} else {
			this.currentPage = newPage;
			IEditController editController = editControllers[newPage];
			editController.load();
			view.setEditView(editController.getView(), newPage != 0,
					newPage < editControllers.length - 1);
		}
	}

	public void back() {
		setPage(currentPage - 1);
	}

	public void nextOrFinish() {
		setPage(currentPage + 1);
	}

	private void finish() {
		finishCallback.run();
	}

	public void cancel() {
		cancelCallback.run();
	}
}
