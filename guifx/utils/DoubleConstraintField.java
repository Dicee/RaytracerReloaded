package guifx.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class DoubleConstraintField extends HBox {
	private final TextField field;
	private final List<Predicate<Double>> constraints;
	
	public DoubleConstraintField(StringProperty name) {
		this(name,new Predicate[] { });
	}
	
	public DoubleConstraintField(StringProperty name, Predicate<Double>... constraints) {
		this(constraints);
		
		Label nameLabel = new Label();
		nameLabel.textProperty().bind(name);
		getChildren().addAll(nameLabel,field);
	}
	
	public DoubleConstraintField(Predicate<Double>... constraints) {
		super(10);
		this.constraints = Arrays.asList(constraints);
		this.field = new TextField();
		this.field.setPrefColumnCount(5);
	}
	
	public void addConstraint(Predicate<Double> constraint) {
		if (constraint == null)
			throw new NullPointerException();
		constraints.add(constraint);
	}
	
	public double getValue() {
		double d = Double.parseDouble(field.getText());
		
		constraints.stream().forEach(predicate -> {
			if (!predicate.test(d))
				throw new ConstraintsException();
		});
		return d;
	}
}
