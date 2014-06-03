package guifx.generics.impl.tabs;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.SourceFXFactory;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import scene.Source;


public class SourcesTab extends SceneElementTab<Source> {
	public SourcesTab() {
		super(strings.getObservableProperty("sources"),strings.getObservableProperty("tools"));
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {
		switch (type) {
			case CREATE : return defaultCreateAction();
			case EDIT   : return defaultEditAction();
			default :
		}
		return (ActionEvent ev) -> System.out.println(String.format("%s not yet implemented by the type %s",
				type,SourcesTab.class.getName()));
	}
	
	@Override
	protected boolean isSupported(Tools type) {
		return Arrays.asList(Tools.CREATE,Tools.EDIT,Tools.DELETE,
				Tools.ROTATE,Tools.SHOW_HIDE,Tools.TRANSLATE).contains(type);
	}
	
	@Override
	protected GraphicFactory<Source> newFactory() {
		return new SourceFXFactory(this);
	}
}
