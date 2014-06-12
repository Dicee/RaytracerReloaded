package guifx.generics.impl.factories;

import static guifx.utils.DoubleConstraintField.ERROR_RETURN;
import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import utils.NamedObject;
import guifx.utils.Constraint;
import guifx.utils.Constraints;
import guifx.utils.DoubleConstraintField;
import guifx.utils.OrientationChooser;
import guifx.utils.VectorBuilder;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import scene.Screen;
import utils.math.Point;
import utils.math.Vector3D;

public class ScreenFXFactory extends GraphicFactory<Screen> {
	private static final double			PREFERRED_WIDTH		= 540;
	private static final double			PREFERRED_HEIGHT	= 285;

	private final OrientationChooser	orientationChooser;
	private final DoubleConstraintField	heightField, widthField;
	private final VectorBuilder			pViewBuilder;	
	
	public ScreenFXFactory() {
		this(null);
	}
	
	public ScreenFXFactory(Consumer<NamedObject<Screen>> consumer) {
		super(strings.getObservableProperty("createScreenTitle"),strings.getObservableProperty("createAction"),
				consumer,PREFERRED_WIDTH,PREFERRED_HEIGHT);
		
		StringProperty witdhMsg   = strings.getObservableProperty("positiveWidthMessage");
		StringProperty heightMsg  = strings.getObservableProperty("positiveHeightMessage");
		Constraint positiveWidth  = new Constraints.LowerBound(witdhMsg,0);
		Constraint positiveHeight = new Constraints.LowerBound(heightMsg,0);
		
		this.orientationChooser   = new OrientationChooser();
		this.pViewBuilder         = new VectorBuilder();
		this.widthField           = new DoubleConstraintField(positiveWidth);
		this.heightField          = new DoubleConstraintField(positiveHeight);
		
		Label label               = new Label();
		Label xLabel              = new Label("x");
		Label pViewLabel          = new Label();
		label     .textProperty().bind(strings.getObservableProperty("screenDimensionsLabel"));
		pViewLabel.textProperty().bind(strings.getObservableProperty("pointOfView"));
		label     .setFont(subtitlesFont);
		pViewLabel.setFont(subtitlesFont);
		
		HBox dimensions = new HBox(3,widthField,xLabel,heightField);
		dimensions.setPadding(new Insets(0,10,0,0));
		GridPane gridPane = new GridPane();
		gridPane.add(orientationChooser,0,0,2,1);
		gridPane.add(label,0,1,1,1);
		gridPane.add(dimensions,1,1,2,1);
		gridPane.add(pViewLabel,0,2,1,1); 
		gridPane.add(pViewBuilder,1,2,1,1);
		gridPane.setVgap(5);
		gridPane.setHgap(10);
		root.getChildren().add(gridPane);
		
		GridPane.setHalignment(orientationChooser,HPos.RIGHT);
		
		AnchorPane.setTopAnchor(gridPane,20d);
		AnchorPane.setLeftAnchor(gridPane,30d);
		AnchorPane.setRightAnchor(gridPane,30d);
	}
	
	@Override
	protected NamedObject<Screen> create() {
		NamedObject<Screen> result = null;
		Vector3D orientation       = orientationChooser.getSelectedValues();
		double   width             = widthField.getValue();
		double   height            = width == ERROR_RETURN ? ERROR_RETURN : heightField.getValue();
		
		if (width != ERROR_RETURN && height != ERROR_RETURN) {
			Vector3D ex     = new Vector3D(width,0,0);
			Vector3D ez     = new Vector3D(0,0,height);
			Point    O      = new Point(- width/2,0,- height/2);
			Screen   screen = new Screen(O.translate(ex),O,O.translate(ez),pViewBuilder.getPoint());
			screen.rotateXYZ(orientation.x,orientation.y,orientation.z);
			result          = new NamedObject<>(strings.getObservableProperty("screen"),screen);
		}
		return result;
	}
}
