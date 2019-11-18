package org.aksw.limes.core.ml.algorithm;


/**
 * @author sherif
 *
 */
public class LearningParameter {

    protected String name;
    protected Object value;
    protected Class<?> clazz;
    protected double rangeStart;
    protected double rangeEnd;
    protected double rangeStep;
    protected String description;
    protected String[] instanceOptions;

    
    /**
     * Constructor
     */
    public LearningParameter(){
        super();
    }
    
    
    /**
     * Constructor
     * 
     * @param name parameter's name
     * @param value parameter's value
     */
    public LearningParameter(String name, Object value) {
        this();
        this.name = name;
        this.value = value;
    }
    
    /**
     * Constructor
     * 
     * @param name parameter's name
     * @param value parameter's value
     * @param clazz parameter's class
     * @param rangeStart parameter's range start
     * @param rangeEnd parameter's range end
     * @param rangeStep parameter's range step
     * @param description parameter's description
     */
    public LearningParameter(String name, Object value, Class<?> clazz, double rangeStart, double rangeEnd,
            double rangeStep, String description) {
        this(name, value);
        this.clazz = clazz;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.rangeStep = rangeStep;
        this.description = description;
    }
    
    
    public LearningParameter(String name, Object value, Class<?> clazz, String[] instanceOptions, String description) {
        this(name, value);
        this.clazz = clazz;
        this.instanceOptions = instanceOptions;
        this.description = description;
    }

	@Override
	public String toString() {
		return new StringBuilder("").append(name).append(" : ").append(value).toString();
	}

    
    
    /**
     * @return parameter's range step
     */
    public double getRangeStep() {
        return rangeStep;
    }

    /**
     * @param rangeStep to be set
     */
    public void setRangeStep(double rangeStep) {
        this.rangeStep = rangeStep;
    }

    /**
     * @return parameter's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name to be set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return parameter's value
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * @param value to be set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * @return parameter's class 
     */
    public Class<?> getClazz() {
        return clazz;
    }
    
    /**
     * @param clazz to be set
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    /**
     * @return parameter's range start as double
     */
    public double getRangeStart() {
        return rangeStart;
    }
    
    /**
     * @param rangeStart to be set
     */
    public void setRangeStart(double rangeStart) {
        this.rangeStart = rangeStart;
    }
    
    /**
     * @return parameter's range end
     */
    public double getRangeEnd() {
        return rangeEnd;
    }
    
    /**
     * @param rangeEnd to be set
     */
    public void setRangeEnd(double rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
    
    /**
     * @return parameter's description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

	public String[] getInstanceOptions() {
		return instanceOptions;
	}


	public void setInstanceOptions(String[] instanceOptions) {
		this.instanceOptions = instanceOptions;
	}


	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rangeEnd);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rangeStart);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rangeStep);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LearningParameter other = (LearningParameter) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (Double.doubleToLongBits(rangeEnd) != Double.doubleToLongBits(other.rangeEnd))
            return false;
        if (Double.doubleToLongBits(rangeStart) != Double.doubleToLongBits(other.rangeStart))
            return false;
        if (Double.doubleToLongBits(rangeStep) != Double.doubleToLongBits(other.rangeStep))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.toString().equals(other.value.toString()))
            return false;
        return true;
    }
    

}
