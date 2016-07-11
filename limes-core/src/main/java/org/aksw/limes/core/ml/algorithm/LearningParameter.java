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

    
    
    /**
     * @param name
     * @param value
     * @param clazz
     * @param rangeStart
     * @param rangeEnd
     * @param rangeStep
     * @param description
     */
    public LearningParameter(String name, Object value, Class<?> clazz, double rangeStart, double rangeEnd,
            double rangeStep, String description) {
        super();
        this.name = name;
        this.value = value;
        this.clazz = clazz;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.rangeStep = rangeStep;
        this.description = description;
    }

    /**
     * 
     */
    public LearningParameter(){
        super();
    }
    
    /**
     * @return
     */
    public double getRangeStep() {
        return rangeStep;
    }

    /**
     * @param rangeStep
     */
    public void setRangeStep(double rangeStep) {
        this.rangeStep = rangeStep;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * @return
     */
    public Class<?> getClazz() {
        return clazz;
    }
    
    /**
     * @param clazz
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    /**
     * @return
     */
    public double getRangeStart() {
        return rangeStart;
    }
    
    /**
     * @param rangeStart
     */
    public void setRangeStart(double rangeStart) {
        this.rangeStart = rangeStart;
    }
    
    /**
     * @return
     */
    public double getRangeEnd() {
        return rangeEnd;
    }
    
    /**
     * @param rangeEnd
     */
    public void setRangeEnd(double rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
    
    /**
     * @return
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
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
