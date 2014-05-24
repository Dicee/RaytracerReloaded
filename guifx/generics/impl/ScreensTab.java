package guifx.generics.impl;

import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import java.util.Arrays;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import scene.Screen;

public class ScreensTab extends SceneElementTab<Screen> {
	public ScreensTab(StringProperty titleProperty, StringProperty toolbarTitleProperty) {
		super(titleProperty, toolbarTitleProperty);
	}

	@Override
	protected EventHandler<ActionEvent> doAction(Tools type) {
		return (ActionEvent ev) -> System.out.println(String.format("%s not yet implemented by the type %s",
				type,ScreensTab.class.getName()));
	}
	
	@Override
	protected boolean isSupported(Tools type) {
		return Arrays.asList(Tools.CREATE,Tools.EDIT,Tools.DELETE,
				Tools.ROTATE,Tools.RESIZE,Tools.TRANSLATE).contains(type);
	}
}