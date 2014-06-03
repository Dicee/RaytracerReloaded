package guifx.generics.impl.tabs;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.ScreenFXFactory;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import scene.Screen;

public class ScreensTab extends SceneElementTab<Screen> {
	public ScreensTab() {
		super(strings.getObservableProperty("views"),strings.getObservableProperty("tools"));
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {
		switch (type) {
			case CREATE : return defaultCreateAction();
			case EDIT   : return defaultEditAction();
			default :
		}
		return (ActionEvent ev) -> System.out.println(String.format("%s not yet implemented by the type %s",
				type,ScreensTab.class.getName()));
	}
	
	@Override
	protected boolean isSupported(Tools type) {
		return Arrays.asList(Tools.CREATE,Tools.EDIT,Tools.DELETE,
				Tools.ROTATE,Tools.RESIZE,Tools.TRANSLATE).contains(type);
	}
	
	@Override
	protected GraphicFactory<Screen> newFactory() {
		return new ScreenFXFactory(this);
	}
}