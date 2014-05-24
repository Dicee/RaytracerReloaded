package guifx.generics;

import guifx.MainUI;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;

public abstract class SceneElementTab<T> extends Tab {
	private final ContextToolBar toolbar;
	protected final ListExplorer<T> listExplorer;
	
	public SceneElementTab(StringProperty titleProperty, StringProperty toolbarTitleProperty) {
		super();
		this.toolbar      = new ContextToolBar(toolbarTitleProperty,110);
		this.listExplorer = new ListExplorer<>();
		textProperty().bind(titleProperty);		
		
		toolbar.setOrientation(Orientation.VERTICAL);
		listExplorer.setPrefWidth(2*MainUI.PREFERRED_WIDTH);
		
		Accordion tools = new Accordion();
		TitledPane pane1 = new TitledPane("Nouveau",null);
		TitledPane pane2 = new TitledPane("Editer",null);
		TitledPane pane3 = new TitledPane("Supprimer",null);
		tools.getPanes().addAll(pane1,pane2,pane3);
		
		setContent(new HBox(toolbar,listExplorer));
		setClosable(false);
	}
		
	public void addTool(Button b, Tools type) {
		if (isSupported(type)) {
			toolbar.getItems().add(b);
			b.setOnAction(doAction(type));
		} else
			throwUnsupportedTool(type);
	}

	private void throwUnsupportedTool(Tools type) {
		throw new UnsupportedOperationException(
			String.format("The tool %s is not supported by the class %s",type,getClass().getName()));
	}
	
	protected abstract EventHandler<ActionEvent> doAction(Tools type);
	protected abstract boolean isSupported(Tools type);
	
	public ObservableList<T> getItems() {
		return listExplorer.getListView().getItems();
	}
}
