package guifx;

import guifx.generics.*;
import guifx.generics.impl.tabs.*;
import impl.org.controlsfx.i18n.Localization;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;
import javafx.application.Application;
import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;
import static javafx.application.Application.setUserAgentStylesheet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import objects.Texture;
import utils.ObservableProperties;

public class MainUI extends Application {
	
	public static final Properties				properties			= new Properties();
	public static final ObservableProperties	strings				= new ObservableProperties();
	
	private static final int					PREFERRED_SKIN		= 0;
	private static final int					PREFERRED_LANGUAGE	= 1;
	
	public static final double					PREFERRED_WIDTH		= 1100;
	public static final double					PREFERRED_HEIGHT	= 600;

	private final MenuItem[]					preferences			= new MenuItem[2];
	
	@Override
	public void start(Stage primaryStage)  {
		//Loading preferences and program constants
		loadProperties();	
		
		//Loading localised texts and descriptions
		loadLocalizedTexts(properties.getProperty(properties.getProperty("defaultLanguage")));			
		
        VBox root       = new VBox();
        MenuBar menuBar = setMenuBar();        
        setHeader(root,menuBar);		
		
		TabPane tabPane = new TabPane();
		setSourcesPane(tabPane);		
		setViewsPane(tabPane);		
		SceneElementTab<Texture> textureTab = setTexturesPane(tabPane);	
        setObjectsPane(tabPane,textureTab);
		
		VBox.setVgrow(tabPane,Priority.ALWAYS);		
        root.getChildren().addAll(tabPane);		
		
        Scene scene = new Scene(root,PREFERRED_WIDTH,PREFERRED_HEIGHT,Color.WHITESMOKE);
		primaryStage.setOnCloseRequest(ev -> Platform.exit());
        primaryStage.setTitle("Raytracer Reloaded");
        primaryStage.setScene(scene);
        primaryStage.show(); 
	}
	
	private void setObjectsPane(TabPane tabPane, SceneElementTab<Texture> texturesTab) {
		SceneElementTab tab = new ObjectsTab(texturesTab);		
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		tab.addTool(getButton("resizeIcon","resize"),Tools.RESIZE);
		tab.addTool(getButton("translateIcon","translate"),Tools.TRANSLATE);
		tab.addTool(getButton("rotateIcon","rotate"),Tools.ROTATE);
		tab.addTool(getButton("showOrHideIcon","showOrHide"),Tools.SHOW_HIDE);
		
		tabPane.getTabs().add(0,tab);
	}
	
	private void setSourcesPane(TabPane tabPane) {
		SceneElementTab tab = new SourcesTab();
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		tab.addTool(getButton("translateIcon","translate"),Tools.TRANSLATE);
		tab.addTool(getButton("rotateIcon","rotate"),Tools.ROTATE);
		tab.addTool(getButton("showOrHideIcon","showOrHide"),Tools.SHOW_HIDE);			
		
		tabPane.getTabs().add(tab);
	}
	
	private void setViewsPane(TabPane tabPane) {
		SceneElementTab tab = new ScreensTab();
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		tab.addTool(getButton("resizeIcon","resize"),Tools.RESIZE);
		tab.addTool(getButton("translateIcon","translate"),Tools.TRANSLATE);
		tab.addTool(getButton("rotateIcon","rotate"),Tools.ROTATE);
		
		tabPane.getTabs().add(tab);
	}
	
	private SceneElementTab<Texture> setTexturesPane(TabPane tabPane) {
		SceneElementTab<Texture> tab = new TexturesTab();
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		
		tabPane.getTabs().add(tab);
        return tab;
	}

	private void loadProperties() {
		try (InputStreamReader isr = new InputStreamReader(
				getClass().getResourceAsStream("/properties/config.properties"))) {
			properties.load(isr);
			isr.close();
		} catch (IOException e) {
			System.out.println("Error while retrieving the application properties");
			e.printStackTrace();
		}
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
		preferences[PREFERRED_LANGUAGE] = selectedMenu;
		
		french.setOnAction((ActionEvent ev) -> {
			if (french != preferences[PREFERRED_LANGUAGE]) {
				loadLocalizedTexts(properties.getProperty("FR"));
				Localization.setLocale(new Locale("fr","FR"));
			}
			changePreference(french,PREFERRED_LANGUAGE,checkedIcon);
		});   
		english.setOnAction((ActionEvent ev) -> {
			if (english != preferences[PREFERRED_LANGUAGE]) {
				loadLocalizedTexts(properties.getProperty("EN"));
				Localization.setLocale(new Locale("en","UK"));				
			}
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
		preferences[PREFERRED_SKIN] = selectedMenu;
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
	
	private void setHeader(Pane root, MenuBar menuBar) {
        VBox header         = new VBox();        
        Button computeScene = getButton("computeSceneIcon","computeScene");
        Button editScene    = getButton("editSceneIcon","editScene");         
        ToolBar tb          = new ToolBar(editScene,computeScene);		
        header.getChildren().addAll(menuBar,tb);
		root.getChildren().add(header);
    }		
	
	private Button getButton(String iconPropertyName, String textPropertyName) {
		ImageView icon = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty(iconPropertyName))));
		Button button = new Button("",icon);
		button.setStyle("-fx-alignment:top-left;-fx-graphic-text-gap:10px");
		button.textProperty().bind(strings.getObservableProperty(textPropertyName));
		return button;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
