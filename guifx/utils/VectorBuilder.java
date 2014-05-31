package guifx.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import utils.math.Vector3D;

public class VectorBuilder extends HBox {
	private final TextField xField, yField, zField;
	private final List<Predicate<Double>> constraints;
	
	public VectorBuilder() {
		this(new Predicate[] { });
	}
	
	public VectorBuilder(Predicate<Double>... constraints) {
		super(5);
		this.constraints = Arrays.asList(constraints);
		this.xField      = new TextField();
		this.yField      = new TextField();
		this.zField      = new TextField();
		this.xField.setPrefColumnCount(5);
		this.yField.setPrefColumnCount(5);
		this.zField.setPrefColumnCount(5);
		getChildren().addAll(new Label("X :"),xField,new Label("Y :"),yField,new Label("Z :"),zField);
	}	
	
	public void addConstraint(Predicate<Double> constraint) {
		if (constraint == null)
			throw new NullPointerException();
		constraints.add(constraint);
	}
	
	public Vector3D getVector() throws ConstraintsException {
		double x = Double.parseDouble(xField.getText());
		double y = Double.parseDouble(yField.getText());
		double z = Double.parseDouble(zField.getText());
		
		List<Double> coords = Arrays.asList(x,y,z);
		constraints.stream().forEach(predicate -> {
			if (!coords.stream().allMatch(predicate))
				throw new ConstraintsException();
		});
		return new Vector3D(x,y,z);
	}
}
