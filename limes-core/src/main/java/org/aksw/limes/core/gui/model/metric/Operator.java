package org.aksw.limes.core.gui.model.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Operator for metric expressions allows to combine metric values
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class Operator extends Node {
    private static double defaultThreshold = 0.7;
    /**
     * operators
     */
    public static final Set<String> identifiers = Collections
            .unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{
                    "add", "and", "diff", "max", "min", "minus", "mult", "or",
                    "xor"})));
    /**
     * unmodifiable HashSet of validChildClasses
     */
    @SuppressWarnings("unchecked")
    static public final Set<Class<? extends Node>> validChildClasses = Collections
            .unmodifiableSet(new HashSet<Class<? extends Node>>(Arrays
                    .asList((Class<? extends Node>[]) new Class[]{
                            Measure.class, Operator.class})));

    /**
     * Constructor
     *
     * @param id identification string
     */
    public Operator(String id) {
        super(id);
        this.param1 = defaultThreshold;
        this.param2 = defaultThreshold;
    }

    /**
     * returns the identifiers
     */
    @Override
    public Set<String> identifiers() {
        return identifiers;
    }

    /**
     * returns max number of childs
     */
    @Override
    public byte getMaxChilds() {
        return 2;
    }

    /**
     * returns validChildClasses
     *
     * @return validChildClasses
     */
    @Override
    public Set<Class<? extends Node>> validChildClasses() {
        return validChildClasses;
    }
}