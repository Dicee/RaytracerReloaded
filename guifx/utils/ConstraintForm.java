package guifx.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.layout.HBox;

public abstract class ConstraintForm extends HBox {
	protected final List<Constraint<Double>> constraints;
	
	public ConstraintForm(int hgap, Constraint<Double>... constraints) {
		super(hgap);
		this.constraints = new ArrayList<>(Arrays.asList(constraints));
	}
	
	public ConstraintForm(Constraint<Double>... constraints) {
		super();
		this.constraints = new ArrayList<>(Arrays.asList(constraints));
	}
	
	public void addConstraint(Constraint<Double> constraint) {
		if (constraint == null)
			throw new NullPointerException();
		constraints.add(constraint);
	}
	
	public void clearConstraints() {
		constraints.clear();
	}
}
