package guifx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;

public class ContextToolBar extends ToolBar {
	public ContextToolBar(String title) {
		super();
		getItems().add(new Label(title));
	}

	public ContextToolBar(String title, Node... items) {
		super(items);
		Button button = new Button(title);
		button.setDisable(true);
		getItems().add(0,button);
	}
}
