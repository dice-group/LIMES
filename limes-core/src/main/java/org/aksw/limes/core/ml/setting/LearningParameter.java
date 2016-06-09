package org.aksw.limes.core.ml.setting;


public class LearningParameter {

    protected String name;
    protected Object value;
    protected Class<?> clazz;
    protected double rangeStart;
    protected double rangeEnd;
    protected double rangeStep;
    protected String description;

    
    
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

    public LearningParameter(){
        super();
    }
    
    public double getRangeStep() {
        return rangeStep;
    }

    public void setRangeStep(double rangeStep) {
        this.rangeStep = rangeStep;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }
    
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public double getRangeStart() {
        return rangeStart;
    }
    
    public void setRangeStart(double rangeStart) {
        this.rangeStart = rangeStart;
    }
    
    public double getRangeEnd() {
        return rangeEnd;
    }
    
    public void setRangeEnd(double rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    

}
