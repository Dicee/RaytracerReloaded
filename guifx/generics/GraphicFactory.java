package guifx.generics;

import static guifx.MainUI.strings;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class GraphicFactory<T> {
	private static final double PREFERRED_WIDTH  = 500;
	private static final double PREFERRED_HEIGHT = 300;
	private static final String backgroundStyle  = 
			"linear-gradient(#b9b9b9 0%,#eeeeee 86%,#909090 87%,#dddddd 100%)";
	protected Stage primaryStage;

	public GraphicFactory(StringProperty titleProperty, StringProperty actionProperty) {
		primaryStage  = new Stage(StageStyle.DECORATED);
		Button close  = new Button();
		Button create = new Button();
		close .textProperty().bind(strings.getObservableProperty("closeAction"));
		create.textProperty().bind(actionProperty);
		
		HBox footer      = new HBox(12,create,close);
		AnchorPane root  = new AnchorPane(footer);
		root.setBackground(new Background(new BackgroundFill(Paint.valueOf(backgroundStyle),
				new CornerRadii(0,false),Insets.EMPTY)));
		close.setOnAction(ev -> hide());
		
		AnchorPane.setBottomAnchor(footer,10d);
		AnchorPane.setRightAnchor(footer,15d);
		
		javafx.scene.Scene scene = new Scene(root,PREFERRED_WIDTH,PREFERRED_HEIGHT,Color.WHITESMOKE);
        primaryStage.titleProperty().bind(titleProperty);
        primaryStage.setScene(scene);
		primaryStage.setResizable(false);
	}
	
	public abstract T create(Class<? extends T> targetClass, Object... args);
	
	public final void show() {
		primaryStage.show();
	}
	
	public final void hide() {
		primaryStage.hide();
	}
}
