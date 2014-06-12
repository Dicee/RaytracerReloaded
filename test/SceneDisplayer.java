package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import objects.Cube;
import objects.Sphere;
import objects.Texture;
import scene.Pixel;
import scene.Raytracer;
import scene.Screen;
import scene.Source;
import utils.math.Point;
import utils.math.Vector3D;

/**
 *
 * @author Dici
 */
public class SceneDisplayer extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        try {
            //primaryStage.show();
            computeScene();
        } catch (IOException ex) {
            Logger.getLogger(SceneDisplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private void computeScene() throws IOException {
        scene.Scene scene  = new scene.Scene(new Color(0.5,0.5,0.5,1),1);
        Sphere      sphere = new Sphere(new Point(0,60,60),40,Texture.DEFAULT_TEXTURE);
        Cube    	cube   = new Cube(new Point(),50,Texture.DEFAULT_TEXTURE);
        Screen      screen = new Screen(new Point(180,120,0),new Point(180,0,0),new Point(180,0,120),new Point(400,0,30));
        Source      source = new Source(new Color(0.5,0.5,0.5,1),new Point(150,20,10));
        Source      source2 = new Source(new Color(1,1,1,1),new Point(150,-20,10));
        cube.rotateXYZ(5,17,30);
        cube.translate(new Vector3D(0,130,60));
        scene.setSources(Arrays.asList(source,source2));
        scene.setObjects(Arrays.asList(sphere,cube));
        
        Raytracer raytracer = new Raytracer(scene,screen);
        raytracer.render(0);
        
        screen.save(new File("test.png"),"png");
        
        Color[][] colors = screen.getColors();					
		
		int longueur = colors.length;
		int largeur  = colors.length;			
		
		BufferedImage img = new BufferedImage(longueur,largeur,BufferedImage.TYPE_INT_RGB);
		      System.out.println("coucou");
		for (int i=0; i<longueur; i++) 
			for (int j=0; j<largeur; j++) 				
				img.setRGB(i,largeur-j-1,Pixel.toRGB(colors[i][j]));
        //new ImageView();
        JFrame frame = new JFrame();
        frame.setContentPane(new ImagePane(img));
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //System.out.println(ImageIO.write(img, "png",new File("test.png")));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
