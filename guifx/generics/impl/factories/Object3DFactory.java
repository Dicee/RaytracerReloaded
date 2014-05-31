package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.utils.AdvancedSlider;
import guifx.utils.OrientationChooser;
import guifx.utils.VectorBuilder;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import objects.Object3D;

public class Object3DFactory extends GraphicFactory<Object3D> {
	
	private static final String[] typesProperties = 
		{ "sphere","planeSurface","cube","parallelepiped","cone","cylinder" };
	
	private final OrientationChooser orientationChooser;
	private final AdvancedSlider scale;
	private final TitledPane commonCaracteristics, specificities;
	private final VectorBuilder centerBuilder;
	
	public Object3DFactory() {
		super(strings.getObservableProperty("createObjectTitle"),
			strings.getObservableProperty("createAction"),700,500);
		this.orientationChooser   = new OrientationChooser();
		this.commonCaracteristics = new TitledPane();
		this.specificities        = new TitledPane();
		this.centerBuilder        = new VectorBuilder();
		this.scale                = new AdvancedSlider(0,100,1);
		this.commonCaracteristics.textProperty().bind(strings.getObservableProperty("commonCaracteristicsLabel"));
		this.specificities       .textProperty().bind(strings.getObservableProperty("specificitiesLabel"));
		setCommonCaracteristics();
		setSpecificities();
		
		List<StringProperty> types = new ArrayList<>();
		for (String propertyName : typesProperties)
			types.add(strings.getObservableProperty(propertyName));
		
		ComboBox typeChoice = setTypeChoice(types);
		Label    comboLabel = new Label();
		HBox     header     = new HBox(12,comboLabel,typeChoice);  
		comboLabel.textProperty().bind(strings.getObservableProperty("objectTypeLabel"));
		root.getChildren().addAll(header,commonCaracteristics,specificities);
		
		AnchorPane.setTopAnchor(header,20d);
		AnchorPane.setLeftAnchor(header,40d);
		AnchorPane.setTopAnchor(commonCaracteristics,60d);
		AnchorPane.setLeftAnchor(commonCaracteristics,40d);
		AnchorPane.setTopAnchor(specificities,60d);
		AnchorPane.setRightAnchor(specificities,40d);
	}
	
	@Override
	public Object3D create(Class<? extends Object3D> targetClass, Object... args) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	private void setCommonCaracteristics() {
		Label scaleLabel  = new Label();
		Label centerLabel = new Label();
		scaleLabel .textProperty().bind(strings.getObservableProperty("scaleLabel"));
		centerLabel.textProperty().bind(strings.getObservableProperty("centerLabel"));
		scaleLabel.setFont(subtitlesFont);
		centerLabel.setFont(subtitlesFont);
		
		VBox content = new VBox(10);
		commonCaracteristics.setContent(content);
		commonCaracteristics.setCollapsible(false);
		commonCaracteristics.setFocusTraversable(false);
		content.setPadding(new Insets(5,5,5,5));
		
		Rectangle clip = new Rectangle(600,340);
        clip.setArcHeight(15);
        clip.setArcWidth(15);
		commonCaracteristics.setClip(clip);
		
		content.getChildren().addAll(scaleLabel,scale,centerLabel,centerBuilder,orientationChooser);
	}
	
	private void setSpecificities() {		
		specificities.setCollapsible(false);
		specificities.setFocusTraversable(false);
		
		Rectangle clip = new Rectangle(600,340);
        clip.setArcHeight(15);
        clip.setArcWidth(15);
		specificities.setClip(clip);
	}

	private ComboBox<StringProperty> setTypeChoice(List<StringProperty> types) {
		ComboBox<StringProperty> typeChoice = new ComboBox(FXCollections.observableList(types));
		typeChoice.valueProperty().addListener((ObservableValue<? extends StringProperty> ov, StringProperty oldValue, 
				StringProperty newValue) -> {
			StringProperty ratio = strings.getObservableProperty("heightRayRatio");
			switch (newValue.getName()) {
				case "sphere"         : showNoSpecificities();              break;
				case "cube"           : showNoSpecificities();              break;
				case "parallelepiped" : showAngleSpecificities();           break;
				case "cylinder"       : showLengthRatioSpecifities(ratio);  break;
				case "cone"           : showLengthRatioSpecifities(ratio);  break;
				default               : showPlaneSurfaceSpecifities();      break;
			}
		});
		typeChoice.getSelectionModel().selectFirst();
				
		return typeChoice;
	}

	private Node showNoSpecificities() {
		Label label = new Label();
		label.textProperty().bind(strings.getObservableProperty("noSpecifitiesLabel"));
		label.setFont(GraphicFactory.subtitlesFont);
		label.setPadding(new Insets(5,5,5,5));
		specificities.setContent(label);
		return label;
	}

	private Node showAngleSpecificities() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private Node showLengthRatioSpecifities(StringProperty ratioLabelText) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private Node showPlaneSurfaceSpecifities() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
