package guifx.generics;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ContextToolBar extends ToolBar {
	
	private Label title;
	
	public ContextToolBar(StringProperty titleProperty, double prefWidth, Node... items) {
		super(items);
		setPrefWidth(prefWidth);
		
		title = new Label();
		title.setFont(Font.font("",FontWeight.BOLD,20));
		title.setAlignment(Pos.CENTER);
		title.textProperty().bind(titleProperty);
		getItems().add(0,title);		
		
		widthProperty().addListener(obs -> resizeItems());		
	}
	
	public final void resizeItems() {
		double maxWidth  = getPrefWidth();
		Button[] buttons = new Button[getItems().size() - 1];
		int i            = 0;
		for (Node item : getItems()) 
			if (item instanceof Button) {
				Button b     = (Button) item;
				maxWidth     = Math.max(maxWidth,b.prefWidth(b.getHeight()));	
				buttons[i++] = b;
			}
		for (Button b : buttons) 
			b.setPrefWidth(maxWidth);
		title.setMaxWidth(maxWidth);
	}
	
	public ContextToolBar(StringProperty titleProperty, double prefWidth) {
		this(titleProperty,prefWidth,new Node[] {});		
	}
}