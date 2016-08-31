package org.aksw.limes.core.gui.controller;

import org.aksw.limes.core.gui.view.WizardView;

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

    /**
     * Constructor
     * @param finishCallback called on finish
     * @param cancelCallback called on cancellation
     * @param view corresponding view
     * @param editControllers controllers for steps
     */
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

    /**
     * sets a new page checks if it is valid and loads the appropriate controller
     * @param newPage
     */
    private void setPage(int newPage) {
        if (newPage < 0 || newPage > editControllers.length) {
            return;
        }
        if (this.currentPage != -1) {
            if(editControllers[this.currentPage].validate()){
        	editControllers[this.currentPage].save();
            }else{
        	return;
            }
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
