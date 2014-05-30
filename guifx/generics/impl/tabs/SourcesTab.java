package guifx.generics.impl.tabs;

import static guifx.MainUI.strings;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.SourceFactory;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import scene.Source;


public class SourcesTab extends SceneElementTab<Source> {
	public SourcesTab() {
		super(strings.getObservableProperty("sources"),strings.getObservableProperty("tools"),
				new SourceFactory());
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {
		switch (type) {
			case CREATE :
				return (ActionEvent ev) -> factory.show();
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
}
