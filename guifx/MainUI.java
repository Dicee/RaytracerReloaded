package guifx;

import XML.XMLProjectBuilder;
import scene.Project;
import XML.XMLSceneLoader;
import guifx.generics.*;
import guifx.generics.impl.factories.SceneFXFactory;
import guifx.generics.impl.tabs.*;
import impl.org.controlsfx.i18n.Localization;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.application.Application;
import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;
import static javafx.application.Application.setUserAgentStylesheet;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import objects.Object3D;
import objects.Texture;
import org.controlsfx.dialog.Dialogs;
import org.jdom2.JDOMException;
import scene.Screen;
import scene.Source;
import utils.NamedObject;
import utils.ObservableProperties;

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
            }
        }
    }
    
    private void save() {
        if (currentFile == null) 
            saveAs();
        else {
            refreshProject();
            try {
                XMLProjectBuilder.save(currentFile,project);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshProject() {
        List<Object3D> objects  = collectNamedObjects(objectsTab.getItems());
        List<Source>   sources  = collectNamedObjects(sourcesTab.getItems());
        List<Screen>   screens  = collectNamedObjects(screensTab.getItems());
        List<Texture>  textures = collectNamedObjects(texturesTab.getItems());
        
        scene.setObjects(objects);
        scene.setSources(sources);
        project = new Project(scene,screens,textures);
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
        //selectedFile      = selectedFile == null ? null : new File(selectedFile.getAbsolutePath())
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
		
		french.setOnAction((ActionEvent ev) -> {
            languageChoiceAction(french,"FR","fr","FR",checkedIcon);
		});   
		english.setOnAction((ActionEvent ev) -> {
			languageChoiceAction(english,"EN","en","UK",checkedIcon);			
		});
        spanish.setOnAction((ActionEvent ev) -> {
			languageChoiceAction(spanish,"ES","es","ES",checkedIcon);			
		});
		return chooseLanguage;
	}
	
    private void languageChoiceAction(MenuItem menu, String propertyName, String lang, String country, ImageView checkedIcon) {
        if (menu != preferences[PREFERRED_LANGUAGE]) {
			loadLocalizedTexts(properties.getProperty(propertyName));
			Localization.setLocale(new Locale(lang,country));				
		}
		changePreference(menu,PREFERRED_LANGUAGE,checkedIcon);
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
        
        //final Consumer<NamedObject<Scene>> consumer = namedObject ->
                
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