package guifx.generics.impl.tabs;

import static guifx.MainUI.strings;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.TextureFactory;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import objects.Texture;

public class TexturesTab extends SceneElementTab<Texture> {
	public TexturesTab() {
		super(strings.getObservableProperty("textures"),strings.getObservableProperty("tools"),
				new TextureFactory());
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {	
		switch (type) {
			case CREATE :
				return (ActionEvent ev) -> factory.show();
			default :
		}
		return (ActionEvent ev) -> System.out.println(String.format("%s not yet implemented by the type %s",
				type,TexturesTab.class.getName()));
	}

	@Override
	protected boolean isSupported(Tools type) {
		return Arrays.asList(Tools.CREATE,Tools.EDIT,Tools.DELETE).contains(type);
	}
}