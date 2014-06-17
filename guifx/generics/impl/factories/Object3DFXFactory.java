package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import static objects.Object3DFactory.*;
import guifx.generics.GraphicFactory;
import utils.NamedObject;
import guifx.utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import objects.Object3D;
import objects.Texture;
import utils.math.Point;
import utils.math.Vector3D;

public class Object3DFXFactory extends GraphicFactory<Object3D> {
	private static final double							PREFERRED_WIDTH			= 680;
	private static final double							PREFERRED_HEIGHT		= 450;
	private static final String[]						typesProperties			= { "sphere", "planeSurface", "cube",
																					"parallelepiped", "cone", "cylinder" };

	private final ObservableList<NamedObject<Texture>>	textures;

	private final OrientationChooser					orientationChooser		= new OrientationChooser();
	private final ScaledSlider							scale					= new ScaledSlider(0,100,1);
	private final TitledPane							commonCaracteristics	= new TitledPane();
	private final TitledPane							specificities			= new TitledPane();
	private final TitledPane							textureCaracteristics	= new TitledPane();
	private final VectorBuilder							centerBuilder			= new VectorBuilder();

	/**
	 * Specific fields
	 */
	private final DoubleConstraintField[]				doubleValueFields		= new DoubleConstraintField[2];
	private final LabelledSlider[]						angleSliders			= new LabelledSlider[3];
	private final RadioButton							finite					= new RadioButton(),
			infinite = new RadioButton();
	private final ComboBox<StringProperty>				typeChoice;

	/**
	 * Texture fields
	 */
	private final Slider								patternRepeat			= new Slider();
	private final RadioButton							repeat					= new RadioButton(),
			noRepeat = new RadioButton();
	private final RadioButton							adapt					= new RadioButton(),
			noAdapt = new RadioButton();
	private final ComboBox<NamedObject<Texture>>		textureIds				= new ComboBox<>();

	/**
	 * boolean used for data validation
	 */
	private boolean										failure;
	
	public Object3DFXFactory(ObservableList<NamedObject<Texture>> textures) {
		this(null,textures);
	}
	
	public Object3DFXFactory(Consumer<NamedObject<Object3D>> consumer, ObservableList<NamedObject<Texture>> textures) {
		super(strings.getObservableProperty("createObjectTitle"),strings.getObservableProperty("createAction"),
			consumer,PREFERRED_WIDTH,PREFERRED_HEIGHT);
        this.textures = textures;
		this.commonCaracteristics .textProperty().bind(strings.getObservableProperty("commonCaracteristicsLabel"));
		this.specificities        .textProperty().bind(strings.getObservableProperty("specificitiesLabel"));
        this.textureCaracteristics.textProperty().bind(strings.getObservableProperty("textureCaracteristicsLabel"));
		setCommonCaracteristics();
		setSpecificities();
        setTextureFields();
        
		List<StringProperty> types = new ArrayList<>();
		for (String propertyName : typesProperties)
			types.add(strings.getObservableProperty(propertyName));
		
		typeChoice          = setTypeChoice(types);
		Label    comboLabel = new Label();
		HBox     header     = new HBox(12,comboLabel,typeChoice);  
		comboLabel.textProperty().bind(strings.getObservableProperty("objectTypeLabel"));
        
        GridPane gridPane = new GridPane();
        gridPane.add(commonCaracteristics,0,0,1,2);
        gridPane.addColumn(1,textureCaracteristics,specificities);
        gridPane.setHgap(10);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));
        
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(header);
        borderPane.setCenter(gridPane);        
        root.getChildren().add(borderPane);
        
        AnchorPane.setLeftAnchor(borderPane,30d);
		AnchorPane.setTopAnchor(borderPane,10d);
		AnchorPane.setRightAnchor(borderPane,30d);
		
		primaryStage.setResizable(true);
	}
	
	@Override
	protected NamedObject<Object3D> create() {
		failure            = false;
		Object3D result    = null;
		Point    center    = getCenter();
		Vector3D rotation  = getOrientation();
		
		if (failure)
			return null;
		
        NamedObject<Texture> t = textureIds.getSelectionModel().getSelectedItem();
        Texture texture        = t == null ? Texture.DEFAULT_TEXTURE : t.bean;
        
		String selection = typeChoice.getSelectionModel().getSelectedItem().getName();
		switch (selection) {
			case "sphere"       :
				result = createSphere(scale.getValue(),center,rotation.x,rotation.y,rotation.z,texture);
				break;
			case "cube"         :
				result = createCube(scale.getValue(),center,rotation.x,rotation.y,rotation.z,texture);
				break;
			case "cone"         :
				double ratio = getDoubleFieldValue(0);
				if (!failure)
					result = createCone(scale.getValue(),center,rotation.x,rotation.y,rotation.z,
						ratio,texture);
				break;
			case "cylinder"     :
				ratio = getDoubleFieldValue(0);
				if (!failure)
					result = createCylinder(scale.getValue(),center,rotation.x,rotation.y,rotation.z,
						ratio,texture);
				break;
			case "parallelepiped" :
				double heightRatio = getDoubleFieldValue(0);
				double depthRatio  = getDoubleFieldValue(1);
				double alpha       = angleSliders[0].getValue();
				double beta        = angleSliders[1].getValue();
				if (!failure) 
					result = createParallelepiped(scale.getValue(),center,rotation.x,rotation.y,rotation.z,
						heightRatio,depthRatio,alpha,beta,texture);
				break;
			case "planeSurface" :
				double d = getDoubleFieldValue(0);
				if (infinite.isSelected() && !failure) 
					result = createInfiniteSurface(d,rotation.x,rotation.y,rotation.z,texture);
				else if (!failure) {
					alpha  = angleSliders[0].getValue();
					result = createQuadFace(scale.getValue(),center,rotation.x,rotation.y,rotation.z, 
							d,alpha,texture);
				}
				break;	
			default :
				failure = true;
		}
		
		if (!failure) {
			result.setAdapt(adapt.isSelected());
			result.setRepeat(repeat.isSelected());
			result.setPatternRepeat((int) patternRepeat.getValue());
		}
		return failure ? null : new NamedObject<>(strings.getObservableProperty(result.getName()),result);
	}
	
	private Point getCenter() {
		if (failure || centerBuilder.isDisabled())
			return null;		
		
		Point result = centerBuilder.getPoint();
		failure      = result == VectorBuilder.ERROR_RETURN;
		return result;
	}
	
	private double getDoubleFieldValue(int i) {
		if (failure)
			return DoubleConstraintField.ERROR_RETURN;		
		double result = doubleValueFields[i].getValue();
		failure       = result == DoubleConstraintField.ERROR_RETURN;
		return result;
	}
	
	private Vector3D getOrientation() {
		if (failure || orientationChooser.isDisabled())
			return null;		
		Vector3D result = orientationChooser.getSelectedValues();
		failure       = result == OrientationChooser.errorReturn;
		return result;
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
			
        ToggleGroup group = new ToggleGroup();
		group.getToggles().addAll(finite,infinite);
		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,
			Toggle oldToggle, Toggle newToggle) -> {
				Node spec;
				if (newToggle == infinite) 
					spec = doubleValueSpecifities(strings.getObservableProperty("altitude"),0);
				else {
					ConstraintForm ratio = doubleValueSpecifities(strings.getObservableProperty("widthLengthRatio"),0);
					Constraint<Double> c = new Constraints.
							LowerBound(strings.getObservableProperty("positiveWidthLengthRatioMessage"),0);
					ratio.addConstraint(c);
					spec = new VBox(10,angleSpecificities(0),ratio);
				}
				specificities.setContent(new VBox(10,planeSurfaceSpecifities(false),spec));
				disableFiniteCaracteristics(newToggle == infinite);
			});
		angleSpecificities();
	}

    public final void setTextureFields() {
        ToggleGroup adaptGroup = new ToggleGroup();
        adaptGroup.getToggles().addAll(adapt,noRepeat);
        adapt.setSelected(true);
        Label adaptLabel   = new Label();
        Label noAdaptLabel = new Label();
        adaptLabel  .textProperty().bind(strings.getObservableProperty("adaptLabel"));
        noAdaptLabel.textProperty().bind(strings.getObservableProperty("noAdaptLabel"));
        
        ToggleGroup repeatGroup = new ToggleGroup();
        repeatGroup.getToggles().addAll(repeat,noRepeat);
        repeat.setSelected(true);
        Label repeatLabel   = new Label();
        Label noRepeatLabel = new Label();
        repeatLabel  .textProperty().bind(strings.getObservableProperty("repeatLabel"));
        noRepeatLabel.textProperty().bind(strings.getObservableProperty("noRepeatLabel"));
        
        Label patternRepeatLabel = new Label();
        patternRepeatLabel.textProperty().bind(strings.getObservableProperty("patternRepeatLabel"));
        patternRepeat.setMin(1);
        patternRepeat.setMax(8);
        patternRepeat.setValue(1);
		patternRepeat.setMajorTickUnit(1);
        patternRepeat.setShowTickLabels(true);
        patternRepeat.setShowTickMarks(true);
        
        Label idsLabel = new Label();
        idsLabel.textProperty().bind(strings.getObservableProperty("textureIdsLabel"));
        textureIds.setItems(textures);
        
        Arrays.asList(patternRepeatLabel,idsLabel).stream().forEach(label -> label.setFont(subtitlesFont));
        
        GridPane content = new GridPane();
        content.addRow(2,adaptLabel,adapt,noAdaptLabel,noAdapt);
        content.addRow(3,repeatLabel,repeat,noRepeatLabel,noRepeat);
        content.add(idsLabel,0,0,1,1);
        content.add(textureIds,1,0,3,1);
        content.add(patternRepeatLabel,0,1,1,1);
        content.add(patternRepeat,1,1,3,1);
        content.setPadding(new Insets(5));
        content.setHgap(5);
        content.setVgap(10);
        
        textureCaracteristics.setContent(content);
        textureCaracteristics.setFocusTraversable(false);
        textureCaracteristics.setCollapsible(false);
    }
    
	private ComboBox<StringProperty> setTypeChoice(List<StringProperty> types) {
		ComboBox<StringProperty> typeChoice = new ComboBox<>(FXCollections.observableList(types));
		typeChoice.valueProperty().addListener((ObservableValue<? extends StringProperty> ov, StringProperty oldValue, 
				StringProperty newValue) -> {
			StringProperty ratio = strings.getObservableProperty("heightRayRatio");
			Constraint<Double> ratioConstraint = new Constraints.
				LowerBound(strings.getObservableProperty("positiveHeightRayRatioMessage"),0);
			switch (newValue.getName()) {
				case "sphere"         : show(noSpecificities());                                break;
				case "cube"           : show(noSpecificities());                                break;
				case "parallelepiped" : show(parallelepipedSpecificities());                    break;
				case "cylinder"       : show(doubleValueSpecifities(ratio,ratioConstraint,0));  break;
				case "cone"           : show(doubleValueSpecifities(ratio,ratioConstraint,0));  break;
				default               : show(planeSurfaceSpecifities(true));                    break;
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

	private ConstraintForm doubleValueSpecifities(StringProperty ratioLabelText, int i) {
		doubleValueFields[i] = new DoubleConstraintField(10,ratioLabelText);		
		doubleValueFields[i].setPadding(new Insets(5,5,5,5));
		return doubleValueFields[i];
	}

	private ConstraintForm doubleValueSpecifities(StringProperty ratioLabelText, Constraint<Double> c, int i) {
		ConstraintForm result = doubleValueSpecifities(ratioLabelText,i);
		result.addConstraint(c);
		return result;
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
		
		Constraint<Double> lengthConstraint = new Constraints.
			LowerBound(strings.getObservableProperty("positiveWidthLengthRatioMessage"),0);
		Constraint<Double> depthConstraint = new Constraints.
			LowerBound(strings.getObservableProperty("positiveWidthDepthRatioMessage"),0);	
		
		doubleValueFields[0].addConstraint(lengthConstraint);
		doubleValueFields[1].addConstraint(depthConstraint);
		return content;
	}
	
	private void show(Node content) {
		specificities.setContent(content);
	}
}
