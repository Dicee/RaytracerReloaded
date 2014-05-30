package guifx.generics.impl.tabs;

import static guifx.MainUI.strings;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.Object3DFactory;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import objects.Object3D;

public class ObjectsTab extends SceneElementTab<Object3D> {
	public ObjectsTab() {
		super(strings.getObservableProperty("objects"),strings.getObservableProperty("tools"),
				new Object3DFactory());
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {
		switch (type) {
			case CREATE :
				return (ActionEvent ev) -> factory.show();
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
}
