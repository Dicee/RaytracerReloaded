package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import utils.NamedObject;
import guifx.utils.Constraints;
import guifx.utils.DoubleConstraintField;
import guifx.utils.TextureID;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import objects.Texture;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import objects.AdvancedTexture;
import objects.BasicTexture;
import org.controlsfx.dialog.Dialogs;

public class TextureFXFactory extends GraphicFactory<Texture> {
	private static final double				PREFERRED_WIDTH			= 450;
	private static final double				PREFERRED_HEIGHT		= 260;
	/**
	 * ColorPicker(s) nums
	 */
	private static final int				Ka						= 0;
	private static final int				Kr						= 1;
	private static final int				Kt						= 2;
	private static final int				REFLECTANCE				= 3;

	/**
	 * DoubleConstraintField(s) nums
	 */
	private static final int				REFRACTIVE_INDEX		= 0;
	private static final int				BRILLIANCE				= 1;

	private final TitledPane				commonCaracteristics	= new TitledPane();
	private final TitledPane				specificities			= new TitledPane();

	private final ColorPicker[]				colorPickers			= new ColorPicker[4];
	private final DoubleConstraintField[]	fields					= new DoubleConstraintField[2];

	private final RadioButton				basic					= new RadioButton(), advanced = new RadioButton();
	private TextField						path;
	private File							currentDir				= null;

	/**
	 * boolean used for data validation
	 */
	private boolean							failure;

	public TextureFXFactory() {
		this(null);
	}
	
	public TextureFXFactory(Consumer<NamedObject<Texture>> consumer) {
		super(strings.getObservableProperty("createTextureTitle"),strings.getObservableProperty("createAction"),
			consumer,PREFERRED_WIDTH,PREFERRED_HEIGHT);
	
		BorderPane mainPane = new BorderPane();
		mainPane.setLeft(setCommonCaracteristics());
		mainPane.setRight(setBasicSpecificities());
		mainPane.setTop(setToggleGroup());
		root.getChildren().add(mainPane);
		
		AnchorPane.setTopAnchor(mainPane,20d);
		AnchorPane.setLeftAnchor(mainPane,30d);
		AnchorPane.setRightAnchor(mainPane,30d);
	}
	
	@Override
	protected NamedObject<Texture> create() {
		failure                = false;
		double refractiveIndex = getDoubleFieldValue(REFRACTIVE_INDEX);
		double brilliance      = getDoubleFieldValue(BRILLIANCE);
		
		if (failure)
			return null;
		
		Texture texture   = null;
		Color KrValue     = colorPickers[Kr].getValue();
		Color KtValue     = colorPickers[Kt].getValue();
		Color reflectance = colorPickers[REFLECTANCE].getValue();
		if (basic.isSelected()) {
			texture = new BasicTexture(refractiveIndex,(float) brilliance,reflectance,KrValue, 
				KtValue,colorPickers[Ka].getValue());
		} else {
			try {
				texture = new AdvancedTexture(refractiveIndex,(float) brilliance,reflectance,KrValue,
					KtValue,new File(path.getText()));
			} catch (IOException e) {
				Dialogs.create().owner(root).
					title(strings.getProperty("error")).
					masthead(strings.getProperty("anErrorOccurredMessage")).
					message(strings.getProperty("unfoundFileErrorMessage")).
					showError();
			}
		}
		return texture == null ? null : new TextureID(texture);
	}
	
	private double getDoubleFieldValue(int i) {
		if (failure)
			return DoubleConstraintField.ERROR_RETURN;		
		double result = fields[i].getValue();
		failure      = result == DoubleConstraintField.ERROR_RETURN;
		return result;
	}
	
	private HBox doubleConstraintField(int i, String errorPropertyName, double min) {
		StringProperty error = strings.getObservableProperty(errorPropertyName);
		fields[i]            = new DoubleConstraintField(new Constraints.LowerBound(error,min));
		return fields[i];
	}
	
	private Node[] setLabels() {
		Label refractiveIndex = new Label();
		Label brilliance      = new Label();
		Label Kr              = new Label("Kr");
		Label Kt              = new Label("Kt");
		Label reflectance     = new Label();
		refractiveIndex.textProperty().bind(strings.getObservableProperty("indiceLabel"));
		brilliance.textProperty().bind(strings.getObservableProperty("brillianceLabel"));
		reflectance.textProperty().bind(strings.getObservableProperty("reflectanceLabel"));
		
		List<Label> labels    = Arrays.asList(refractiveIndex,brilliance,Kr,Kt,reflectance);
		labels.stream().forEach(label -> label.setFont(subtitlesFont));
		return (Node[]) labels.toArray();
	}
	
	private Node[] setFields() {
		Node refractiveIndex = doubleConstraintField(REFRACTIVE_INDEX,"refractiveIndexErrorMessage",1);
		Node brilliance      = doubleConstraintField(BRILLIANCE,"brillianceErrorMessage",5);
		Node KrNode          = colorPickers[Kr]          = new ColorPicker();
		Node KtNode          = colorPickers[Kt]          =  new ColorPicker();
		Node reflectance     = colorPickers[REFLECTANCE] =  new ColorPicker();
		return new Node[] { refractiveIndex,brilliance,KrNode,KtNode,reflectance };
	}

	private Node setCommonCaracteristics() {
		GridPane gridPane = new GridPane();
		gridPane.addColumn(0,setLabels());
		gridPane.addColumn(1,setFields());
		gridPane.setHgap(10);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(5,5,5,5));
		
		commonCaracteristics.setContent(gridPane);
		commonCaracteristics.setCollapsible(false);
		commonCaracteristics.textProperty().bind(strings.getObservableProperty("commonCaracteristicsLabel"));
		commonCaracteristics.setFocusTraversable(false);
		
		return commonCaracteristics;
	}

	private Node setBasicSpecificities() {
		Label KaNode = new Label("Ka");
		KaNode.setFont(subtitlesFont);
		
		HBox content = new HBox(10,KaNode,colorPickers[Ka] = new ColorPicker());
		content.setPadding(new Insets(5));
		
		specificities.textProperty().bind(strings.getObservableProperty("specificitiesLabel"));
		specificities.setCollapsible(false);
		specificities.setFocusTraversable(false);
		specificities.setContent(content);
		
		return specificities;
	}
	
	private Node setAdvancedSpecificities() {
		path            = new TextField();
		Label pathLabel = new Label();
		Button browse   = new Button();
		pathLabel.textProperty().bind(strings.getObservableProperty("pathLabel"));
		pathLabel.setFont(subtitlesFont);
		browse.textProperty().bind(strings.getObservableProperty("browseLabel"));
		
		browse.setOnAction(ev -> {
			FileChooser chooser = new FileChooser();
			if (currentDir != null)
				chooser.setInitialDirectory(currentDir);
			
			String imageFiles = strings.getProperty("imageFiles");
			chooser.getExtensionFilters().add(new FileChooser.
				ExtensionFilter(imageFiles + " (*.png, *.jpg...)", "*.png","*.jpg","*.jpeg","*.JPG"));
			File selectedFile = chooser.showOpenDialog(null);
			if (selectedFile != null) {
				currentDir = selectedFile.getParentFile();
				path.setText(selectedFile.getAbsolutePath());
			}
		});
		
		VBox content = new VBox(10,pathLabel,path,browse);
		content.setPadding(new Insets(5));
		
		specificities.setContent(content);
		return specificities;
	}

	private Node setToggleGroup() {
		ToggleGroup group = new ToggleGroup();
		basic   .textProperty().bind(strings.getObservableProperty("basicTextureLabel"));
		advanced.textProperty().bind(strings.getObservableProperty("advancedTextureLabel"));
		group.getToggles().addAll(basic,advanced);
		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,
			Toggle oldToggle, Toggle newToggle) -> {
				Node spec;
				if (newToggle == basic) 
					setBasicSpecificities();
				else 
					setAdvancedSpecificities();
			});
		group.selectToggle(basic);
		
		HBox result = new HBox(10,basic,advanced);
		result.setPadding(new Insets(5));
		return result;
	}
}
