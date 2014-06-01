package guifx.generics.impl.factories.view;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import guifx.generics.NamedObject;
import java.util.function.Consumer;
import objects.Texture;

public class TextureFXFactory extends GraphicFactory<Texture> {
	public TextureFXFactory() {
		this(null);
	}
	
	public TextureFXFactory(Consumer<NamedObject<Texture>> consumer) {
		super(strings.getObservableProperty("createTextureTitle"),strings.getObservableProperty("createAction"),
			consumer);
	}
	
	@Override
	protected NamedObject<Texture> create() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public void clear() {
		
	}
}
