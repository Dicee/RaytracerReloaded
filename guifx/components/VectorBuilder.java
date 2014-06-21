package guifx.components;

import guifx.utils.Constraint;
import static guifx.MainUI.strings;
import java.util.Arrays;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.dialog.Dialogs;
import utils.math.Point;
import utils.math.Vector3D;

public class VectorBuilder extends ConstraintForm {
	public static Object	ERROR_RETURN	= null;
	private final TextField	xField, yField, zField;
	
	public VectorBuilder() {
		this(new Constraint[] { });
	}
	
	public VectorBuilder(Constraint<Double>... constraints) {
		super(5,constraints);
		this.xField      = new TextField();
		this.yField      = new TextField();
		this.zField      = new TextField();
		this.xField.setPrefColumnCount(5);
		this.yField.setPrefColumnCount(5);
		this.zField.setPrefColumnCount(5);
		getChildren().addAll(new Label("X :"),xField,new Label("Y :"),yField,new Label("Z :"),zField);
	}	

	private double[] getValues() {
		TextField errorField = null;
		try {
			final double[] result = new double[3];
			int            i      = -1;
			
			List<TextField> fields = Arrays.asList(xField,yField,zField);
			for (TextField field : fields) {
				result[++i] = Double.parseDouble((errorField = field).getText());
				for (Constraint predicate : constraints)
					if (!predicate.test(result[i]))
						throw new ConstraintsException(predicate.errorMessage());
			}
			return result;
		} catch (NumberFormatException nfe) {
			errorField.requestFocus();
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("numberFormatException")).
				showError();
			return null;
		} catch (ConstraintsException ce) {
			errorField.requestFocus();
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(ce.getMessage()).
				showError();
			return null;
		}
	}
	
	public Vector3D getVector() {
		double[] values = getValues();
		if (values == null)
			return null;
		else 
			return new Vector3D(values[0],values[1],values[2]);
	}
	
	public Point getPoint() {
		double[] values = getValues();
		if (values == null)
			return null;
		else 
			return new Point(values[0],values[1],values[2]);
	}
	
	public void setOnAction(EventHandler<ActionEvent> handler) {
		xField.setOnAction(handler);
		yField.setOnAction(handler);
		zField.setOnAction(handler);
	}
	
	public void bindOnActionProperty(ObservableValue< ? extends EventHandler<ActionEvent>> ov) {
		xField.onActionProperty().bind(ov);
		yField.onActionProperty().bind(ov);
		zField.onActionProperty().bind(ov);
	}
}
