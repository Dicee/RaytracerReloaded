package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import objects.Object3D;

public class Object3DFactory extends GraphicFactory<Object3D> {

	public Object3DFactory() {
		super(strings.getObservableProperty("createObjectTitle"),
			strings.getObservableProperty("createAction"));
	}
	
	@Override
	public Object3D create(Class<? extends Object3D> targetClass, Object... args) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
