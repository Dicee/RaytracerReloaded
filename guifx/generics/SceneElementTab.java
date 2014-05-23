package guifx.generics;

import guifx.MainUI;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

public class SceneElementTab<T> extends Tab {
	private final ContextToolBar toolbar;
	private final ListExplorer<T> listExplorer;
	
	public SceneElementTab(StringProperty titleProperty, StringProperty toolbarTitleProperty) {
		super();
		this.toolbar      = new ContextToolBar(toolbarTitleProperty,110);
		this.listExplorer = new ListExplorer<>();
		textProperty().bind(titleProperty);		
		
		toolbar.setOrientation(Orientation.VERTICAL);
		listExplorer.setPrefWidth(2*MainUI.PREFERRED_WIDTH);

		toolbar.resizeItems();
		toolbar.resizeItems();
		
		setContent(new HBox(toolbar,listExplorer));
		setClosable(false);
	}
	
	public ObservableList<Node> getToolBarItems() {
		return toolbar.getItems();
	}
	
	public ObservableList<T> getItems() {
		return listExplorer.getListView().getItems();
	}
}
