package guifx.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.layout.HBox;

public abstract class ConstraintForm extends HBox {
	protected final List<Predicate<Double>> constraints;
	
	public ConstraintForm(int hgap, Predicate<Double>... constraints) {
		super(hgap);
		this.constraints = Arrays.asList(constraints);
	}
	
	public void addConstraint(Predicate<Double> constraint) {
		if (constraint == null)
			throw new NullPointerException();
		constraints.add(constraint);
	}
}
