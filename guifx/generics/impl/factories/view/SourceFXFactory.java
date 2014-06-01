package guifx.generics.impl.factories.view;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import java.util.function.Consumer;
import scene.Source;

public class SourceFXFactory extends GraphicFactory<Source> {
	public SourceFXFactory() {
		this(null);
	}
	
	public SourceFXFactory(Consumer<NamedObject<Source>> consumer) {
		super(strings.getObservableProperty("createSourceTitle"),strings.getObservableProperty("createAction"),
			consumer);
	}
	
	@Override
	protected NamedObject<Source> create() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public void clear() {
		
	}
}
