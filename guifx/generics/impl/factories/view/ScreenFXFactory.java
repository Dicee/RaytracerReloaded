package guifx.generics.impl.factories.view;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import java.util.function.Consumer;
import scene.Screen;

public class ScreenFXFactory extends GraphicFactory<Screen> {
	public ScreenFXFactory() {
		this(null);
	}
	
	public ScreenFXFactory(Consumer<Screen> consumer) {
		super(strings.getObservableProperty("createScreenTitle"),strings.getObservableProperty("createAction"),
				consumer);
	}
	
	@Override
	protected Screen create() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
