package org.aksw.limes.core.gui.controller;

import org.aksw.limes.core.gui.view.EditClassMatchingView;
import org.aksw.limes.core.gui.view.EditEndpointsView;
import org.aksw.limes.core.gui.view.EditPropertyMatchingView;
import org.aksw.limes.core.gui.view.WizardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
    private Runnable finishCallback;
    /**
     * gets run if cancel is pressed
     */
    private Runnable cancelCallback;
    /**
     * corresponding view
     */
    private WizardView view;
    /**
     * controllers for steps
     */
    private IEditController[] editControllers;
    /**
     * current page
     */
    private int currentPage = -1;
    
    Logger logger = LoggerFactory.getLogger(getClass());

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
	setPage(0);
    }

    /**
     * sets a new page checks if it is valid and loads the appropriate
     * controller
     * 
     * @param newPage
     */
    private void setPage(int newPage){
	//This should not happen
	if (newPage < 0 || newPage > editControllers.length) {
	    return;
	}
	//If we go to the next page, validate and save
	if (this.currentPage != -1 && newPage > this.currentPage) {
	    if (editControllers[this.currentPage].validate()) {
		editControllers[this.currentPage].save();
	    } else {
		//if it's not valid return, error will be displayed
		return;
	    }
	}
	//After last page save and close
	if (newPage == editControllers.length) {
	    this.currentPage = -1;
	    view.close();
	    finish();
	} else {
	    this.currentPage = newPage;
	    IEditController editController = editControllers[newPage];
	    editController.load();
	    //if loading screen is showing we have to checked whether the loading was successful or cancelled
	    if (editControllers[currentPage].getTaskProgressView() != null) {

		// if finished successfully
		editControllers[currentPage].getTaskProgressView().getFinishedSuccessfully()
			.addListener(new ChangeListener<Boolean>() {

			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable,
				    Boolean oldValue, Boolean newValue) {
				if (newValue) {
				    view.setEditView(editController.getView(), newPage != 0,
					    newPage < editControllers.length - 1);
				    editControllers[currentPage].setTaskProgressView(null);
				}
			    }
			});

		// if cancelled
		editControllers[currentPage].getTaskProgressView().getCancelled()
			.addListener(new ChangeListener<Boolean>() {

			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable,
				    Boolean oldValue, Boolean newValue) {
				if (newValue) {
				    currentPage--;
				    editControllers[currentPage].setTaskProgressView(null);
				}
			    }
			});
	    } else {
		view.setEditView(editController.getView(), newPage != 0,
			newPage < editControllers.length - 1);

	    }
	}
    }
    
    /**
     * goes one page back
     */
    public void back() {
	setPage(currentPage - 1);
    }

    /**
     * goes to the next page or finishes
     */
    public void nextOrFinish() {
	setPage(currentPage + 1);
    }

    /**
     * calls the finishCallback
     */
    private void finish() {
	finishCallback.run();
    }

    /**
     * calls the cancelCallback
     */
    public void cancel() {
	cancelCallback.run();
    }
}
