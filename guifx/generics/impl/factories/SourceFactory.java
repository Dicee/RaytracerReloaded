package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import scene.Source;

public class SourceFactory extends GraphicFactory<Source> {
	public SourceFactory() {
		super(strings.getObservableProperty("createSourceTitle"),
			strings.getObservableProperty("createAction"));
	}
	
	@Override
	public Source create(Class<? extends Source> targetClass, Object... args) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
