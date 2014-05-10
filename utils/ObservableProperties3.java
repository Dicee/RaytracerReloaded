package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Properties;

public class ObservableProperties3 extends Properties {
	private final Map<String,List<Observer>> observers = new HashMap<>();
		
	public void addPropertyListener(Observer observer, String property) {
		if (!containsKey(property))
			throw new IllegalArgumentException("No such property");
		List<Observer> observersList = observers.get(property);
		observersList                = observersList == null ? new ArrayList<>() : observersList;
		observersList.add(observer);
		observers.put(property,observersList);
	}
		
	@Override
	public Object setProperty(String key, String value) {
		Object result = super.setProperty(key,value);	
		fireListeners(key,value);
		return result;
	}
	
	@Override
	public void load(Reader reader) throws IOException {
		super.load(reader);
		fireListeners();
	}
	
	@Override
	public void load(InputStream fis) throws IOException {
		super.load(fis);
		fireListeners(); 
	}
	
	@Override
	public void loadFromXML(InputStream fis) throws IOException {
		super.loadFromXML(fis);
		fireListeners();
	}
	
	private void fireListeners() {
		for (Map.Entry<Object,Object> entry : entrySet())
			fireListeners((String) entry.getKey(),(String) entry.getValue());
	}
	
	private void fireListeners(String key, String value) {		
		for (Observer obs : observers.get(key)) {
			try {
				obs.update(null, value);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
