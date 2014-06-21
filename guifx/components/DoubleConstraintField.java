package guifx.components;

import guifx.utils.Constraint;
import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.controlsfx.dialog.Dialogs;

public class DoubleConstraintField extends ConstraintForm {
	public static final double	ERROR_RETURN	= Double.POSITIVE_INFINITY;
	private final TextField		field;

	public DoubleConstraintField(StringProperty name) {
		this(10,name,new Constraint[] { });
	}
	
	public DoubleConstraintField(Constraint... constraints) {
		this(5,constraints);
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

	public void setValue(double d) {
        constraints.stream().forEach(predicate -> {
			if (!predicate.test(d))
				throw new ConstraintsException(predicate.errorMessage());
		});
        field.setText(d + "");
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
			field.requestFocus();
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("numberFormatException")).
				showError();
			return ERROR_RETURN;
		} catch (ConstraintsException ce) {
			field.requestFocus();
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(ce.getMessage()).
				showError();
			return ERROR_RETURN;
		}
	}
	
	public void setOnAction(EventHandler<ActionEvent> handler) {
		field.setOnAction(handler);
	}
	
	public void bindOnActionProperty(ObservableValue< ? extends EventHandler<ActionEvent>> ov) {
		field.onActionProperty().bind(ov);
	}
}
