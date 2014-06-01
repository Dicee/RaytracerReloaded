package guifx.generics.impl.tabs;

import static guifx.MainUI.strings;
import guifx.generics.NamedObject;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.view.Object3DFXFactory;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import objects.Object3D;

public class ObjectsTab extends SceneElementTab<Object3D> {
	public ObjectsTab() {
		super(strings.getObservableProperty("objects"),strings.getObservableProperty("tools"));
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {
		switch (type) {
			case CREATE :
				return (ActionEvent ev) -> { 
					showGraphicFactory();
					editMode = false; 
				};
			case EDIT :
				return (ActionEvent ev) -> { 
					showGraphicFactory();
					editMode = true; 
				};
			default :
		}
		return (ActionEvent ev) -> System.out.println(String.format("%s not yet implemented by the type %s",
				type,ObjectsTab.class.getName()));
	}
	
	@Override
	protected boolean isSupported(Tools type) {
		return Arrays.asList(Tools.CREATE,Tools.EDIT,Tools.DELETE,
				Tools.ROTATE,Tools.SHOW_HIDE,Tools.TRANSLATE,Tools.RESIZE).contains(type);
	}
	
	@Override
	public void accept(NamedObject<Object3D> item) {
		if (!editMode) 
			getItems().add(++index,item);
		else 
			getItems().get(index).bean.copy(item.bean);
	}
	
	private void showGraphicFactory() {
		(factory = new Object3DFXFactory(this)).show();
	}
}
