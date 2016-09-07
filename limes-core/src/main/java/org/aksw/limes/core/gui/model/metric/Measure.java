package org.aksw.limes.core.gui.model.metric;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.gui.model.metric.Property.Origin;
import org.aksw.limes.core.measures.measure.MeasureType;

/**
 * data model class to graphically represent measures
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class Measure extends Node {

    /**
     * Set of valid Child-Classes
     */
    static public final Set<Class<? extends Node>> validChildClasses = Collections
            .<Class<? extends Node>>singleton(Property.class);

    /**
     * Constructor
     *
     * @param id
     *         Id of the Node
     */
    public Measure(String id) {
        super(id);
    }

    /**
     * Returns the Measurement identifiers
     *
     * @return measurement identifiers
     */
    @Override
    public Set<String> identifiers() {
	Set<String> identifiers = new HashSet<String>();
	for(int i = 0; i < MeasureType.values().length; i++){
	    identifiers.add(MeasureType.values()[i].toString().toLowerCase());
	}
        return identifiers;
    }

    /**
     * Returns the Number of maximal Children of the Node
     *
     * @return Number of Children
     */
    @Override
    public byte getMaxChilds() {
        return 2;
    }

    /**
     * Returns the valid child-classes
     *
     * @return valid child classes
     */
    @Override
    public Set<Class<? extends Node>> validChildClasses() {
        return validChildClasses;
    }

    /**
     * Returns if node accepts child
     *
     * @return true if Node accepts Child
     */
    @Override
    public boolean acceptsChild(Node node) {
        synchronized (this) {
            synchronized (node) {
                return super.acceptsChild(node)
                        && (getChilds().isEmpty() || ((Property) getChilds()
                        .iterator().next()).origin != ((Property) node).origin);
            }
        }
    }

    /**
     * Returns the Acceptance of linkedChild
     *
     * @return Acceptance of linked Child
     */
    @Override
    public Acceptance acceptsChildWithReason(Node node) {
        synchronized (this) {
            synchronized (node) {
                Acceptance acceptance = super.acceptsChildWithReason(node);
                if (acceptance != Acceptance.OK) {
                    return acceptance;
                }
                if (!acceptsChild(node)) {
                    if ((((Property) getChilds().iterator().next()).origin == Origin.SOURCE)) {
                        return Acceptance.TARGET_PROPERTY_EXPECTED;
                    }
                    return Acceptance.SOURCE_PROPERTY_EXPECTED;
                }
                return Acceptance.OK;
            }
        }
    }
}
