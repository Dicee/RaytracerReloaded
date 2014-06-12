package test;


/**Classe gerant l'affichage d'une image dans un JPanel
 * @author David Courtinot
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePane extends JPanel {
	
	protected int largeur, hauteur;
	protected BufferedImage buffer; 	
		
	public void paintComponent(Graphics g) {
	   g.drawImage(buffer,0,0,null);
	 }
	
	 /**Construire un ImagePane a partir d'une scene, d'une vue, et des coordonnees du coin haut gauche de l'image
	  * dans le referentiel de son parent.
	  * @param scene scene a calculer
	  * @param vue vue a afficher
	  * @param x premiere coordonnee du coin haut gauche de l'image dans le referentiel de son parent
	  * @param y deuxieme coordonnee du coin haut gauche de l'image dans le referentiel de son parent
	  */
	public ImagePane(BufferedImage im) 
	{
		super();						
		this.largeur     = im.getWidth();
		this.hauteur     = im.getHeight();
		this.buffer      = im;	
		this.setSize(new Dimension(largeur,hauteur));
	}
	
	
	public ImagePane(String string) throws IOException {
		this(ImageIO.read(ImagePane.class.getResource(string)));
	}

	/**Acceder aux dimensions du composant graphique.
	 * @return dimensions du de l'image
	 */
	public Dimension getDimension()	{
		return new Dimension(hauteur,largeur);
	}
}
