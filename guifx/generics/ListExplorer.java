package guifx.generics;

import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ListExplorer<T> extends HBox {
	private final ListView<T> listView;
	private final TextArea test;
	
	public ListExplorer() {
		super(2);
		test = new TextArea("salut");
		listView = new ListView();
		
		getChildren().addAll(listView,test);
		
		test.setMaxWidth(Double.MAX_VALUE);
		listView.setMinWidth(100);

		HBox.setMargin(test,new Insets(0,1,0,0));
		HBox.setMargin(listView,new Insets(1,0,1,1));
		HBox.setHgrow(test,Priority.ALWAYS);
	}
	
	public ListView<T> getListView() {
		return listView;
	}
}
