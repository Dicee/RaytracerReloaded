package guifx.generics;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class ListExplorer<T> extends HBox {
	private final ListView<NamedObject<T>> listView;
	private final TextArea test;
	
	public ListExplorer() {
		super(2);
		test = new TextArea("salut");
		listView = new ListView();
		
		getChildren().addAll(listView,test);
		
		test.setMaxWidth(Double.MAX_VALUE);
		listView.setMinWidth(100);
		
		listView.setCellFactory(new Callback<ListView<NamedObject<T>>, ListCell<NamedObject<T>>>() {
			public ListCell<NamedObject<T>> call(ListView<NamedObject<T>> param) {
				final ListCell<NamedObject<T>> cell = new ListCell<NamedObject<T>>() {
					@Override
					public void updateItem(NamedObject<T> item, boolean empty) {
						super.updateItem(item,empty);
						if (item != null) 
							textProperty().bind(item.nameProperty());
					}
				};
				return cell;
			}
		});
		

		HBox.setMargin(test,new Insets(0,1,0,0));
		HBox.setMargin(listView,new Insets(1,0,1,1));
		HBox.setHgrow(test,Priority.ALWAYS);
	}
	
	public ListView<NamedObject<T>> getListView() {
		return listView;
	}
}
