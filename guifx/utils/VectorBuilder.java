package guifx.utils;

import static guifx.MainUI.strings;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.dialog.Dialogs;
import utils.math.Vector3D;

public class VectorBuilder extends ConstraintForm {
	private final TextField xField, yField, zField;
	
	public VectorBuilder() {
		this(new Predicate[] { });
	}
	
	public VectorBuilder(Predicate<Double>... constraints) {
		super(5,constraints);
		this.xField      = new TextField();
		this.yField      = new TextField();
		this.zField      = new TextField();
		this.xField.setPrefColumnCount(5);
		this.yField.setPrefColumnCount(5);
		this.zField.setPrefColumnCount(5);
		getChildren().addAll(new Label("X :"),xField,new Label("Y :"),yField,new Label("Z :"),zField);
	}	

	public Vector3D getVector() {
		try {
			double x = Double.parseDouble(xField.getText());
			double y = Double.parseDouble(yField.getText());
			double z = Double.parseDouble(zField.getText());
		
			List<Double> coords = Arrays.asList(x,y,z);
			constraints.stream().forEach(predicate -> {
				if (!coords.stream().allMatch(predicate))
					throw new ConstraintsException();
			});
			return new Vector3D(x,y,z);
		} catch (NumberFormatException nfe) {
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("numberFormatException")).
				showError();
			return null;
		} catch (ConstraintsException ce) {
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("constraintsError")).
				showError();
			return null;
		}
	}
}
