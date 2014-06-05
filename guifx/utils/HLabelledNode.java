package guifx.utils;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class HLabelledNode<T extends Node> extends HBox {
	public final T node;
	public final Label label;

	public HLabelledNode(int hgap, T node, StringProperty textProperty, Font font) {
		super(hgap);
		this.node  = node;
		this.label = new Label();
		this.label.textProperty().bind(textProperty);
		this.label.setFont(font);
		getChildren().addAll(label,node);
	}

	public HLabelledNode(int hgap, T node, StringProperty textProperty) {
		super(hgap);
		this.node  = node;
		this.label = new Label();
		this.label.textProperty().bind(textProperty);
	}

	public HLabelledNode(int hgap, T node, String text, Font font) {
		super(hgap);
		this.node  = node;
		this.label = new Label(text);
        this.label.setFont(font);
	}
}
