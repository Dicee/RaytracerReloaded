/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import scene.Screen;

/**
 *
 * @author David
 */
public class ScreenFactory extends GraphicFactory<Screen> {
	public ScreenFactory() {
		super(strings.getObservableProperty("createScreenTitle"),
			strings.getObservableProperty("createAction"));
	}
	
	@Override
	public Screen create(Class<? extends Screen> targetClass, Object... args) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
