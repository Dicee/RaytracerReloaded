package guifx;

import guifx.utils.FXPainter;
import guifx.generics.SceneElementTab;
import guifx.generics.Tools;
import guifx.generics.impl.factories.SceneFXFactory;
import guifx.generics.impl.tabs.ObjectsTab;
import guifx.generics.impl.tabs.ScreensTab;
import guifx.generics.impl.tabs.SourcesTab;
import guifx.generics.impl.tabs.TexturesTab;
import impl.org.controlsfx.i18n.Localization;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import objects.Object3D;
import objects.Texture;

import org.controlsfx.dialog.Dialogs;
import org.jdom2.JDOMException;

import scene.Project;
import scene.Raytracer;
import scene.Screen;
import scene.Source;
import utils.NamedObject;
import utils.ObservableProperties;
import XML.XMLProjectBuilder;
import XML.XMLSceneLoader;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

public class MainUI extends Application {
	
	private static final Properties				properties			= new Properties();
	public static final ObservableProperties	strings				= new ObservableProperties();
	
	private static final int					PREFERRED_SKIN		= 0;
	private static final int					PREFERRED_LANGUAGE	= 1;
	public static final double					PREFERRED_WIDTH		= 1100;
	public static final double					PREFERRED_HEIGHT	= 600;

	private final MenuItem[]					preferences			= new MenuItem[2];
    private SceneElementTab<Object3D>           objectsTab;
    private SceneElementTab<Source>             sourcesTab;
    private SceneElementTab<Screen>             screensTab;
    private SceneElementTab<Texture>            texturesTab;
    
    private File                                currentFile;
    private Stage                               primaryStage;
    private scene.Scene                         scene;
    private boolean                             sceneParametersOpenned = false;
    private Project                             project;
    
	@Override
	public void start(Stage primaryStage)  {
        this.primaryStage = primaryStage;
        this.scene        = new scene.Scene(new Color(0,0,0,1),1);
        
		//Loading preferences and program constants
		loadProperties();	
		
		//Loading localised texts and descriptions
		loadLocalizedTexts(properties.getProperty(properties.getProperty("defaultLanguage")));			
		
        VBox root       = new VBox();
        MenuBar menuBar = setMenuBar();        
        setHeader(root,menuBar);		
		
		TabPane tabPane = new TabPane();
		sourcesTab      = setSourcesPane(tabPane);		
		screensTab      = setViewsPane(tabPane);		
		texturesTab     = setTexturesPane(tabPane);	
        objectsTab      = setObjectsPane(tabPane);
		
		VBox.setVgrow(tabPane,Priority.ALWAYS);		
        root.getChildren().addAll(tabPane);		
		
        Scene scene = new Scene(root,PREFERRED_WIDTH,PREFERRED_HEIGHT,Color.WHITESMOKE);
		primaryStage.setOnCloseRequest(ev -> Platform.exit());
        primaryStage.setTitle("Raytracer Reloaded");
        primaryStage.setScene(scene);
        primaryStage.show(); 
	}
	
    private void clearAll() {
        objectsTab .getItems().clear();
        sourcesTab .getItems().clear();
        screensTab .getItems().clear();
        texturesTab.getItems().clear();
    }
    
    private void load() {
        File file = chooseFile(strings.getProperty("xmlFiles"),false,"*.xml");
        if (file != null) {
        	clearAll();
            try {
                project = XMLSceneLoader.load(file);
                scene.copy(project.getScene());
                
                project.getScene().getObjects().stream().
                    map(o -> new NamedObject<>(strings.getObservableProperty(o.getName()),o)).
                    forEach(namedObject -> objectsTab.getItems().add(namedObject));
                
                project.getScene().getSources().stream().
                    map(s -> new NamedObject<>(strings.getObservableProperty("source"),s)).
                    forEach(namedObject -> sourcesTab.getItems().add(namedObject));
                
                project.getScreens().stream().
                    map(o -> new NamedObject<>(strings.getObservableProperty("screen"),o)).
                    forEach(namedObject -> screensTab.getItems().add(namedObject));
                
                project.getTextures().stream().
                    map(o -> new NamedObject<>(strings.getObservableProperty(o.getName()),o)).
                    forEach(namedObject -> texturesTab.getItems().add(namedObject));
            } catch (JDOMException ex) {
                Dialogs.create().owner(primaryStage).
                    title(strings.getProperty("error")).
                    masthead(strings.getProperty("anErrorOccurredMessage")).
                    message(strings.getProperty("xmlErrorMessage")).
                    showError();
            } catch (IOException ex) {
                Dialogs.create().owner(primaryStage).
                    title(strings.getProperty("error")).
                    masthead(strings.getProperty("anErrorOccurredMessage")).
                    message(strings.getProperty("unfoundFileErrorMessage")).
                    showError();
            } catch (Throwable t) {
            	Dialogs.create().owner(primaryStage).
                	title(strings.getProperty("error")).
                	masthead(strings.getProperty("anErrorOccurredMessage")).
                	showException(t);
            }
        }
    }
    
    private void save() {
        if (currentFile == null) 
            saveAs();
        else {
            refreshProject();
            try {
            	String path = currentFile.getPath();
            	int i       = path.lastIndexOf('.');
            	path        = i == -1 ? path : path.substring(0,i);
            	currentFile = new File(path + ".xml");
                XMLProjectBuilder.save(currentFile,project);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void refreshScene() {
        scene.setObjects(collectNamedObjects(objectsTab.getItems()));
        scene.setSources(collectNamedObjects(sourcesTab.getItems()));
    }

    private void refreshProject() {
    	refreshScene();
        List<Screen>   screens  = collectNamedObjects(screensTab.getItems());
        List<Texture>  textures = collectNamedObjects(texturesTab.getItems());
        project                 = new Project(scene,screens,textures);
    }
    
    private <T> List<T> collectNamedObjects(ObservableList<NamedObject<T>> objects) {
        return objects.stream().map(namedObject -> namedObject.bean).collect(Collectors.toList());
    } 
    
    private void saveAs() {
        File file = chooseFile(strings.getProperty("xmlFiles"),true,"*.xml");
        if (file != null) 
            save();
    }
    
    private File chooseFile(String filterName, boolean save, String... extensions) {
        FileChooser chooser = new FileChooser();
			if (currentFile != null)
				chooser.setInitialDirectory(currentFile.getParentFile());
			
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterName,extensions));
		File selectedFile = save ? chooser.showSaveDialog(null) : chooser.showOpenDialog(null);
        currentFile       = selectedFile == null ? currentFile : selectedFile;
        return selectedFile;
    }
    
	private SceneElementTab<Object3D> setObjectsPane(TabPane tabPane) {
		SceneElementTab<Object3D> tab = new ObjectsTab(texturesTab);		
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		tab.addTool(getButton("resizeIcon","resize"),Tools.RESIZE);
		tab.addTool(getButton("translateIcon","translate"),Tools.TRANSLATE);
		tab.addTool(getButton("rotateIcon","rotate"),Tools.ROTATE);
		tab.addTool(getButton("showOrHideIcon","showOrHide"),Tools.SHOW_HIDE);
		
		tabPane.getTabs().add(0,tab);
        tabPane.getSelectionModel().select(tab);
        return tab;
	}
	
	private SceneElementTab<Source> setSourcesPane(TabPane tabPane) {
		SceneElementTab<Source> tab = new SourcesTab();
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		tab.addTool(getButton("translateIcon","translate"),Tools.TRANSLATE);
		tab.addTool(getButton("rotateIcon","rotate"),Tools.ROTATE);
		tab.addTool(getButton("showOrHideIcon","showOrHide"),Tools.SHOW_HIDE);			
		
		tabPane.getTabs().add(tab);
        return tab;
	}
	
	private SceneElementTab<Screen> setViewsPane(TabPane tabPane) {
		SceneElementTab<Screen> tab = new ScreensTab();
		
		tab.addTool(getButton("createIcon","create"),Tools.CREATE);
		tab.addTool(getButton("editIcon","edit"),Tools.EDIT);
		tab.addTool(getButton("trashIcon","trash"),Tools.DELETE);
		tab.addTool(getButton("resizeIcon","resize"),Tools.RESIZE);
		tab.addTool(getButton("translateIcon","translate"),Tools.TRANSLATE);
		tab.addTool(getButton("rotateIcon","rotate"),Tools.ROTATE);
		
		tabPane.getTabs().add(tab);
        return tab;
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
        
        quit   .setOnAction(ev -> System.exit(0));
        save   .setOnAction(ev -> save());
        saveAs .setOnAction(ev -> saveAs());
        load   .setOnAction(ev -> load());
        menuBar.getMenus().addAll(menuFile,menuEdit,menuHelp);
        
		final ImageView checkedIcon = new ImageView(
                new Image(getClass().getResourceAsStream(properties.getProperty("checkedIcon"))));
		
        Menu chooseStyle    = setChooseStyle(checkedIcon);	
		Menu chooseLanguage = setChooseLanguage(checkedIcon);	   
		
        menuHelp.getItems().addAll(chooseStyle,chooseLanguage);
		return menuBar;
    }
	
	private Menu setChooseLanguage(final ImageView checkedIcon) {
		Menu chooseLanguage    = new Menu();
		final MenuItem french  = new MenuItem();
		final MenuItem english = new MenuItem();	
        final MenuItem spanish = new MenuItem();
		MenuItem selectedMenu;
		
		chooseLanguage.textProperty().bind(strings.getObservableProperty("lang"));
		french        .textProperty().bind(strings.getObservableProperty("lang-fr"));
		english       .textProperty().bind(strings.getObservableProperty("lang-en"));
        spanish       .textProperty().bind(strings.getObservableProperty("lang-es"));
		
		switch (properties.getProperty("defaultLanguage")) {
			case "FR" : 
				selectedMenu = french;
				break;
            case "ES" : 
				selectedMenu = spanish;
				break;
			default :
				selectedMenu = english;
		}
		selectedMenu.setGraphic(checkedIcon);
		chooseLanguage.getItems().addAll(french,english,spanish);
		preferences[PREFERRED_LANGUAGE] = selectedMenu;
		
		french .setOnAction(languageChoiceAction(french,"FR","fr","FR",checkedIcon));   
		english.setOnAction(languageChoiceAction(english,"EN","en","UK",checkedIcon));
        spanish.setOnAction(languageChoiceAction(spanish,"ES","es","ES",checkedIcon));
		return chooseLanguage;
	}
	
    private EventHandler<ActionEvent> languageChoiceAction(MenuItem menu, String propertyName, String lang, String country, ImageView checkedIcon) {
      return (ActionEvent ev) -> {
    	  if (menu != preferences[PREFERRED_LANGUAGE]) {
    		  loadLocalizedTexts(properties.getProperty(propertyName));
    		  Localization.setLocale(new Locale(lang,country));				
    	  }
    	  changePreference(menu,PREFERRED_LANGUAGE,checkedIcon);
      };
    }
    
	private Menu setChooseStyle(final ImageView checkedIcon) {
		Menu chooseStyle       = new Menu();		
		final MenuItem caspian = new MenuItem("Caspian");
        final MenuItem modena  = new MenuItem("Modena");
		MenuItem selectedMenu;
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
		root  .getChildren().add(header);
        
        editScene.setOnAction(ev -> {
            if (!sceneParametersOpenned) {
                sceneParametersOpenned = true;
                new SceneFXFactory(namedObject -> {
                    if (namedObject != null)
                        scene.copy(namedObject.bean);
                    sceneParametersOpenned = false;
                },scene).show();
            }
        });
        computeScene.setOnAction(ev -> { 
        	switch (screensTab.getItems().size()) {
        		case 0  :
        			Dialogs.create().owner(primaryStage).
    					title(strings.getProperty("error")).
    					masthead(strings.getProperty("anErrorOccurredMessage")).
    					message(strings.getProperty("noAvailableScreenMessage")).
    					showError();
        			break;
        		case 1  :
        			openFXPainter(screensTab.getItems().get(0).bean);
					break;
        		default : 
					showScreenSelector();
					break;
        	}
        });
    }		
	
	private void showScreenSelector() {
		ObservableList<Integer> items = FXCollections.observableArrayList();
		for (int i=1 ; i<=screensTab.getItems().size() ; i++)
			items.add(i);
		
		Stage             stage = new Stage(StageStyle.DECORATED);
		Label             label = new Label();
		ComboBox<Integer> box   = new ComboBox(items);
		Button            ok    = new Button("OK");
		label.textProperty().bind(strings.getObservableProperty("selectScreenMessage"));
		
		ok.setOnAction(ev -> {
			Integer index = box.getSelectionModel().getSelectedItem();
			if (index != null) {
				openFXPainter(screensTab.getItems().get(index).bean);
				stage.hide();
			}
		});
		
		HBox root = new HBox(10,label,box,ok);
		root.setLayoutX(15);
		root.setLayoutY(15);
		
		stage.setScene(new Scene(root,400,40));
		stage.setResizable(false);
		stage.show();
	}
	
	private void openFXPainter(Screen screen) {
		//new Thread(() -> {
			refreshScene();
			new FXPainter(new Raytracer(scene,screen));
		//}).start();
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