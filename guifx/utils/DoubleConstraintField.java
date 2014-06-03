package guifx.utils;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.dialog.Dialogs;

public class DoubleConstraintField extends ConstraintForm {
	public static final double errorReturn = Double.POSITIVE_INFINITY;
	
	private final TextField field;
	
	public DoubleConstraintField(StringProperty name) {
		this(10,name,new Constraint[] { });
	}
	
	public DoubleConstraintField(int hgap, StringProperty name, Constraint<Double>... constraints) {
		this(hgap,constraints);
		
		Label nameLabel = new Label();
		nameLabel.textProperty().bind(name);
		nameLabel.setFont(GraphicFactory.subtitlesFont);
		getChildren().add(0,nameLabel);
	}
	
	private DoubleConstraintField(int hgap, Constraint... constraints) {
		super(hgap,constraints);
		this.field = new TextField();
		this.field.setPrefColumnCount(5);
		getChildren().add(field);
	}
	
	public DoubleConstraintField(Constraint... constraints) {
		super(constraints);
		this.field = new TextField();
		this.field.setPrefColumnCount(5);
		getChildren().add(field);
	}
	
	public double getValue() {
		try {
			double d = Double.parseDouble(field.getText());
			constraints.stream().forEach(predicate -> {
				if (!predicate.test(d))
					throw new ConstraintsException(predicate.errorMessage());
			});
			return d;
		} catch (NumberFormatException nfe) {
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("numberFormatException")).
				showError();
			return errorReturn;
		} catch (ConstraintsException ce) {
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(ce.getMessage()).
				showError();
			return errorReturn;
		}
	}
}
