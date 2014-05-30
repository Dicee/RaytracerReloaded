package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import objects.Texture;

public class TextureFactory extends GraphicFactory<Texture> {
	public TextureFactory() {
		super(strings.getObservableProperty("createTextureTitle"),
			strings.getObservableProperty("createAction"));
	}
	
	@Override
	public Texture create(Class<? extends Texture> targetClass, Object... args) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
