package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import java.util.function.Consumer;
import scene.Screen;

public class ScreenFXFactory extends GraphicFactory<Screen> {
	public ScreenFXFactory() {
		this(null);
	}
	
	public ScreenFXFactory(Consumer<NamedObject<Screen>> consumer) {
		super(strings.getObservableProperty("createScreenTitle"),strings.getObservableProperty("createAction"),
				consumer);
	}
	
	@Override
	protected NamedObject<Screen> create() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
