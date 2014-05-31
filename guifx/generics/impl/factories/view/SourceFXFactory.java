package guifx.generics.impl.factories.view;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import java.util.function.Consumer;
import scene.Source;

public class SourceFXFactory extends GraphicFactory<Source> {
	public SourceFXFactory() {
		this(null);
	}
	
	public SourceFXFactory(Consumer<Source> consumer) {
		super(strings.getObservableProperty("createSourceTitle"),strings.getObservableProperty("createAction"),
			consumer);
	}
	
	@Override
	protected Source create() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
