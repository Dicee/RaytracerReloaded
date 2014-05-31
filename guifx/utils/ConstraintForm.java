package guifx.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class ConstraintForm {
	protected final List<Predicate<Double>> constraints;
	
	public ConstraintForm(Predicate<Double>... constraints) {
		this.constraints = Arrays.asList(constraints);
	}
	
	public void addConstraint(Predicate<Double> constraint) {
		if (constraint == null)
			throw new NullPointerException();
		constraints.add(constraint);
	}
}
