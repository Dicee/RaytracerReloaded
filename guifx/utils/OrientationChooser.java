package guifx.utils;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import java.util.Arrays;
import java.util.Iterator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.math.Vector3D;

public class OrientationChooser extends VBox {
	public static Vector3D	errorReturn	= null;
	private final Slider	sx, sy, sz;
	private final Label		xValue, yValue, zValue;
	
	public OrientationChooser() {
		super(5);
		Label title = new Label();
		HBox xAxis  = new HBox(15,new Label("X : "),sx = new Slider(0,150,10),xValue = new Label("0 °"));
		HBox yAxis  = new HBox(15,new Label("Y : "),sy = new Slider(0,150,10),yValue = new Label("0 °"));
		HBox zAxis  = new HBox(15,new Label("Z : "),sz = new Slider(0,150,10),zValue = new Label("0 °"));
		title.textProperty().bind(strings.getObservableProperty("orientationChoiceLabel"));
		title.setFont(GraphicFactory.subtitlesFont);
		
		Iterator<Slider> sliders = Arrays.asList(sx,sy,sz).iterator();
		Iterator<Label> labels   = Arrays.asList(xValue,yValue,zValue).iterator();
		while (sliders.hasNext() && labels.hasNext()) {
			Slider slider = sliders.next();
			Label  label  = labels.next();
			
			slider.setMajorTickUnit(60);
			slider.setMinorTickCount(30);
			slider.setMin(-180);
			slider.setMax(180);
			slider.setValue(0);
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue,
					Number newValue) -> {
				label.setText(String.format("%d °",(int) slider.getValue()));
			});
		}
		getChildren().addAll(title,xAxis,yAxis,zAxis);
	}
	
	public Vector3D getSelectedValues() {
		return new Vector3D(sx.getValue(),sy.getValue(),sz.getValue());
	}
}
