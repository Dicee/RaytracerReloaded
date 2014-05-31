package guifx.utils;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.controlsfx.dialog.Dialogs;

public class DoubleConstraintField extends ConstraintForm {
	private final TextField field;
	
	public DoubleConstraintField(StringProperty name) {
		this(10,name,new Predicate[] { });
	}
	
	public DoubleConstraintField(int hgap, StringProperty name, Predicate<Double>... constraints) {
		this(hgap,constraints);
		
		Label nameLabel = new Label();
		nameLabel.textProperty().bind(name);
		nameLabel.setFont(GraphicFactory.subtitlesFont);
		getChildren().addAll(nameLabel,field);
	}
	
	public DoubleConstraintField(int hgap, Predicate<Double>... constraints) {
		super(hgap,constraints);
		this.field = new TextField();
		this.field.setPrefColumnCount(5);
	}
	
	public double getValue() {
		try {
			double d = Double.parseDouble(field.getText());
			constraints.stream().forEach(predicate -> {
				if (!predicate.test(d))
					throw new ConstraintsException();
			});
			return d;
		} catch (NumberFormatException nfe) {
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("numberFormatException")).
				showError();
			return Double.NaN;
		} catch (ConstraintsException ce) {
			Dialogs.create().owner(this).
				title(strings.getProperty("error")).
				masthead(strings.getProperty("anErrorOccurredMessage")).
				message(strings.getProperty("constraintsError")).
				showError();
			return Double.NaN;
		}
	}
}
