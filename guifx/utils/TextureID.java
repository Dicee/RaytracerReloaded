package guifx.utils;

import utils.NamedObject;
import static guifx.MainUI.strings;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
import objects.Texture;

public class TextureID extends NamedObject<Texture> {
	private static int				currentID	= 0;
	public final int				id;
	private final StringProperty	nameIDProperty;
   
    public static final int currentID() {
        return currentID;
    }
    
    public TextureID(Texture texture) {
        super(strings.getObservableProperty(texture.getName()),texture);
        this.id = currentID++;
        
        final StringProperty nameProperty = super.nameProperty();
        this.nameIDProperty               = new StringPropertyBase() {
            @Override
            public Object getBean() {
                return nameProperty.getBean();
            }

            @Override
            public String getName() {
                return bean.getName();
            }
            
            @Override
            public String getValue() {
                return String.format("%s (id = %d)",nameProperty.getValue(),id);
            }
            
            @Override
            public void setValue(String s) {
                super.setValue(s);
                super.fireValueChangedEvent();
            }
        };
        nameProperty.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
            nameIDProperty.setValue(String.format("%s (id = %d)",newValue,id));
        });
    }
    
    @Override
    public String toString() {
        return String.format("%s (id = %d)",super.toString(),id);
    }
    
    @Override
    public StringProperty nameProperty() {        
		return nameIDProperty;
	}
}
