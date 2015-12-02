package org.aksw.limes.core.ml.algorithm.eagle.core;

import java.math.BigDecimal;

import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.ProgramChromosome;

public class Metric {
	private String expression;
	private double threshold;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		BigDecimal bd = new BigDecimal(threshold).setScale(4, BigDecimal.ROUND_HALF_EVEN);
		double d = bd.doubleValue();
		this.threshold = d;
	}

	public Metric(String expression, double threshold) {
		setExpression(expression);
		setThreshold(threshold);
	}

	@Override
	public boolean equals(Object o) {
	if (!o.getClass().getCanonicalName()
				.equals(this.getClass().getCanonicalName())) {
			return false;
		}
		else {
			if (((Metric) o).expression.equalsIgnoreCase(expression)
					&& ((Metric) o).threshold == threshold) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is this metic a valid LIMES link specification.
	 * @return true if so, false otherwise.
	 */
	public boolean isValid() {
		if(expression.indexOf("falseProp")==-1 && expression.length()>0)
			return true;
		return false;
	}
	
	public String toString() {
		BigDecimal bd =	new BigDecimal( threshold );
		bd = bd.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		return expression + ">=" + bd;
	}
	@Override
	public int hashCode() {
		return expression.hashCode()+(int)(10*threshold);
		
	}

	public static void main(String args[]) {
		String expr = "AND(AND(cosine(x.authors,y.authors)|0.7057014747423743,falseProp)|0.6496907940555052,euclidean(x.year,y.year)|0.80141795589077)|0.6496907940555052 >= 0.39257398117644515";
		Metric m = new Metric(expr, 1d);
		System.out.println(m.isValid());
	}
	
	public static Metric getMetric(IGPProgram p) {
		Object[] args = {};
		ProgramChromosome pc = p.getChromosome(0);
		return (Metric) pc.getNode(0).execute_object(pc, 0, args);
	}
	
}
