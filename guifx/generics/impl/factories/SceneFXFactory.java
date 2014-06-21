package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.utils.Constraints;
import guifx.components.DoubleConstraintField;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import scene.Scene;
import utils.NamedObject;

public class SceneFXFactory extends GraphicFactory<scene.Scene> {
	private static final double				PREFERRED_WIDTH		= 320;
	private static final double				PREFERRED_HEIGHT	= 110;

	private final ColorPicker				colorPicker;
	private final DoubleConstraintField		refractiveIndex;
    
    public SceneFXFactory(Consumer<NamedObject<Scene>> consumer, Scene scene) {
        super(strings.getObservableProperty("editScene"),strings.getObservableProperty("editAction"),consumer,
            PREFERRED_WIDTH,PREFERRED_HEIGHT);
        this.colorPicker     = new ColorPicker(scene.getAmbientColor());
        this.refractiveIndex = new DoubleConstraintField(new Constraints.
            LowerBound(strings.getObservableProperty("refractiveIndexErrorMessage"),1));
        this.refractiveIndex.setValue(scene.getRefractiveIndex());
        this.refractiveIndex.bindOnActionProperty(create.onActionProperty());
        
        Label indexLabel = new Label();
        Label colorLabel = new Label();
        indexLabel.textProperty().bind(strings.getObservableProperty("indiceLabel"));
        colorLabel.textProperty().bind(strings.getObservableProperty("ambientColorLabel"));
        indexLabel.setFont(subtitlesFont);
        colorLabel.setFont(subtitlesFont);
        
        GridPane gridPane = new GridPane();
        gridPane.addRow(0,indexLabel,refractiveIndex);
        gridPane.addRow(1,colorLabel,colorPicker);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        root.getChildren().add(gridPane);
        
        AnchorPane.setLeftAnchor(gridPane,30d);
		AnchorPane.setTopAnchor(gridPane,10d);
		AnchorPane.setRightAnchor(gridPane,30d);
    }

    @Override
    protected NamedObject<Scene> create() {
        double         n  = refractiveIndex.getValue();
        StringProperty sp = strings.getObservableProperty("scene");
        return n != DoubleConstraintField.ERROR_RETURN ? new NamedObject<>(sp,new Scene(colorPicker.getValue(),n)) : null;
    }

	@Override
	public void show(Scene scene) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
