package org.aksw.limes.core.gui.view;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;

/**
 * Interface to Import different Self Configuration Panels to the
 * SelfConfiguration View
 * 
 * @author Sascha Hahne
 *
 */
public class SelfConfigurationPanelInterface {
	/**
	 * View of Panel
	 */
	public SelfConfigurationView selfConfigurationView;
	/**
	 * Button to start Learning
	 */
	public Button learnButton;

	/**
	 * Button to show Mapping
	 */
	public Button mapButton;

	/**
	 * Shows if Process is runnning or Limes is dead
	 */
	public ProgressIndicator progressIndicator;

	/**
	 * UserInterfaceparams Parameters of the Form for the Learning
	 */
	double[] UIparams;

	/**
	 * Constructor
	 * 
	 * @param view
	 *            Corresponding View
	 */
	public SelfConfigurationPanelInterface(SelfConfigurationView view) {
		this.selfConfigurationView = view;
	};

	/**
	 * Getter UIparams
	 * 
	 * @return UserInterfaceparams of the Form
	 */
	public double[] getUIParams() {

		return this.UIparams;
	}

}
