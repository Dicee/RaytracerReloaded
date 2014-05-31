package guifx.generics.impl.factories.view;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.utils.AdvancedSlider;
import guifx.utils.DoubleConstraintField;
import guifx.utils.OrientationChooser;
import guifx.utils.VectorBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import objects.Object3D;

public class Object3DFXFactory extends GraphicFactory<Object3D> {
	private static final double PREFERRED_WIDTH = 610;
	private static final double PREFERRED_HEIGHT = 400;
	private static final String[] typesProperties
			= {"sphere", "planeSurface", "cube", "parallelepiped", "cone", "cylinder"};

	private final OrientationChooser orientationChooser = new OrientationChooser();
	private final AdvancedSlider scale = new AdvancedSlider(0,100,1);
	private final TitledPane commonCaracteristics = new TitledPane();
	private final TitledPane specificities = new TitledPane();
	private final VectorBuilder centerBuilder = new VectorBuilder();
	
	/**
	 * Specific fields
	 */
	private DoubleConstraintField lengthRatioField;
	private Slider alphaSlider, betaSlider;
	private final RadioButton finite = new RadioButton(), infinite = new RadioButton();
	private final ToggleGroup group = new ToggleGroup();
	private final ComboBox<StringProperty> typeChoice;
	
	public Object3DFXFactory() {
		this(null);
	}
	
	public Object3DFXFactory(Consumer<Object3D> consumer) {
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
	protected Object3D create() {
		String selection = typeChoice.getSelectionModel().getSelectedItem().getName();
		switch (selection) {
			case "sphere" :
		}
		return null;
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
		
		//Initialisation of the RadioButton(s) text and event handling
		finite  .textProperty().bind(strings.getObservableProperty("finiteLabel"));
		infinite.textProperty().bind(strings.getObservableProperty("infiniteLabel"));
				
		group.getToggles().addAll(finite,infinite);
		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,
			Toggle oldToggle, Toggle newToggle) -> {
				Node spec = newToggle == infinite ? noSpecificities() : angleSpecificities();
				specificities.setContent(new VBox(10,planeSurfaceSpecifities(false),spec));
			});
		angleSpecificities();
	}

	private ComboBox<StringProperty> setTypeChoice(List<StringProperty> types) {
		ComboBox<StringProperty> typeChoice = new ComboBox(FXCollections.observableList(types));
		typeChoice.valueProperty().addListener((ObservableValue<? extends StringProperty> ov, StringProperty oldValue, 
				StringProperty newValue) -> {
			StringProperty ratio = strings.getObservableProperty("heightRayRatio");
			switch (newValue.getName()) {
				case "sphere"         : show(noSpecificities());              break;
				case "cube"           : show(noSpecificities());              break;
				case "parallelepiped" : show(parallelepipedSpecificities());  break;
				case "cylinder"       : show(lengthRatioSpecifities(ratio));  break;
				case "cone"           : show(lengthRatioSpecifities(ratio));  break;
				default               : show(planeSurfaceSpecifities(true));  break;
			}
		});
		typeChoice.getSelectionModel().selectFirst();
		return typeChoice;
	}

	private Node noSpecificities() {
		Label label = new Label();
		label.textProperty().bind(strings.getObservableProperty("noSpecifitiesLabel"));
		label.setFont(GraphicFactory.subtitlesFont);
		label.setPadding(new Insets(5,5,5,5));
		return label;
	}

	private Node angleSpecificities() {
		Label anglesLabel = new Label();
		Label alphaLabel  = new Label("Alpha");
		Label betaLabel   = new Label("Beta");
		Label alphaValue  = new Label("0 °");
		Label betaValue   = new Label("0 °");
		anglesLabel.textProperty().bind(strings.getObservableProperty("anglesLabel"));
		anglesLabel.setFont(subtitlesFont);
		alphaLabel .setFont(subtitlesFont);
		betaLabel  .setFont(subtitlesFont);
		alphaLabel .setPrefWidth(40);
		betaLabel  .setPrefWidth(40);
		
		alphaSlider              = new Slider(0,180,90);
		betaSlider               = new Slider(0,180,90);	
		Iterator<Slider> sliders = Arrays.asList(alphaSlider,betaSlider).iterator();
		Iterator<Label> labels   = Arrays.asList(alphaValue,betaValue).iterator();
		while (sliders.hasNext() && labels.hasNext()) {
			Slider slider = sliders.next();
			Label  label  = labels.next();
			
			slider.setMajorTickUnit(60);
			slider.setMinorTickCount(30);
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue,
					Number newValue) -> {
				label.setText(String.format("%d °",(int) slider.getValue()));
			});
		}
		
		HBox alpha   = new HBox(10,alphaLabel,alphaSlider,alphaValue);
		HBox beta    = new HBox(10,betaLabel,betaSlider,betaValue);
		VBox content = new VBox(10/*,anglesLabel*/,alpha,beta);		
		content.setPadding(new Insets(5,5,5,5));
		return content;
	}

	private Node lengthRatioSpecifities(StringProperty ratioLabelText) {
		lengthRatioField = new DoubleConstraintField(10,ratioLabelText);
		lengthRatioField.setPadding(new Insets(5,5,5,5));
		return lengthRatioField;
	}

	private Node planeSurfaceSpecifities(boolean select) {	
		if (select) group.selectToggle(infinite);
		HBox buttons = new HBox(10,infinite,finite);
		Node content = select ? new VBox(10,buttons,noSpecificities()) : buttons;
		buttons.setPadding(new Insets(5,5,5,5));
		return content;
	}

	private Node parallelepipedSpecificities() {
		VBox content = new VBox(10,
				angleSpecificities(),
				lengthRatioSpecifities(strings.getObservableProperty("widthLengthRatio")));
		return content;
	}
	
	private void show(Node content) {
		specificities.setContent(content);
	}
}
