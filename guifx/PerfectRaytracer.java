package guifx;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javafx.application.Application;
import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;
import static javafx.application.Application.setUserAgentStylesheet;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ObservableProperties;

/**
 *
 * @author David Courtinot
 */ 
public class PerfectRaytracer extends Application {
	
	private static final Properties			properties		= new Properties();
	private static final ObservableProperties	strings			= new ObservableProperties();

	private static final int			PREFERRED_SKIN		= 0;
	private static final int			PREFERRED_LANGUAGE	= 1;

	private MenuItem[]				preferences		= new MenuItem[2];
	
	@Override
	public void start(Stage primaryStage)  {	
		//Loading preferences and program constants
		try (InputStreamReader isr = new InputStreamReader(
				getClass().getResourceAsStream("/properties/config.properties"))) {
			properties.load(isr);
			isr.close();
		} catch (IOException e) {
			System.out.println("Error while retrieving the application properties");
			e.printStackTrace();
		}	
		
		//Loading localised texts and descriptions
		loadLocalizedTexts(properties.getProperty(properties.getProperty("defaultLanguage")));			
		
        VBox root       = new VBox(10);
        MenuBar menuBar = setMenuBar();        
        VBox header     = setHeader(menuBar);
		
        root.getChildren().add(header);
        
        Scene scene = new Scene(root,1100,600);
        primaryStage.setTitle("LateX Editor 3.0");
        primaryStage.setScene(scene);
        primaryStage.show();    
	}

	private void loadLocalizedTexts(String lang) {
        try (InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(lang))) {
			strings.load(isr);
			isr.close();
		} catch (IOException e) {
			System.out.println("Error while retrieving the application texts and descriptions");
			e.printStackTrace();
		}
	}
	
	private MenuBar setMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile   = new Menu();
        Menu menuEdit   = new Menu();
        Menu menuHelp   = new Menu();
		
	menuFile.textProperty().bind(strings.getObservableProperty("file"));
	menuEdit.textProperty().bind(strings.getObservableProperty("edit"));
	menuHelp.textProperty().bind(strings.getObservableProperty("help"));
        
        MenuItem newScene;
        MenuItem save;
        MenuItem saveAs;
        MenuItem load;
        MenuItem quit;
        
        menuFile.getItems().add(newScene = new MenuItem());
        menuFile.getItems().add(load     = new MenuItem());
        menuFile.getItems().add(save     = new MenuItem());
        menuFile.getItems().add(saveAs   = new MenuItem());
        menuFile.getItems().add(quit     = new MenuItem());
		
	newScene.textProperty().bind(strings.getObservableProperty("newScene"));
	load    .textProperty().bind(strings.getObservableProperty("loadScene"));
	save    .textProperty().bind(strings.getObservableProperty("saveScene"));
	saveAs  .textProperty().bind(strings.getObservableProperty("saveSceneAs"));
	quit    .textProperty().bind(strings.getObservableProperty("quit"));
       
        newScene.setAccelerator(new KeyCharacterCombination("N",
                                KeyCombination.CONTROL_DOWN));
        save    .setAccelerator(new KeyCharacterCombination("S",
                                KeyCharacterCombination.CONTROL_DOWN));
        saveAs  .setAccelerator(new KeyCharacterCombination("S",
                                KeyCharacterCombination.CONTROL_DOWN,
                                KeyCharacterCombination.ALT_DOWN));
        load    .setAccelerator(new KeyCharacterCombination("L",
                                KeyCharacterCombination.CONTROL_DOWN));        
        quit    .setAccelerator(new KeyCharacterCombination("Q",
                                KeyCharacterCombination.CONTROL_DOWN));
        
        quit.setOnAction((ActionEvent ev) -> {
            System.exit(0);
        });
        menuBar.getMenus().addAll(menuFile,menuEdit,menuHelp);
        
		final ImageView checkedIcon = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty("checkedIcon"))));
		
        Menu chooseStyle    = setChooseStyle(checkedIcon);	
		Menu chooseLanguage = setChooseLanguage(checkedIcon);	   
		
        menuHelp.getItems().addAll(chooseStyle,chooseLanguage);
		return menuBar;
    }
	
	private Menu setChooseLanguage(final ImageView checkedIcon) {
		Menu chooseLanguage     = new Menu();
		final MenuItem french   = new MenuItem();
		final MenuItem english  = new MenuItem();		
		MenuItem       selectedMenu;
		
		chooseLanguage.textProperty().bind(strings.getObservableProperty("lang"));
		french        .textProperty().bind(strings.getObservableProperty("lang-fr"));
		english       .textProperty().bind(strings.getObservableProperty("lang-en"));
		
		switch (properties.getProperty("defaultLanguage")) {
			case "FR" : 
				selectedMenu = french;
				break;
			default :
				selectedMenu = english;
		}
		selectedMenu.setGraphic(checkedIcon);
		chooseLanguage.getItems().addAll(french,english);
		preferences[PREFERRED_LANGUAGE] = french;
		
		french.setOnAction((ActionEvent ev) -> {
			if (french != preferences[PREFERRED_LANGUAGE])
				loadLocalizedTexts(properties.getProperty("FR"));
			changePreference(french,PREFERRED_LANGUAGE,checkedIcon);
		});   
		english.setOnAction((ActionEvent ev) -> {
			if (english != preferences[PREFERRED_LANGUAGE])
				loadLocalizedTexts(properties.getProperty("EN"));
			changePreference(english,PREFERRED_LANGUAGE,checkedIcon);			
		});
		return chooseLanguage;
	}
	
	private Menu setChooseStyle(final ImageView checkedIcon) {
		Menu chooseStyle        = new Menu();		
		final MenuItem caspian  = new MenuItem("Caspian");
        	final MenuItem modena   = new MenuItem("Modena");
		MenuItem       selectedMenu;
		switch (properties.getProperty("defaultStyle")) {
			case "CASPIAN" : 
				selectedMenu = caspian;
				break;
			default :
				selectedMenu = modena;
		}
		selectedMenu.setGraphic(checkedIcon);
		preferences[PREFERRED_SKIN] = caspian;
		setUserAgentStylesheet(properties.getProperty("defaultStyle"));
		
		chooseStyle.textProperty().bind(strings.getObservableProperty("skin"));
		
		caspian.setOnAction((ActionEvent ev) -> {			
			setUserAgentStylesheet(STYLESHEET_CASPIAN);
			changePreference(caspian,PREFERRED_SKIN,checkedIcon);
		});
		modena.setOnAction((ActionEvent ev) -> {
			setUserAgentStylesheet(STYLESHEET_MODENA);
			changePreference(modena,PREFERRED_SKIN,checkedIcon);
		});       
  		chooseStyle.getItems().addAll(caspian,modena);
		return chooseStyle;
	}
	
	private void changePreference(MenuItem clicked, int checkedIndex, Node node) {
		MenuItem checked = preferences[checkedIndex];
		if (checked != clicked) {
			checked.setGraphic(null);
			clicked.setGraphic(node);
			preferences[checkedIndex] = clicked;
		}
	}
	
	private VBox setHeader(MenuBar menuBar) {
        VBox header              = new VBox();
        ImageView resizeIcon     = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty("resizeIcon"))));
        ImageView translateIcon  = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty("translateIcon"))));
        ImageView trashIcon      = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty("trashIcon"))));
        ImageView showOrHideIcon = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty("showOrHideIcon"))));
        
        Button resize     = new Button("",resizeIcon);
        Button translate  = new Button("",translateIcon);
	Button showOrHide = new Button("",showOrHideIcon);
        Button trash      = new Button("",trashIcon); 
		
	resize    .textProperty().bind(strings.getObservableProperty("resize"));
	translate .textProperty().bind(strings.getObservableProperty("translate"));
	showOrHide.textProperty().bind(strings.getObservableProperty("showOrHide"));
	trash     .textProperty().bind(strings.getObservableProperty("trash"));
        
        ToolBar tb = new ToolBar(resize,translate,showOrHide,trash);
        header.getChildren().addAll(menuBar,tb);
        return header;
    }	
	
	public static void main(String[] args) {
		launch(args);
	}
}
