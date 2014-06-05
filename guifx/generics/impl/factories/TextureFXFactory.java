package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import guifx.utils.Constraints;
import guifx.utils.DoubleConstraintField;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import objects.Texture;
import guifx.utils.HLabelledNode;

public class TextureFXFactory extends GraphicFactory<Texture> {
    private static final double PREFERRED_WIDTH = 610;
	private static final double PREFERRED_HEIGHT = 400;
	/**
	 * ColorPicker(s) nums
	 */
	private static final int Ka = 0;
	private static final int Kr = 1;
	private static final int Kt = 2;
	private static final int reflectance = 3;

	/**
	 * DoubleConstraintField(s) nums
	 */
	private static final int indice = 0;
	private static final int brilliance = 1;

	private final TitledPane commonCaracteristics = new TitledPane();
	private final TitledPane specificities = new TitledPane();

	private final ColorPicker[] colorPickers = new ColorPicker[4];
	private final DoubleConstraintField[] fields = new DoubleConstraintField[2];

	public TextureFXFactory() {
		this(null);
	}
	
	public TextureFXFactory(Consumer<NamedObject<Texture>> consumer) {
		super(strings.getObservableProperty("createTextureTitle"),strings.getObservableProperty("createAction"),
			consumer,PREFERRED_WIDTH,PREFERRED_HEIGHT);
		
		GridPane gridPane = new GridPane();
		
		gridPane.add(labelledField(indice,"indiceLabel","refractiveIndexErrorMessage",1),0,0);
        gridPane.add(labelledField(brilliance,"brillianceLabel","brillianceErrorMessage",0),1,0);
        gridPane.add(labelledColorPicker(Kr,"Kr"),0,1);
        System.out.println(colorPickers[Kr]);
        //gridPane.add(labelledColorPicker(Kt,"Kt"),1,1);
        //gridPane.add(labelledColorPicker(reflectance,strings.getObservableProperty("reflectanceLabel")),1,2);
		root.getChildren().add(gridPane);
		
		AnchorPane.setTopAnchor(gridPane,20d);
		AnchorPane.setLeftAnchor(gridPane,30d);
		AnchorPane.setRightAnchor(gridPane,30d);
	}
	
	@Override
	protected NamedObject<Texture> create() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private HBox labelledColorPicker(int i, String name) {
		return new HLabelledNode<>(10,
			colorPickers[i] = new ColorPicker(),
            name,
			subtitlesFont);
	}
    
    private HBox labelledColorPicker(int i, StringProperty nameProperty) {
		return new HLabelledNode<>(10,
			colorPickers[i] = new ColorPicker(),
            nameProperty,
			subtitlesFont);
	}

	private final HBox labelledField(int i, String propertyName, String errorPropertyName, double min) {
		StringProperty text  = strings.getObservableProperty(propertyName); 
		StringProperty error = strings.getObservableProperty(errorPropertyName);
		fields[i]            = new DoubleConstraintField(new Constraints.LowerBound(error,min));
		return new HLabelledNode<>(10,fields[i],text,subtitlesFont);
	}
}
