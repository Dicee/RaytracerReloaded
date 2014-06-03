package guifx.generics;

import guifx.MainUI;
import static guifx.MainUI.strings;
import java.util.function.Consumer;
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
import utils.Copiable;

public abstract class SceneElementTab<T> extends Tab implements Consumer<NamedObject<T>> {
	private final ContextToolBar toolbar;
	protected final ListExplorer<T> listExplorer;
	protected GraphicFactory<T> factory;
	protected int index = -1;
	protected boolean editMode;
	
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
	protected abstract GraphicFactory<T> newFactory();
	
	protected final EventHandler<ActionEvent> defaultCreateAction() {
		return (ActionEvent ev) -> { 
			showGraphicFactory(strings.getObservableProperty("createAction"));
			editMode = false;
		};
	}
	
	protected final EventHandler<ActionEvent> defaultEditAction() {
		return (ActionEvent ev) -> { 
			showGraphicFactory(strings.getObservableProperty("editAction"));
			editMode = true;
		};
	}
	
	protected void showGraphicFactory(StringProperty sp) {
		factory = newFactory();
		factory.textProperty().bind(sp);
		factory.show();
	}
	
	public ObservableList<NamedObject<T>> getItems() {
		return listExplorer.getListView().getItems();
	}
	
	@Override
	public void accept(NamedObject<T> item) {
		if (!editMode) 
			getItems().add(++index,item);
		else 
			((Copiable<T>) getItems().get(index).bean).copy(item.bean);
	}
}
