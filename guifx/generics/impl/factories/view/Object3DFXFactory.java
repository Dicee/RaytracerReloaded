package guifx.generics.impl.factories.view;

import static objects.Object3DFactory.*;
import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import guifx.utils.ScaledSlider;
import guifx.utils.DoubleConstraintField;
import guifx.utils.LabelledSlider;
import guifx.utils.OrientationChooser;
import guifx.utils.VectorBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import objects.Object3D;
import utils.math.Point;
import utils.math.Vector3D;

public class Object3DFXFactory extends GraphicFactory<Object3D> {
	private static final double PREFERRED_WIDTH = 610;
	private static final double PREFERRED_HEIGHT = 400;
	private static final String[] typesProperties
			= {"sphere", "planeSurface", "cube", "parallelepiped", "cone", "cylinder"};

	private final OrientationChooser orientationChooser = new OrientationChooser();
	private final ScaledSlider scale = new ScaledSlider(0,100,1);
	private final TitledPane commonCaracteristics = new TitledPane();
	private final TitledPane specificities = new TitledPane();
	private final VectorBuilder centerBuilder = new VectorBuilder();
	
	/**
	 * Specific fields
	 */
	private final DoubleConstraintField[] doubleValueFields = new DoubleConstraintField[2];
	private final LabelledSlider[] angleSliders = new LabelledSlider[3];
	private final RadioButton finite = new RadioButton(), infinite = new RadioButton();
	private final ToggleGroup group = new ToggleGroup();
	private final ComboBox<StringProperty> typeChoice;
	
	/**
	 * boolean used for data validation
	 */
	private boolean failure;
	
	public Object3DFXFactory() {
		this(null);
	}
	
	public Object3DFXFactory(Consumer<NamedObject<Object3D>> consumer) {
		super(strings.getObservableProperty("createObjectTitle"),strings.getObservableProperty("createAction"),
			consumer,PREFERRED_WIDTH,PREFERRED_HEIGHT);
		this.commonCaracteristics.textProperty().bind(strings.getObservableProperty("commonCaracteristicsLabel"));
		this.specificities       .textProperty().bind(strings.getObservableProperty("specificitiesLabel"));
		setCommonCaracteristics();
		setSpecificities();
		
		List<StringProperty> types = new ArrayList<>();
		for (String propertyName : typesProperties)
			types.add(strings.getObservableProperty(propertyName));
		
		typeChoice          = setTypeChoice(types);
		Label    comboLabel = new Label();
		HBox     header     = new HBox(12,comboLabel,typeChoice);  
		comboLabel.textProperty().bind(strings.getObservableProperty("objectTypeLabel"));
		root.getChildren().addAll(header,commonCaracteristics,specificities);
		
		AnchorPane.setTopAnchor(header,20d);
		AnchorPane.setLeftAnchor(header,30d);
		AnchorPane.setTopAnchor(commonCaracteristics,60d);
		AnchorPane.setLeftAnchor(commonCaracteristics,30d);
		AnchorPane.setTopAnchor(specificities,60d);
		AnchorPane.setRightAnchor(specificities,30d);
	}
	
	@Override
	protected NamedObject<Object3D> create() {
		failure                  = false;
		Object3D       result    = null;
		Point          center    = getCenter();
		Vector3D       rotation  = getOrientation();
		
		if (failure)
			return null;
		
		String selection = typeChoice.getSelectionModel().getSelectedItem().getName();
		switch (selection) {
			case "sphere"       :
				result = createSphere(scale.getValue(),center,rotation.x,rotation.y,rotation.z,null);
				break;
			case "cube"         :
				result = createCube(scale.getValue(),center,rotation.x,rotation.y,rotation.z,null);
				break;
			case "cone"         :
				double ratio = getDoubleFieldValue(0);
				if (!failure)
					result = createCone(scale.getValue(),center,rotation.x,rotation.y,rotation.z,
						ratio,null);
				break;
			case "cylinder"     :
				ratio = getDoubleFieldValue(0);
				if (!failure)
					result = createCylinder(scale.getValue(),center,rotation.x,rotation.y,rotation.z,
						ratio,null);
				break;
			case "parallelepiped" :
				double heightRatio = getDoubleFieldValue(0);
				double depthRatio  = getDoubleFieldValue(1);
				double alpha       = angleSliders[0].getValue();
				double beta        = angleSliders[1].getValue();
				if (!failure) 
					result = createParallelepiped(scale.getValue(),center,rotation.x,rotation.y,rotation.z,
						heightRatio,depthRatio,alpha,beta,null);
				break;
			case "planeSurface" :
				double d = getDoubleFieldValue(0);
				if (infinite.isSelected() && !failure) 
					result = createInfiniteSurface(d,rotation.x,rotation.y,rotation.z,null);
				else if (!failure) {
					alpha  = angleSliders[0].getValue();
					result = createQuadFace(scale.getValue(),center,rotation.x,rotation.y,rotation.z, 
							d,alpha,null);
				}
				break;	
			default :
				failure = true;
		}
		//System.out.println("Failure : " + failure);
		return failure ? null : new NamedObject<>(strings.getObservableProperty(result.getName()),result);
	}
	
	private Point getCenter() {
		if (failure || centerBuilder.isDisabled())
			return null;		
		
		Point result = centerBuilder.getPoint();
		failure      = result == VectorBuilder.errorReturn;
		return result;
	}
	
	private double getDoubleFieldValue(int i) {
		if (failure)
			return DoubleConstraintField.errorReturn;		
		double result = doubleValueFields[i].getValue();
		failure       = result == DoubleConstraintField.errorReturn;
		return result;
	}
	
	private Vector3D getOrientation() {
		if (failure || orientationChooser.isDisabled())
			return null;		
		Vector3D result = orientationChooser.getSelectedValues();
		failure       = result == OrientationChooser.errorReturn;
		return result;
	}
	
	@Override
	public void clear() {
		
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
		
		setAngleSliders();
		
		//Initialisation of the RadioButton(s) text and event handling
		finite  .textProperty().bind(strings.getObservableProperty("finiteLabel"));
		infinite.textProperty().bind(strings.getObservableProperty("infiniteLabel"));
				
		group.getToggles().addAll(finite,infinite);
		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,
			Toggle oldToggle, Toggle newToggle) -> {
				Node spec;
				if (newToggle == infinite) 
					spec = doubleValueSpecifities(strings.getObservableProperty("altitude"),0);
				else {
					Node ratio = doubleValueSpecifities(strings.getObservableProperty("widthLengthRatio"),0);
					spec = new VBox(10,angleSpecificities(0),ratio);
				}
				specificities.setContent(new VBox(10,planeSurfaceSpecifities(false),spec));
				disableFiniteCaracteristics(newToggle == infinite);
			});
		angleSpecificities();
	}

	private ComboBox<StringProperty> setTypeChoice(List<StringProperty> types) {
		ComboBox<StringProperty> typeChoice = new ComboBox(FXCollections.observableList(types));
		typeChoice.valueProperty().addListener((ObservableValue<? extends StringProperty> ov, StringProperty oldValue, 
				StringProperty newValue) -> {
			StringProperty ratio = strings.getObservableProperty("heightRayRatio");
			switch (newValue.getName()) {
				case "sphere"         : show(noSpecificities());                break;
				case "cube"           : show(noSpecificities());                break;
				case "parallelepiped" : show(parallelepipedSpecificities());    break;
				case "cylinder"       : show(doubleValueSpecifities(ratio,0));  break;
				case "cone"           : show(doubleValueSpecifities(ratio,0));  break;
				default               : show(planeSurfaceSpecifities(true));    break;
			}
			disableFiniteCaracteristics(newValue.getName().equals("planeSurface") && infinite.isSelected());
		});
		typeChoice.getSelectionModel().selectFirst();
		return typeChoice;
	}

	private void disableFiniteCaracteristics(boolean b) {
		centerBuilder.setDisable(b);
		scale.setDisable(b);
	}
	
	private Node noSpecificities() {
		Label label = new Label();
		label.textProperty().bind(strings.getObservableProperty("noSpecifitiesLabel"));
		label.setFont(GraphicFactory.subtitlesFont);
		label.setPadding(new Insets(5,5,5,5));
		return label;
	}

	private void setAngleSliders() {
		angleSliders[0] = new LabelledSlider("Alpha",10,0,180,90,"%.0f °");
		angleSliders[1] = new LabelledSlider("Beta",10,0,180,90,"%.0f °");
		angleSliders[2] = new LabelledSlider("Gamma",10,0,180,90,"%.0f °");
		
		Arrays.asList(angleSliders).stream().forEach(slider -> {
			slider.getLabel().setPrefWidth(50);
			slider.getLabel().setFont(subtitlesFont);
			slider.setMajorTickUnit(60);
			slider.setMinorTickCount(30);
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
		});
	}
	
	private Node angleSpecificities(int... sliderIndexes) {
		Node[] sliders = new Node[sliderIndexes.length];
		int    i       = 0;
		for (int index : sliderIndexes)
			sliders[i++] = angleSliders[index];
		
		VBox content = new VBox(10,sliders);		
		content.setPadding(new Insets(5,5,5,5));
		return content;
	}
	
	private Node angleSpecificities() {
		int[] indexes = new int[angleSliders.length];
		for (int i=0 ; i<indexes.length ; i++)
			indexes[i] = i;
		return angleSpecificities(indexes);
	}

	private Node doubleValueSpecifities(StringProperty ratioLabelText, int i) {
		doubleValueFields[i] = new DoubleConstraintField(10,ratioLabelText);		
		doubleValueFields[i].setPadding(new Insets(5,5,5,5));
		return doubleValueFields[i];
	}

	private Node planeSurfaceSpecifities(boolean select) {	
		if (select) infinite.setSelected(true);
		HBox buttons = new HBox(10,infinite,finite);
		Node content;
		if (select){
			Node altitude = doubleValueSpecifities(strings.getObservableProperty("altitude"),0);
			content       = new VBox(10,buttons,altitude);
		} else
			content       = buttons;
		buttons.setPadding(new Insets(5,5,5,5));
		return content;
	}

	private Node parallelepipedSpecificities() {
		VBox content = new VBox(10,
				angleSpecificities(0,1),
				doubleValueSpecifities(strings.getObservableProperty("widthLengthRatio"),0),
				doubleValueSpecifities(strings.getObservableProperty("widthDepthRatio"),1));
		return content;
	}
	
	private void show(Node content) {
		specificities.setContent(content);
	}
}
