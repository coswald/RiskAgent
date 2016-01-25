package com.sillysoft.tools;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

import java.util.*;
import java.util.List;
import java.net.URL;

// Needed for the saveComponentAsJPEG method
import java.io.*;
import com.sun.image.codec.jpeg.*;

import javax.imageio.*;	// for PNG writing method


public class GraphicsTool
{

static public final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
static public final Color HALF_TRANSPARENT_WHITE = new Color(1f, 1f, 1f, 0.5f);

static public final Color THICK_ARROW_COLOR = Color.WHITE;

// returns a new color from multiplying <color>'s alpha by <alpha>
public static Color transparent( Color color, float alpha )
	{
	if (color == null)
		return transparent(Color.WHITE, alpha);
		
	return new Color( color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha()*alpha));
	}

// returns an opaque version of <color>
public static Color solid(Color color)
	{
	return new Color( color.getRed(), color.getGreen(), color.getBlue());
	}


private static String supportFolderPath, modernResourcesLocation;
public static void initGraphicsTool(String supportFolderPath_set, String modernResourcesLocation_set)
	{
	supportFolderPath = supportFolderPath_set;
	modernResourcesLocation = modernResourcesLocation_set;
	}
	
// what about getmanaged?
public static Image getImageFromJAR(String name)
	{
	if (supportFolderPath == null)
		throw new NullPointerException("GraphicsTool.getImageFromJAR called before initGraphicsTool()");
		
	Image i;
	String path = supportFolderPath+"Mods"+File.separator+name;
	if (name.startsWith("Themes/"))
		{
		path = supportFolderPath+name;
		name = name.substring(name.lastIndexOf("/")+1);
		}
		
	if (new File(path).exists())
		{
		// load the modded file
		i = new ImageIcon(path).getImage();
		if (i != null)
			return i;
		}

	path = modernResourcesLocation+"Images"+File.separator+name;
	if (new File(path).exists())
		{
		// load the modded file
		i = new ImageIcon(path).getImage();
		if (i != null)
			return i;
		}
				
	try
		{
		i = new ImageIcon(GraphicsTool.class.getResource("/"+name)).getImage();
		return i;
		}
	catch(Exception e)
		{
		}

	System.out.println("GraphicsTool.getImageFromJAR() -> error loading "+name);
	
	BufferedImage buff = GraphicsTool.createBufferedImage(50, 50, Transparency.TRANSLUCENT);
	Graphics2D imageG = buff.createGraphics();
	imageG.setColor(Color.BLACK);
	imageG.fillRect(0,0,50,50);
	imageG.dispose();
	return buff;
	}

public static BufferedImage getBufferedImageFromJAR(String name)
	{
	try
		{
		String path = supportFolderPath+"Mods"+File.separator+name;
		File modFile = new File(path);
		if (modFile.exists())
			{
			// load the modded file
			return ImageIO.read(modFile);
			}

		// The normal case:
		URL url = GraphicsTool.class.getResource("/"+name);
		return ImageIO.read(url);
		}
	catch( Exception e)
		{
		System.out.println("GraphicsTool.getBufferedImageFromJAR() -> error loading "+name);
		}
	return null;
	}


public static ImageIcon getImageIconNamed(String name)
	{
	try
		{
		return new ImageIcon(GraphicsTool.class.getResource("/"+name));
		}
	catch( Exception e)
		{
		System.out.println("getImageIconNamed("+name+") failed");
		e.printStackTrace();
		}
	return new ImageIcon();
	}

/** Move any rectangles that overlap. Each call to this method will move intersection rects one unit away. Return true if something was moved, false if the rects no longer intersect at all.

Optional parameter: they are allowed to overlap by 'bufferSize' pixels. Default is 0.	*/
public static boolean arrangeRects(Rectangle[] rects, Rectangle bounds, int unit)
	{
	return arrangeRects(rects, bounds, unit, 0);
	}

static public boolean arrangeRects(Rectangle[] r, Rectangle bounds, int unit, int bufferSize)
	{
	boolean movedSomething = false;
	int n = r.length;
	int negBufferSize = -1 * bufferSize;
	for (int i = 0; i < n; i++)
		{
		for (int j = 0; j < n; j++)
			{
			if (i != j && GraphicsTool.intersectsRectMoreThanBuffer(r[i], r[j], bufferSize))
				{
				// move them both
				// move in the x direction
				if (r[i].getCenterX() > r[j].getCenterX())
					{
					// move r[i] to the right
					if (r[i].getMaxX() < bounds.getMaxX())
						r[i].translate(unit, 0);
					if (r[j].getX() > 0)
						r[j].translate(-unit, 0);
					}
				else
					{
					// move r[i] to the left
					if (r[i].getX() > 0)
						r[i].translate(-unit, 0);
					if (r[j].getMaxX() < bounds.getMaxX())
						r[j].translate(unit, 0);
					}
				// move in the y direction
				if (r[i].getCenterY() > r[j].getCenterY())
					{
					// move r[i] to a higher Y
					if (r[i].getMaxY() < bounds.getMaxY())
						r[i].translate(0, unit);
					if (r[j].getY() > 0)
						r[j].translate(0, -unit);
					}
				else
					{
					// move r[i] to a lower Y
					if (r[i].getY() > 0)
						r[i].translate(0, -unit);
					if (r[j].getMaxY() < bounds.getMaxY())
						r[j].translate(0, unit);
					}

				movedSomething = true;
				}
			}
		// now check shape i against the edges of the board:
		if (r[i].getY() < negBufferSize)
			{
			r[i].translate(0, unit);
			movedSomething = true;
			}
		else if (r[i].getMaxY() > bounds.getMaxY()+bufferSize)
			{
			r[i].translate(0, -unit);
			movedSomething = true;
			}
		if (r[i].getX() < negBufferSize)
			{
			r[i].translate(unit, 0);
			movedSomething = true;
			}
		else if (r[i].getMaxX() > bounds.getMaxX()+bufferSize)
			{
			r[i].translate(-unit, 0);
			movedSomething = true;
			}

		}

	//System.out.println("movedSomething="+movedSomething);
	return movedSomething;
	}

/** A test to see if the given rectangles intersect by more tah 'bufferSize' pixels. */
private static boolean intersectsRectMoreThanBuffer(Rectangle r1, Rectangle r2, int bufferSize)
	{
	if (bufferSize == 0)
		{
		return r1.intersects(r2);
		}

	Rectangle inter = r1.intersection(r2);

	return (inter.getWidth() > bufferSize && inter.getHeight() > bufferSize);
	}

/** Draw a string (using the font set in the Graphics object) inside of a box, centered at the given co-ordinants, with the given padding on all sides. */
public static void drawBoxedString(Graphics2D g, String text, int centeredAtX, int centeredAtY, int padding)
	{
	drawBoxedString(g, text, centeredAtX, centeredAtY, padding, Color.black, 1);
	}

/**
Draw the given text (using the font set in the Graphics object) centered at the point (centeredAtX, centeredAtY) along with a box that contains it. 'padding' specifies how many pixels of space to put around the string on each side. The color and width of the box's outline may be specified as well. */
public static void drawBoxedString(Graphics2D g, String text, int centeredAtX, int centeredAtY, int padding, Color outlineColor, int outlineWidth)
	{
	drawBoxedString(g, text, centeredAtX, centeredAtY, padding, padding, transparent(Color.white, 0.75f), outlineColor, outlineWidth, Color.black);
	}

/** An upgrading drawBoxedString where it is possible to use a different X and Y padding values, and to specify the color of the box insides. */
public static void drawBoxedString(Graphics2D g, String text, int centeredAtX, int centeredAtY, int paddingX, int paddingY, Color boxColor, Color outlineColor, int outlineWidth)
	{
	drawBoxedString(g, text, centeredAtX, centeredAtY, paddingX, paddingY, transparent(Color.white, 0.75f), outlineColor, outlineWidth, Color.black);
	}

/** An upgrading drawBoxedString where it is possible to use a different X and Y padding values, and to specify the color of the box insides. */
public static void drawBoxedString(Graphics2D g, String text, int centeredAtX, int centeredAtY, int paddingX, int paddingY, Color boxColor, Color outlineColor, int outlineWidth, Color textColor)
	{
	FontMetrics fontMetrics = g.getFontMetrics();
	int fontHeight = fontMetrics.getHeight();

	// Break the string at line-breaks and size the box according to the longest one
	String[] lines = new String[countLines(text)];
	int pos = 0;
	int boxWidth = 0;
	for (int i = 0; i < lines.length; i++)
		{
		int end = text.indexOf('\n', pos);
		if (end != -1)
			lines[i] = text.substring(pos, end);
		else
			lines[i] = text.substring(pos);
		pos += lines[i].length()+1;

		// HACK because MacOS X does not correctly judge the width of tabs
		// small hack to make the winning messages box not be too small
		if (i == 2)
			boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i])+20);
		else
			boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i]));
		}

	boxWidth += paddingX*2 + 1;
	int boxHeight = (fontHeight*lines.length)+(paddingY*2);

	int originX = centeredAtX - (boxWidth/2);
	int originY = centeredAtY - (boxHeight/2);

	int arcwidth = 15;
	if (fontHeight < 17)
		arcwidth = 8;
	if (fontHeight < 20)
		arcwidth = 12;	
	// fontHeights: 15 for 1 bonus, 17 for 2 bonus, 20 for 3 bonus
		
	RoundRectangle2D textBox = new RoundRectangle2D.Float(originX, originY, boxWidth, boxHeight, arcwidth, 15);
	g.setColor(boxColor);
	g.fill( textBox );
	g.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g.setColor(outlineColor);
	g.draw( textBox );

	// Now draw the text
	g.setColor(textColor);
	int textStartingY = originY + boxHeight - (paddingY + fontMetrics.getDescent());
	if (fontHeight > 24)
		textStartingY += 1;
	for (int i = 0; i < lines.length; i++)
		{
		g.drawString(lines[i], originX+paddingX+1, textStartingY-(fontHeight*(lines.length-(i+1))));
		}
	}


/** Return the Rectangle that contains where this boxed string would be drawn. */
public static Rectangle getBoxedStringRectangle(Font font, String text, int centeredAtX, int centeredAtY, int paddingX, int paddingY, Color boxColor, Color outlineColor, int outlineWidth, Color textColor)
	{
	FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
	int fontHeight = fontMetrics.getHeight();

	// Break the string at line-breaks and size the box according to the longest one
	String[] lines = new String[countLines(text)];
	int pos = 0;
	int boxWidth = 0;
	for (int i = 0; i < lines.length; i++)
		{
		int end = text.indexOf('\n', pos);
		if (end != -1)
			lines[i] = text.substring(pos, end);
		else
			lines[i] = text.substring(pos);
		pos += lines[i].length()+1;

		// HACK because MacOS X does not correctly judge the width of tabs
		// small hack to make the winning messages box not be too small
		if (i == 2)
			boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i])+20);
		else
			boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i]));
		}

	boxWidth += paddingX*2 + 1;
	int boxHeight = (fontHeight*lines.length)+(paddingY*2);

	int originX = centeredAtX - (boxWidth/2);
	int originY = centeredAtY - (boxHeight/2);

	Rectangle textBox = new Rectangle(originX, originY, boxWidth, boxHeight);
	return textBox;
	}


/** This method is like drawBoxedString, except the specified co-ordinates are the top left corner of where the box should be drawn. */
public static void drawBoxedStringCorneredAt(Graphics2D g, String text, int originX, int originY, int paddingX, int paddingY, Color boxColor, Color outlineColor, int outlineWidth, Color textColor, boolean engraveText)
	{
	FontMetrics fontMetrics = g.getFontMetrics();
	int fontHeight = fontMetrics.getHeight();

	// Break the string at line-breaks and size the box according to the longest one
	String[] lines = new String[countLines(text)];
	int pos = 0;
	int boxWidth = 0;
	for (int i = 0; i < lines.length; i++)
		{
		int end = text.indexOf('\n', pos);
		if (end != -1)
			lines[i] = text.substring(pos, end);
		else
			lines[i] = text.substring(pos);
		pos += lines[i].length()+1;
		boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i]));
		}

	boxWidth += paddingX*2 + 1;
	int boxHeight = (fontHeight*lines.length)+(paddingY*2);

//	Rectangle2D textBox = new Rectangle2D.Double(originX, originY, boxWidth, boxHeight);
	RoundRectangle2D textBox = new RoundRectangle2D.Float(originX, originY, boxWidth, boxHeight, 15, 15);
	g.setColor(boxColor);
	g.fill( textBox );
	g.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g.setColor(outlineColor);
	g.draw( textBox );

	// Now draw the text
	g.setColor(textColor);
	int textStartingY = originY + boxHeight + 1 - (paddingY + fontMetrics.getDescent());
	for (int i = 0; i < lines.length; i++)
		{
		if (engraveText)
			drawEngravedString(g, lines[i], originX+paddingX+1, textStartingY-(fontHeight*(lines.length-(i+1))));
		else
			g.drawString(lines[i], originX+paddingX+1, textStartingY-(fontHeight*(lines.length-(i+1))));
		}
	}
	
private static int countLines(String text)
	{
	if (text == null)
		return 0;

	int lines = 1;
	for (int i = 0; i < text.length(); i++)
		{
		i = text.indexOf('\n', i);
		if (i != -1)
			lines++;
		else
			break;
		}
	return lines;
	}


/** Save a component into a JPEG. Code taken from:
http://www.jguru.com/faq/view.jsp?EID=242020	*/
public static void saveComponentAsJPEG(Component myComponent, String filename, boolean thumbnail, int width, int height, boolean isVox) 
	{
//	Dimension size = myComponent.getSize();
	BufferedImage myImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	Graphics2D g2 = myImage.createGraphics();
	myComponent.paint(g2);
	g2.dispose();
		
	try 
		{
		ImageIO.write(myImage, "jpg", new File(filename+".jpg"));

		if (thumbnail)
			{
			if (! isVox)	// Vox uses mini-PNGs
				{
				// Get a thumbnail that fits into 170x140:
				float thumbScale = Math.min(170f / width, 140f / height);
				int thumbWidth = (int) (width*thumbScale);
				int thumbHeight = (int) (height*thumbScale);

				Image thumbnailImage;
				thumbnailImage = myImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_AREA_AVERAGING);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb.jpg"));
				}

/*						
			Image thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
			ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb.jpg"));

			thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, true);
			ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb2.jpg"));

			thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
			ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb3.jpg"));

			// this uses the old java 1 Image.getScaledInstance() call
			thumbnailImage = myImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_AREA_AVERAGING);
			ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb4.jpg"));

			// worked best on quartz ? shitty on leopard
			BufferedImage thumbnailBufferedImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_3BYTE_BGR);
			g2 = thumbnailBufferedImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);			
			g2.drawImage(myImage, 0, 0, thumbWidth, thumbHeight, null);
			g2.dispose();
			ImageIO.write(thumbnailBufferedImage, "jpg", new File(filename+"_thumb5.jpg"));

			thumbnailBufferedImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_3BYTE_BGR);
			g2 = thumbnailBufferedImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);			
			g2.drawImage(myImage, 0, 0, thumbWidth, thumbHeight, null);
			g2.dispose();
			ImageIO.write(thumbnailBufferedImage, "jpg", new File(filename+"_thumb6.jpg"));
*/
			
			// Get a thumbnail that fits 500 width
			if (width > 500)
				{
				int thumbWidth = (int) (width * (500f / width));
				int thumbHeight = (int) (height * (500f / width));
				
				// this uses the old java 1 Image.getScaledInstance() call
				Image thumbnailImage = myImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_AREA_AVERAGING);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb500.jpg"));				

				/*
				thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb500.jpg"));

				thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, true);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb500b.jpg"));

				thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb500c.jpg"));
			
				thumbnailBufferedImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_3BYTE_BGR);
				g2 = thumbnailBufferedImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);			
				g2.drawImage(myImage, 0, 0, thumbWidth, thumbHeight, null);
				g2.dispose();
				ImageIO.write(thumbnailBufferedImage, "jpg", new File(filename+"_thumb500e.jpg"));
				*/
				}
			
			// Get a thumbnail that fits 800 width
			if (width > 800)
				{
				int thumbWidth = (int) (width * (800f / width));
				int thumbHeight = (int) (height * (800f / width));
				
				// this uses the old java 1 Image.getScaledInstance() call
				Image thumbnailImage = myImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_AREA_AVERAGING);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb800.jpg"));	

				/*
				thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb800.jpg"));

				thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, true);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb800b.jpg"));

				thumbnailImage = getScaledInstance(myImage, thumbWidth, thumbHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
				ImageIO.write(toBufferedImage(thumbnailImage, null), "jpg", new File(filename+"_thumb800c.jpg"));

				thumbnailBufferedImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_3BYTE_BGR);
				g2 = thumbnailBufferedImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);			
				g2.drawImage(myImage, 0, 0, thumbWidth, thumbHeight, null);
				g2.dispose();
				ImageIO.write(thumbnailBufferedImage, "jpg", new File(filename+"_thumb800e.jpg"));
				*/
				}
			}
		}
	catch (Exception e) 
		{
		System.out.println("GraphicsTool.saveComponentAsJPEG: "+e); 
		}
	}  


public static void saveComponentAsPNG(Component myComponent, String filename, boolean thumbnail, int width, int height) 
	{
//	Dimension size = myComponent.getSize();
	BufferedImage myImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	Graphics2D g2 = myImage.createGraphics();
	myComponent.paint(g2);
	g2.dispose();
	try 
		{
		// save captured image to PNG file
		ImageIO.write(myImage, "png", new File(filename+".png"));

		if (thumbnail)
			{
			// Get a thumbnail that fits into 170x140:
			float thumbScaleWidth = 170f / width;
			float thumbScaleHeight = 140f / height;
			float thumbScale = Math.min(thumbScaleWidth, thumbScaleHeight);

			int thumbWidth = (int) (width*thumbScale);
			int thumbHeight = (int) (height*thumbScale);

			Image thumbnailImage = myImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_AREA_AVERAGING);
			ImageIO.write(toBufferedImage(thumbnailImage, null), "png", new File(filename+"_thumb.png"));
			}
		}
	catch (Exception e) 
		{
		System.out.println("GraphicsTool.saveComponentAsPNG: "+e); 
		}
	}  



public static void saveComponentAsMiniPNG(Component myComponent, String filename, int width, int height) 
	{
//	Dimension size = myComponent.getSize();
	BufferedImage myImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	Graphics2D g2 = myImage.createGraphics();
	myComponent.paint(g2);
	g2.dispose();
	try 
		{
		// save captured image to MINI PNG file
		// Get a thumbnail that fits into 170x140:
		float thumbScaleWidth = 170f / width;
		float thumbScaleHeight = 140f / height;
		float thumbScale = Math.min(thumbScaleWidth, thumbScaleHeight);

		int thumbWidth = (int) (width*thumbScale);
		int thumbHeight = (int) (height*thumbScale);

		Image thumbnailImage = myImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_AREA_AVERAGING);
		ImageIO.write(toBufferedImage(thumbnailImage, null), "png", new File(filename+"_thumb.png"));
		}
	catch (Exception e) 
		{
		System.out.println("GraphicsTool.saveComponentAsMiniPNG: "+e); 
		}
	}  

/** Take a GeneralPath and flip it vertically based on the given height. */
public static GeneralPath flipY(GeneralPath path, int height)
	{
	GeneralPath shape = new GeneralPath();
	PathIterator it = path.getPathIterator(new AffineTransform());
	float[] point = new float[2];
	int segType;

	while (! it.isDone())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_LINETO)
			shape.lineTo(point[0], height-point[1]);
		else if (segType == PathIterator.SEG_MOVETO)
			shape.moveTo(point[0], height-point[1]);
		else if (segType == PathIterator.SEG_CLOSE)
			shape.closePath();
		else
			System.out.println("GraphicsTool -> GraphicsTool.flipY() unknown segType: "+segType);
		it.next();
		}

	return shape;
	}

/** Take a String describing a point and return another string of the same point, but with the Y value flipped. */
public static String flipPointStringY(String pointString, int height)
	{
	if (pointString == null)
		return null;

	int commaLoc = pointString.indexOf(",");
	StringBuffer result = new StringBuffer();
	result.append(pointString.substring(0,commaLoc+1));
	result.append(height - Integer.parseInt(pointString.substring(commaLoc+1)));

	return result.toString();
	}


/** Give an XML representation of the given GeneralPath (and flipping the y values of points). It will be made up of one or more 'polygon' tags.  */
public static String getGeneralPathXML(GeneralPath path, int height)
	{
	StringBuffer result = new StringBuffer();
	PathIterator it = path.getPathIterator(new AffineTransform());
	float[] point = new float[2];
	int segType;

	boolean isOpen = false;	// track to end with a </polygon>
	while (! it.isDone())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_LINETO)
			{
			result.append(" ");
			result.append((int) point[0]);
			result.append(",");
			result.append(height-(int)point[1]);
			}
		else if (segType == PathIterator.SEG_MOVETO)
			{
			isOpen = true;
			result.append("\t\t<polygon>");
			result.append((int) point[0]);
			result.append(",");
			result.append(height-(int)point[1]);
			}
		else if (segType == PathIterator.SEG_CLOSE)
			{
			if (isOpen)
				{
				isOpen = false;
				result.append("</polygon>\n");
				}
			}
		else
			System.out.println("GraphicsTool -> GraphicsTool 23846 unknown segType: "+segType);
		it.next();
		}

	if (isOpen)
		result.append("</polygon>\n");

	return result.toString();
	}

/** Return the distance the given point is to the closest segment of the Rectangle. */
public static double distanceFromPointToRect(Point2D p, Rectangle r)
	{
	if (r.contains(p))
		return 0;
		
	Line2D line;
	int distance = 10000000;
	
	line = new Line2D.Double(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMinY());
	distance = Math.min(distance, (int) Math.abs( line.ptLineDist(p) ));

	line = new Line2D.Double(r.getMinX(), r.getMinY(), r.getMinX(), r.getMaxY());
	distance = Math.min(distance, (int) Math.abs( line.ptLineDist(p) ));
	
	line = new Line2D.Double(r.getMinX(), r.getMaxY(), r.getMaxX(), r.getMaxY());
	distance = Math.min(distance, (int) Math.abs( line.ptLineDist(p) ));
	
	line = new Line2D.Double(r.getMaxX(), r.getMinY(), r.getMaxX(), r.getMaxY());
	distance = Math.min(distance, (int) Math.abs( line.ptLineDist(p) ));

	return distance;	
	}

/** Return the distance the given point is to the closest segment of the GeneralPath. */
public static double distanceFromPointToGeneralPath(Point p, GeneralPath path)
	{
	double closestSoFar = -1;

	PathIterator it = path.getPathIterator(new AffineTransform());
	float[] currentPoint = new float[2], previousPoint = null, firstPoint = new float[2];
	int segType;
	while (! it.isDone())
		{
		segType = it.currentSegment(currentPoint);

		if (segType == PathIterator.SEG_MOVETO)
			{
			// it's a move, so set the first point (in the polygon) and ditch the last point
			firstPoint[0] = currentPoint[0];
			firstPoint[1] = currentPoint[1];

			previousPoint = null;
			}
		else if (segType == PathIterator.SEG_CLOSE)
			{
			// UNTESTED XXXX
			// Leave the previous point alone, but use the previous moveto point as the current point
			currentPoint[0] = firstPoint[0];
			currentPoint[1] = firstPoint[1];
			}
		else if (segType != PathIterator.SEG_LINETO)
			{
			throw new RuntimeException("GraphicsTool.distanceFromPointToGeneralPath has not been implemented for all GeneralPaths segment types.");
			}

		// If we have a line segment then check the distance
		if (previousPoint != null)
			{
			Line2D segment = new Line2D.Float(previousPoint[0], previousPoint[1], currentPoint[0], currentPoint[1]);
			double distToSegment = segment.ptSegDist(p);
			if (closestSoFar == -1 || distToSegment < closestSoFar)
				closestSoFar = distToSegment;
			}

		if (previousPoint == null)
			previousPoint = new float[2];
		previousPoint[0] = currentPoint[0];
		previousPoint[1] = currentPoint[1];
		it.next();
		}

	return closestSoFar;
	}


/** Return the distance the given point is to the closest segment of the GeneralPath. */
public static double distanceFromPointToGeneralPath(Point2D p, GeneralPath path)
	{
	double closestSoFar = -1;

	PathIterator it = path.getPathIterator(new AffineTransform());
	float[] currentPoint = new float[2], previousPoint = null, firstPoint = new float[2];
	int segType;
	while (! it.isDone())
		{
		segType = it.currentSegment(currentPoint);

		if (segType == PathIterator.SEG_MOVETO)
			{
			// it's a move, so set the first point (in the polygon) and ditch the last point
			firstPoint[0] = currentPoint[0];
			firstPoint[1] = currentPoint[1];

			previousPoint = null;
			}
		else if (segType == PathIterator.SEG_CLOSE)
			{
			// UNTESTED XXXX
			// Leave the previous point alone, but use the previous moveto point as the current point
			currentPoint[0] = firstPoint[0];
			currentPoint[1] = firstPoint[1];
			}
		else if (segType != PathIterator.SEG_LINETO)
			{
			throw new RuntimeException("GraphicsTool.distanceFromPointToGeneralPath has not been implemented for all GeneralPaths segment types.");
			}

		// If we have a line segment then check the distance
		if (previousPoint != null)
			{
			Line2D segment = new Line2D.Float(previousPoint[0], previousPoint[1], currentPoint[0], currentPoint[1]);
			double distToSegment = segment.ptSegDist(p);
			if (closestSoFar == -1 || distToSegment < closestSoFar)
				closestSoFar = distToSegment;
			}

		if (previousPoint == null)
			previousPoint = new float[2];
		previousPoint[0] = currentPoint[0];
		previousPoint[1] = currentPoint[1];
		it.next();
		}

	return closestSoFar;
	}
	
	
/** Give a String representation of the given GeneralPath. 
NOTE: for now it will only give the first polygon if there are many in the shape. */
public static String stringFromGeneralPath(GeneralPath path)
	{
	StringBuffer result = new StringBuffer();
	PathIterator it = path.getPathIterator(new AffineTransform());
	float[] point = new float[2];
	int segType;

	while (! it.isDone())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_LINETO)
			{
			result.append(" ");
			result.append((int) point[0]);
			result.append(",");
			result.append((int)point[1]);
			}
		else if (segType == PathIterator.SEG_MOVETO)
			{
			result.append((int) point[0]);
			result.append(",");
			result.append((int)point[1]);
			}
		else if (segType == PathIterator.SEG_CLOSE)
			return result.toString();
		else
			System.out.println("GraphicsTool -> GraphicsTool 23846 unknown segType: "+segType);
		it.next();
		}

	return result.toString();
	}


/** Give a String representation of the given GeneralPath with a flipped Y. 
NOTE: for now it will only give the first polygon if there are many in the shape. */
public static String stringFromGeneralPath(GeneralPath path, int height)
	{
	StringBuffer result = new StringBuffer();
	PathIterator it = path.getPathIterator(new AffineTransform());
	float[] point = new float[2];
	int segType;

	while (! it.isDone())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_LINETO)
			{
			result.append(" ");
			result.append((int) point[0]);
			result.append(",");
			result.append(height-(int)point[1]);
			}
		else if (segType == PathIterator.SEG_MOVETO)
			{
			result.append((int) point[0]);
			result.append(",");
			result.append(height-(int)point[1]);
			}
		else if (segType == PathIterator.SEG_CLOSE)
			return result.toString();
		else
			System.out.println("GraphicsTool -> GraphicsTool 23846 unknown segType: "+segType);
		it.next();
		}

	return result.toString();
	}


private static int nextColorCounter = 0;
/** This method will generate a series of Color objects with an attempt at making them contrast with each other. The series will always be the same, and will repeat when all colors have been used. */
public static Color getNextColor()
    {
	Color[] possibleColors = new Color[] {	
			new Color(0.0f, 0.0f, 0.5f, 0.77f),		// dark blue
			new Color(0.5f, 0.0f, 0.0f, 0.77f),		// brown-red
			new Color(0.0f, 0.5f, 0.0f, 0.77f),		// green

			new Color(1.0f, 1.0f, 0.0f, 0.77f),		// yellow
			new Color(1.0f, 0.0f, 0.75f, 0.77f),	// pink
			new Color(0.4f, 0.4f, 0.4f, 0.77f),		// grey
		
			new Color(1.0f, 0.5f, 0.0f, 0.77f),		// orange
			new Color(0.0f, 0.75f, 1.0f, 0.77f),	// light blue
			new Color(0.5f, 0.0f, 0.5f, 0.77f),		// purple

			new Color(0.7f, 0.7f, 0.7f, 0.77f),		// light grey
			
			new Color(1f, 0f, 0f, 0.77f),			// bright red
			new Color(0f, 1f, 0f, 0.77f),			// green
			new Color(0.0f, 0.0f, 0.0f, 0.77f),		// black
			};
    
    Color next = possibleColors[nextColorCounter];
    nextColorCounter = (nextColorCounter+1) % possibleColors.length;
    return next;
    }

/** Return The color WHITE or BLACK, whichever provides more contrast to the given color. */
public static Color getContrastingBase(Color versus)
	{
//	SS.debug("getContrastingBase: "+versus);
	float brightness = Color.RGBtoHSB(versus.getRed(), versus.getGreen(), versus.getBlue(), null)[2];
	if (brightness > 0.5)
		return Color.BLACK;
	else
		return Color.WHITE;
	}

/** Return The color WHITE or BLACK, whichever provides more contrast to the given color. */
public static Color getContrastingBase(Color versus, float brightnessForBlack)
	{
//	SS.debug("getContrastingBase2: "+versus+" - "+brightnessForBlack);
	if (versus == null)
		return Color.BLACK;
		
	float brightness = Color.RGBtoHSB(versus.getRed(), versus.getGreen(), versus.getBlue(), null)[2];
	if (brightness > brightnessForBlack)
		return Color.BLACK;
	else
		return Color.WHITE;
	}


// get the center point of shape <s>
public static Point getCenterOfShape ( Shape shape )
	{
    Rectangle bounds = shape.getBounds();
    
	Point center = new Point((int)bounds.getCenterX(), (int)bounds.getCenterY());
	if (shape.contains( center ))
		return center;

	// Otherwise this is a wacky shape. Try some other points:
	center = new Point( bounds.x + (int) (bounds.getWidth()/4), (int) bounds.getCenterY() );
	if (shape.contains( center ))
		return center;
	center = new Point( bounds.x + (int) (bounds.getWidth()*3/4), (int) bounds.getCenterY() );
	if (shape.contains( center ))
		return center;
	center = new Point( (int) bounds.getCenterX(), bounds.y + (int) (bounds.getHeight()/4) );
	if (shape.contains( center ))
		return center;
	center = new Point( (int) bounds.getCenterX(), bounds.y + (int) (bounds.getHeight()*3/4) );
	if (shape.contains( center ))
		return center;

	// Still no luck finding a good center, just return the middle of the bounds... xxxx
	return new Point( (int) bounds.getCenterX(), (int) bounds.getCenterY() );
	}

static public String stringFromColor(Color color)
    {
	if (color == null)
		{
		SS.debug("GraphicsTool.stringFromColor(null) !! Sending back BLACK");
		return stringFromColor(Color.BLACK);
		}
		
    return (color.getRed()/255.0)+"/"+(color.getGreen()/255.0)+"/"+(color.getBlue()/255.0);
    }

static public Color colorFromString( String defsString )
	{    
	if (defsString == null)
		return null;

	try
		{
		StringTokenizer tok = new StringTokenizer((String)defsString, "/");
		return new Color( Float.parseFloat(tok.nextToken()), Float.parseFloat(tok.nextToken()), Float.parseFloat(tok.nextToken()), 1.0f);
		}
	catch (Exception e)
		{
		SS.debug("GraphicsTool.colorFromString failed with input of: "+defsString+" -> "+e);
		return Color.BLACK;
		}
	}


public static Point pointFromString(String loc)
    {
	if (loc == null || "null".equals(loc))
		return null;

	int commaLoc = loc.indexOf(",");
	if (commaLoc == -1)
		{
		SS.debug("GraphicsTool.pointFromString("+loc+") failed.");
		Thread.dumpStack();
		return new Point(0, 0);
		}
	return new Point(Integer.parseInt(loc.substring(0,commaLoc)), Integer.parseInt(loc.substring(commaLoc+1)));
    }

public static String stringFromPoint(Point p)
    {
	if (p == null)
		return null;

	return p.x+","+p.y;
    }


// Draw the text in the bottom corner inside a box.
// This isn't used by LuxView, since it also needs a timer sometimes.
public static void drawHelperText(Graphics2D g, String helperText, int height)
	{
	if (helperText == null)
		return;

	FontMetrics fontMetrics = g.getFontMetrics();

	// Break the string at line-breaks and size the box according to the longest one
	String[] lines = new String[countLines(helperText)];
	int pos = 0;
	int boxWidth = 0;
	for (int i = 0; i < lines.length; i++)
		{
		int end = helperText.indexOf('\n', pos);
		if (end != -1)
			lines[i] = helperText.substring(pos, end);
		else
			lines[i] = helperText.substring(pos);
		pos += lines[i].length()+1;
		boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i]));
		}

	int padding = 4;
	int xpadding = 20;
	boxWidth += xpadding;
	int boxHeight = (fontMetrics.getHeight()*lines.length)+padding;

	int x = 1;
	int y = (height-6)-(fontMetrics.getHeight()*lines.length);

	Rectangle2D textBox = new Rectangle2D.Double(x, y, boxWidth, boxHeight);
	g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g.setColor(Color.black);
	g.draw( textBox );
	g.setColor(GraphicsTool.transparent(Color.white, 0.75f));
	g.fill( textBox );
	g.setColor(Color.black);
	for (int i = 0; i < lines.length; i++)
		{
		g.drawString(lines[i], x+(xpadding/2), (y-1)+(fontMetrics.getHeight()*(i+1)));
		}
	}

/** Draw an arrow.
Code taken from http://forum.java.sun.com/thread.jsp%3Fthread%3D378460%26forum%3D57%26message%3D2752293+java+draw+arrow&hl=en
*/
public static void drawArrow(Graphics2D g2d, int fromX, int fromY, int toX, int toY, float arrowheadSize, float lineWidth) 
	{
	double aDir=Math.atan2(fromX - toX, fromY - toY);

	g2d.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
	g2d.drawLine(toX+xCor(3,aDir), toY+yCor(3,aDir), fromX-xCor(3,aDir), fromY-yCor(3,aDir));

	g2d.setStroke(new BasicStroke(1f));                        // make the arrow head solid even if dash pattern has been specified
	Polygon tmpPoly=new Polygon();
	int i1=12+(int)(arrowheadSize*2);
	int i2=6+(int)arrowheadSize;                                          // make the arrow head the same size regardless of the length length
	tmpPoly.addPoint(toX,toY);                                     // arrow tip
	tmpPoly.addPoint(toX+xCor(i1,aDir+.5),toY+yCor(i1,aDir+.5));
	tmpPoly.addPoint(toX+xCor(i2,aDir),toY+yCor(i2,aDir));
	tmpPoly.addPoint(toX+xCor(i1,aDir-.5),toY+yCor(i1,aDir-.5));
	tmpPoly.addPoint(toX,toY);                                     // arrow tip
//Color oldColor=g2d.getColor();
//g2d.setColor(Color.BLACK);
	g2d.drawPolygon(tmpPoly);
//g2d.setColor(oldColor);
	g2d.fillPolygon(tmpPoly);                                      // remove this line to leave arrow head unpainted
	}
public static int yCor(int len, double dir) {return (int)(len * Math.cos(dir));}
public static int xCor(int len, double dir) {return (int)(len * Math.sin(dir));}

public static void drawArrowBetweenPoints(Graphics2D g, Point2D p1, Point2D p2, boolean doubleArrow, int arrowheadSize, int lineWidth)
	{
	drawArrow(g, (int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), arrowheadSize, lineWidth);
	if (doubleArrow)
		drawArrow(g, (int) p2.getX(), (int) p2.getY(), (int) p1.getX(), (int) p1.getY(), arrowheadSize, lineWidth);
	}

/** Get a point just inside the Shape along the given line. The 'to' point must be inside the shape for this to work. */
public static Point getContainedPointAlongLine(Point from, Point to, Shape shape)
	{
	// Walk along the direction of the line until we get a point inside the shape
	double aDir = Math.atan2(to.x - from.x, to.y - from.y);
	for(int distance = 1; true; distance++)
		{
		Point checkPoint = new Point(from.x + GraphicsTool.xCor(distance, aDir), from.y + GraphicsTool.yCor(distance, aDir));
		if (shape.contains(checkPoint))
			return checkPoint;
		}
	}

public static void drawDoubleArrowConnectingShapes(Graphics2D g, Shape s1, Shape s2, boolean doubleArrow)
	{
	Point from = getCenterOfShape(s1);
	Point to = getCenterOfShape(s2);
	double aDir = Math.atan2(to.getX() - from.getX(), to.getY() - from.getY());

	// Walk along the line and find the points just inside each of the 2 shapes
	/*
	Point borderPoint1 = null, borderPoint2 = null;
	for(int distance = 1; borderPoint1 == null || borderPoint2 == null; distance++)
		{
		Point checkPoint = new Point(from.x + GraphicsTool.xCor(distance, aDir), from.y + GraphicsTool.yCor(distance, aDir));
		if (s1.contains(checkPoint))
			borderPoint1 = checkPoint;
		if (s2.contains(checkPoint))
			borderPoint2 = checkPoint;
		}
		*/

	// Now get a line segment of the proper length based on the midpoint of the 2 end points found:
	Point midPoint = midpoint(from, to);
//	Point midPoint = midpoint(borderPoint1, borderPoint2);
	int distance = 15;
	from = new Point(midPoint.x - GraphicsTool.xCor(distance, aDir), midPoint.y - GraphicsTool.yCor(distance, aDir));
	to = new Point(midPoint.x + GraphicsTool.xCor(distance, aDir), midPoint.y + GraphicsTool.yCor(distance, aDir));

	drawArrowBetweenPoints(g, from, to, doubleArrow, 1, 2);
	}



public static Point midpoint(Point2D a, Point2D b) 
	{
	int x = (int) ((a.getX() + b.getX()) / 2.0);
	int y = (int) ((a.getY() + b.getY()) / 2.0);

	return new Point(x, y);
	} 

public static Point2D midpoint2D(Point2D a, Point2D b) 
	{
	double x = ((a.getX() + b.getX()) / 2.0);
	double y = ((a.getY() + b.getY()) / 2.0);

	return new Point2D.Double(x, y);
	} 

/** This version uses the basket of points approach using the getMidpointBetweenShapes() method. */
public static void drawDoubleArrowConnectingShapesBAD(Graphics2D g, Shape s1, Shape s2)
	{
	Point2D from = new Point(), to = new Point();
	Point midPoint = getMidpointBetweenShapes(s1, s2, from, to);

	double aDir = Math.atan2(to.getX() - from.getX(), to.getY() - from.getY());

	int distance = 15;
	from = new Point(midPoint.x + GraphicsTool.xCor(distance, aDir), midPoint.y + GraphicsTool.yCor(distance, aDir));
	to = new Point(midPoint.x - GraphicsTool.xCor(distance, aDir), midPoint.y - GraphicsTool.yCor(distance, aDir));

	drawArrowBetweenPoints(g, from, to, true, 1, 2);
	}

/** This method returns the a Point between the 2 shapes that is the closest to them both. The last 2 Point2D parameters will be filled with the 2 points that the midpoint was taken from. */ 
public static Point getMidpointBetweenShapes(Shape s1, Shape s2, Point2D shape1point, Point2D shape2point)
	{
	float[] point = new float[2], point2 = new float[2];
	Point result = null;
	double closestDistance = 1000000;

	// Double loop through the points comparing their distances all to each other
	for (PathIterator it = s1.getPathIterator(new AffineTransform()); ! it.isDone(); it.next())
		{
		it.currentSegment(point);
		Point2D p1 = new Point2D.Float(point[0], point[1]);

		int basketSize = 3, basketMiddle = 1;
		// Compare each shape1 point against a basket of adjoining points from shape2.
		// This way we will find the middle of an area where they are close.
		float[][] shape2basket = new float[basketSize][2];
		PathIterator it2 = s2.getPathIterator(new AffineTransform());
		// Start by grabbing the first few points. Leave the first basket spot open so we can shift into it
		for (int i = 1; i < basketSize; i++)
			it2.currentSegment(shape2basket[i]);
		float[][] firstPoints = new float[basketSize-1][2];
		// we must remember the first 2 points for when we wrap around the end of the shape
		for (int i = 0; i < basketSize-1; i++)
			{
			firstPoints[i][0] = shape2basket[i+1][0];
			firstPoints[i][1] = shape2basket[i+1][1];
			}

		for (; ! it2.isDone(); it2.next())
			{
			// move the basket over by one
			for (int i = 0; i < basketSize-1; i++)
				{
				shape2basket[i][0] = shape2basket[i+1][0];
				shape2basket[i][1] = shape2basket[i+1][1];
				}
			// grab the next point
			it2.currentSegment(shape2basket[basketSize-1]);

			double distance = 0;
			for (int i = 0; i < basketSize; i++)
				distance += p1.distance(new Point2D.Float(shape2basket[i][0], shape2basket[i][1]));

			if (distance < closestDistance)
				{
				closestDistance = distance;
				shape1point.setLocation(p1);
				// use the middle point of the basket
				shape2point.setLocation(new Point2D.Float(shape2basket[basketMiddle][0], shape2basket[basketMiddle][1]));
				result = midpoint(p1, shape2point);
				}
			}

		// We finished looping over shape 2's points. Now do a few more using the combination of end and start points
		for (int i = 0; i < basketSize-1; i++)
			{
			// xxxx
			}
		}


/*		do
			{
			} while (! it2.isDone());

		for (PathIterator it2 = s2.getPathIterator(new AffineTransform()); ! it2.isDone(); it2.next())
			{

			it2.currentSegment(point2);
			Point2D p2 = new Point2D.Float(point2[0], point2[1]);

			if (p1.distance(p2) < closestDistance)
				{
				closestDistance = p1.distance(p2);
				shape1point.setLocation(p1);
				shape2point.setLocation(p2);

				result = midpoint(p1, p2);
				}
			}
		}		*/
	return result;
	}

/** Create and return a BufferedImage of the desired size and transparency that is compatible with the screen. */
public static BufferedImage createBufferedImage (int width, int height, int transparency)
	{
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice gs = ge.getDefaultScreenDevice();
	GraphicsConfiguration gc = gs.getDefaultConfiguration();

	return gc.createCompatibleImage(width, height, transparency);
	}


public static GeneralPath resizeShape(Shape shape, double percent)
	{
	GeneralPath result = new GeneralPath();

	float[] point = new float[2];
	int segType;
	for (PathIterator it = shape.getPathIterator(new AffineTransform()); ! it.isDone(); it.next())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_LINETO)
			result.lineTo((float) (point[0]*percent), (float) (point[1]*percent));
		else if (segType == PathIterator.SEG_MOVETO)
			result.moveTo((float) (point[0]*percent), (float) (point[1]*percent));
		else if (segType == PathIterator.SEG_CLOSE)
			result.closePath();
		else
			System.out.println("GraphicsTool -> GraphicsTool.resizeShape() unknown segType: "+segType);
		}

	return result;
	}


/** Get the resize percentage to fit the starting dimension size into the fitInto dimension size, while maintaining the width:height ratio. */
public static double getResizePercentToFitInsideDimension(int startingWidth, int startingHeight, double fitIntoWidth, double fitIntoHeight)
	{
	double widthPercentResize = fitIntoWidth/startingWidth;
	double heightPercentResize = fitIntoHeight/startingHeight;

//	SS.debug("getResizePercent("+startingWidth+", "+startingHeight+", "+fitIntoWidth+", "+fitIntoHeight+") = "+Math.min(widthPercentResize, heightPercentResize));

	return Math.min(widthPercentResize, heightPercentResize);
	}


/** Draw a collection of ExtraLines using their width and color. */
public static void drawLines(Graphics2D g, List lines, Color defaultColor)
	{
	if (lines == null)
		return;
		
	for (int i = 0; i < lines.size(); i++)
		{
		ExtraLine line = (ExtraLine) lines.get(i);
		if (line.color != null)
			g.setColor(line.color);
		else
			g.setColor(defaultColor);

		line.draw(g);
		}
	}



/** Take an Image and return the same as an BufferedImage. */ 
public static BufferedImage toBufferedImage(Image image, GraphicsConfiguration gc) {
//    if (image instanceof BufferedImage)
//        return (BufferedImage) image;
//    loadImage(image, new Label());
    int w = image.getWidth(null);
    int h = image.getHeight(null);
    BufferedImage result = createBufferedImage(w, h, Transparency.OPAQUE);
    Graphics2D g = result.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return result;
}

 

public static GraphicsConfiguration getDefaultConfiguration() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    return gd.getDefaultConfiguration();
	}

public static void drawShadowedString(Graphics2D g, String text, int x, int y)
	{
	g.setColor(new Color(0, 0, 0, 128));
	g.drawString(text, x+2, y+2 );

	g.setColor(new Color(220, 220, 220));
	g.drawString(text, x, y );
	}

public static void drawEngravedString(Graphics2D g, String text, int x, int y)
	{
	g.setColor(new Color(220, 220, 220, 128));
	g.drawString(text, x+1, y+1 );

	g.setColor(Color.BLACK);
	g.drawString(text, x, y );
	}

public static void drawOutlinedString(Graphics2D g, String text, int x, int y)
	{
	g.setColor(new Color(220, 220, 220, 128));
	g.drawString(text, x+1, y+1 );
	g.drawString(text, x+1, y-1 );
	g.drawString(text, x-1, y+1 );
	g.drawString(text, x-1, y-1 );

	g.setColor(Color.BLACK);
	g.drawString(text, x, y );
	}
		
		
public static String wrapString(String text, int pixelWidth, Font font)
	{
	FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);

	/** Recursive: */
	// If the whole string is not too long then return it:
	if (fontMetrics.stringWidth(text) < pixelWidth)
		return text;
	else
		{
		// Strip off words from the end until we have a string that is short enough	
		int lastLength = text.length();
		String shorterText = StringTool.stripLastWord(text);
		while (fontMetrics.stringWidth(shorterText) > pixelWidth)
			{
			if (shorterText.length() == lastLength)
				{
				// No word was stripped. The shorterText must now be all one word
				if (lastLength == text.length())
					{
					// it was ALL one word. return it all
					return text;
					}
					
				return shorterText + "\n" + wrapString(text.substring(shorterText.length()+1), pixelWidth, font);
				}

			lastLength = shorterText.length();
			shorterText = StringTool.stripLastWord(shorterText);
			}
		return shorterText + "\n" + wrapString(text.substring(shorterText.length()+1), pixelWidth, font);
		}
	}
	
/*
public static void main(String[] args)
	{
	System.out.println("GraphicsTool main test...");
	JFrame frame = new JFrame("GraphicsTool test");
	JPanel testPanel = new JPanel()
		{
		public void paintComponent(Graphics badg) 
			{
			Graphics2D g = (Graphics2D) badg;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Make the background black:
			g.setColor(Color.RED);
//			g.fill(g.getClipBounds());
			
			GraphicsTool.drawArrowBetweenPoints(g, new Point(10, 20), new Point(10, 100), true, 2, 2);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(30, 20), new Point(30, 100), true, 3, 2);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(50, 20), new Point(50, 100), true, 4, 2);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(70, 20), new Point(70, 100), true, 5, 2);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(90, 20), new Point(90, 100), true, 6, 2);

			GraphicsTool.drawArrowBetweenPoints(g, new Point(10, 220), new Point(10, 300), true, 2, 2);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(30, 220), new Point(30, 300), true, 2, 3);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(50, 220), new Point(50, 300), true, 2, 4);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(70, 220), new Point(70, 300), true, 2, 5);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(90, 220), new Point(90, 300), true, 2, 6);

			GraphicsTool.drawArrowBetweenPoints(g, new Point(10, 420), new Point(10, 500), true, 2, 2);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(30, 420), new Point(30, 500), true, 3, 3);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(50, 420), new Point(50, 500), true, 4, 4);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(70, 420), new Point(70, 500), true, 5, 5);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(90, 420), new Point(90, 500), true, 6, 6);
			
			
			g.setColor(Color.BLACK);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(190, 19), new Point(190, 101), false, 4, 4);
			g.setColor(Color.RED);
			GraphicsTool.drawArrowBetweenPoints(g, new Point(190, 20), new Point(190, 100), false, 2, 2);
			
			
			
			System.out.println("--\n"+wrapString("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww a lot of love fun", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString(" a lot of wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww love fun", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString("a lot of love fun wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString("a lot of love fun more wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww a lot of love fun wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww ", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww a lot of   love fun", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString(" wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww a lot of love fun", 100, g.getFont())+"\n--");
			System.out.println("--\n"+wrapString("  wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww a lot of love fun", 100, g.getFont())+"\n--");
			}
		};
	frame.setContentPane(testPanel);
	frame.setSize(800, 600);
	frame.show();
	}
*/	

public static void drawArrowImageConnectingShapes(Graphics2D g, Shape s1, Shape s2, boolean doubleArrow)
	{
	Point from = getCenterOfShape(s1);
	Point to = getCenterOfShape(s2);
	drawArrowImageConnectingPoints(g, from, to, doubleArrow);
	}
	
public static void drawArrowImageConnectingPoints(Graphics2D g, Point from, Point to, boolean doubleArrow)
	{
	// wraparound special cases, ie alaska to kam
	if (from.x < 150 && to.x > 700)
		to = new Point(0, to.y);
	else if (to.x < 150 && from.x > 700)
		to = new Point(from.x+200, to.y);

	double aDir = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());

	// Get a midpoint to draw the arrow at:
	Point2D midPoint = midpoint2D(from, to);	

//g.drawLine(from.x, from.y, to.x, to.y);
//g.drawOval((int)midPoint.getX(), (int)midPoint.getY(), 3, 3);

	if (doubleArrow)
		drawArrowImageDouble(g, midPoint.getX(), midPoint.getY(), aDir);
	else
		drawArrowImage(g, midPoint.getX(), midPoint.getY(), aDir);
	}	
	
public static void drawArrowImage(Graphics2D g, double x, double y, double aDir)
	{
	AffineTransform at = AffineTransform.getTranslateInstance(x-17, y-9);
	at.concatenate(AffineTransform.getRotateInstance(aDir, 17, 9));
	g.drawImage(getManagedImage("arrow05.png"), at, null);
	}

public static void drawArrowImageDouble(Graphics2D g, double x, double y, double aDir)
	{
	AffineTransform at = AffineTransform.getTranslateInstance(x-20, y-9);
	at.concatenate(AffineTransform.getRotateInstance(aDir, 20, 9));
/*
	if (g.getColor() == Color.GRAY)
		g.drawImage(arrowImageDoubleGray, at, null);
	else
		*/
		g.drawImage(getManagedImage("arrow05_double.png"), at, null);

	}

// WARNING: This fails on images that do not yet know their width and height
public static BufferedImage convertToBufferedImage(Image im)
	{
	BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics bg = bi.getGraphics();
    bg.drawImage(im, 0, 0, null);
    bg.dispose();
    return bi;
	}
	
public static JPanel getNonOpaqueJPanel()
	{
	JPanel panel = new JPanel();
	panel.setOpaque(false);
	return panel;
	}

public static JPanel getNonOpaqueJPanel(LayoutManager lm)
	{
	JPanel panel = new JPanel(lm);
	panel.setOpaque(false);
	return panel;
	}



/* 
 *  function returns 'numberOfPoints' points from the circumference
 * of ellipse described by radii 'a' and 'b'.
 * at origin x, y
 * Assumes 'numberOfPoints' is greater than zero.
 * 'numberOfPoints' should be at least 4 to sample polygon from ellipse
 */
 
public static List getEllipsePoints(int x, int y, double a, double b, int numberOfPoints)
	{
    double theta = 0.0;
    double deltheta = 2 * Math.PI / numberOfPoints;
    int i;
	List points = new Vector();

//    ps = N_NEW(numberOfPoints, pointf);
    for (i = 0; i < numberOfPoints; i++) 
		{
		Point p = new Point(x + (int) (a * Math.cos(theta)), y + (int) (b * Math.sin(theta)));
		points.add(p);
        theta += deltheta;
		}
    return points;
	}


/** Take a String with one or more 'polygon' tag and returns the GeneralPath it creates. */
public static GeneralPath getGeneralPathFromXML(String pathXML)
	{
	GeneralPath shape = new GeneralPath();
	String poly;
	StringTokenizer tok;
	int firstX, firstY;
	for (int s = 0; (poly = XMLTool.extract("polygon", pathXML, s)) != null; s++)
		{
		tok = new StringTokenizer(poly, " ,");
		firstX = Integer.parseInt(tok.nextToken());
		firstY = Integer.parseInt(tok.nextToken());
		shape.moveTo(firstX, firstY);
		while (tok.hasMoreTokens())
			{
			shape.lineTo(Integer.parseInt(tok.nextToken()),Integer.parseInt(tok.nextToken()));
			}
		shape.lineTo(firstX, firstY);
		shape.closePath();
		}
	return shape;
	}

/** Take a string with a list of points and returns the GeneralPath it creates. 
Limited to 1 polygon.		*/
public static GeneralPath generalPathFromString(String pathXML)
	{
	GeneralPath shape = new GeneralPath();
	String poly = pathXML;
	StringTokenizer tok;
	int firstX, firstY;


		tok = new StringTokenizer(poly, " ,");
		firstX = Integer.parseInt(tok.nextToken());
		firstY = Integer.parseInt(tok.nextToken());
		shape.moveTo(firstX, firstY);
		while (tok.hasMoreTokens())
			{
			shape.lineTo(Integer.parseInt(tok.nextToken()),Integer.parseInt(tok.nextToken()));
			}
		shape.lineTo(firstX, firstY);
		shape.closePath();

	return shape;
	}
	
	
// get the center point of shape <s>
public static Point getCenterOfGeneralPath ( GeneralPath path )
	{
    Rectangle bounds = path.getBounds();
    
	Point center = new Point((int)bounds.getCenterX(), (int)bounds.getCenterY());
	if (path.contains( center ))
		return center;

	// Otherwise this is a wacky shape. Try some other points:
	center = new Point( bounds.x + (int) (bounds.getWidth()/4), (int) bounds.getCenterY() );
	if (path.contains( center ))
		return center;
	center = new Point( bounds.x + (int) (bounds.getWidth()*3/4), (int) bounds.getCenterY() );
	if (path.contains( center ))
		return center;
	center = new Point( (int) bounds.getCenterX(), bounds.y + (int) (bounds.getHeight()/4) );
	if (path.contains( center ))
		return center;
	center = new Point( (int) bounds.getCenterX(), bounds.y + (int) (bounds.getHeight()*3/4) );
	if (path.contains( center ))
		return center;

	// Still no luck finding a good center, just return the middle of the bounds... xxxx
	return new Point( (int) bounds.getCenterX(), (int) bounds.getCenterY() );
	}
	
public static Polygon getThickArrowPolygon(Point from, Point to)
	{
	return getThickArrowPolygon(from.x, from.y, to.x, to.y);
	}

public static Polygon getThickArrowPolygon(int fromX, int fromY, int toX, int toY)
	{
	// l1 gives the length away from the tip the first arrow head is
	// l2 gives the length away from the tip that the second point away from the tip is
	// l4 gives the length away from the _tail_ point that the tail flap is
	// These lengths stay the same regardless of the length of the line
	// d1,d2,d3 give the direction away from their reference points
	// a direction of 0 is the direction pointing from the tip to the tail
	int l1 = 24;
	double d1 = 0.45;
	
	int l2 = 18;
	double d2 = 0.3;
	int l22 = 20;
	double d22 = 0.16;
	
	int l3 = 12;
	double d3 = 0.5;
	
	double aDir = Math.atan2(fromX - toX, fromY - toY);
	Polygon poly = new Polygon();
	poly.addPoint(toX, toY);												// arrow tip (to start)
	poly.addPoint(toX+xCor(l1, aDir+d1), toY+yCor(l1, aDir+d1));			// tip side-edge 1
	poly.addPoint(toX+xCor(l2, aDir+d2), toY+yCor(l2, aDir+d2));			// stem top 1
	poly.addPoint(toX+xCor(l22, aDir+d22), toY+yCor(l22, aDir+d22));	// stem top corner 1 - added
	
	poly.addPoint(fromX+xCor(l3, aDir+d3), fromY+yCor(l3, aDir+d3));		// stem bottom 1
	poly.addPoint(fromX, fromY);											// arrow tail
	poly.addPoint(fromX+xCor(l3, aDir-d3), fromY+yCor(l3, aDir-d3));		// stem bottom 2
	
	poly.addPoint(toX+xCor(l22, aDir-d22), toY+yCor(l22, aDir-d22));	// stem top corner 2 - added
	poly.addPoint(toX+xCor(l2, aDir-d2), toY+yCor(l2, aDir-d2));			// stem top 2
	poly.addPoint(toX+xCor(l1, aDir-d1), toY+yCor(l1, aDir-d1));			// tip side-edge 2
	poly.addPoint(toX, toY);												// arrow tip (to close)
	
	return poly;
	}
	
	
public static void drawThickArrow(Graphics2D g2d, int fromX, int fromY, int toX, int toY)
	{
	Polygon poly = getThickArrowPolygon(fromX, fromY, toX, toY);
	g2d.fillPolygon(poly);                       

	g2d.setColor(THICK_ARROW_COLOR);
	g2d.drawPolygon(poly);
	}

public static JLabel getJLabelWithFont(String foo, Font font)
	{
	JLabel label = new JLabel(foo);
	label.setFont(font);
	return label;
	}


private static Hashtable managedImages, managedImagesColored;
public static Image getManagedImage(String filename)
	{
	if (managedImages == null)
		managedImages = new Hashtable();
		
	Object stored = managedImages.get(filename);
	if (stored != null)
		return (Image) stored;
		
	Image loaded = getImageFromJAR(filename);	
	managedImages.put(filename, loaded);
	return loaded;
	}
	
public static void releaseManagedImage(String filename)
	{
	if (managedImages != null)
		managedImages.remove(filename);
	}

public static void releaseManagedImages()
	{
	for ( Enumeration e = managedImages.keys() ; e.hasMoreElements() ; )
		{
		// retrieve the object_key
		String object_key = (String) e.nextElement();
		managedImages.remove(object_key);
		}
	}

public static void releaseManagedImagesColored()
	{
	for ( Enumeration e = managedImagesColored.keys() ; e.hasMoreElements() ; )
		{
		// retrieve the object_key
		String object_key = (String) e.nextElement();
		managedImagesColored.remove(object_key);
		}
	}
	
public static void printManagedImageList()
	{
	SS.debug("managedImage List:");
	for ( Enumeration e = managedImages.keys() ; e.hasMoreElements() ; )
		{
		// retrieve the object_key
		String object_key = (String) e.nextElement();
		Image object = (Image) managedImages.get ( object_key );
		SS.debug("managedImage = "+object);
		}
	SS.debug("managedImage List End.");
	}

public static Image getManagedImageColored(String filename, Color color, float transparent)
	{
	return getManagedImageColored(filename, GraphicsTool.transparent(color, transparent));
	}
	
public static Image getManagedImageColored(String filename, Color color)
	{
	if (managedImagesColored == null)
		managedImagesColored = new Hashtable();
		
	Object stored = managedImagesColored.get(filename+color.hashCode());
	if (stored != null)
		return (Image) stored;
		
	Image loaded = getImageFromJAR(filename);
	loaded = paintColorOverImage(loaded, color);
	managedImagesColored.put(filename+color.hashCode(), loaded);
	return loaded;
	}


public static Image paintColorOverImage(Image sourceImage, Color paintColor)
	{
	return paintColorOverImage(sourceImage.getWidth(null), sourceImage.getHeight(null), sourceImage, paintColor);
	}
	
public static Image paintColorOverImage(int width, int height, Image sourceImage, Color paintColor)
	{
	BufferedImage coloredImage = GraphicsTool.createBufferedImage(width, height, Transparency.TRANSLUCENT);
	Graphics2D imageG = coloredImage.createGraphics();
	imageG.drawImage(sourceImage, 0, 0, null);
	imageG.setComposite(AlphaComposite.SrcAtop);
	imageG.setColor(paintColor);
	imageG.fillRect(0,0,width,height);
	imageG.dispose();
	return coloredImage;
	}

public static Image paintColorOverImage(int width, int height, Image sourceImage, Color paintColor, float transparent)
	{
	return paintColorOverImage(width, height, sourceImage, GraphicsTool.transparent(paintColor, transparent));
	}


/**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}. Taken from:
	 * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage img,
                                           int targetWidth,
                                           int targetHeight,
                                           Object hint,
                                           boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }


			
static public Image getBubbleFor(int count, Color color)
	{
	float transLevel = .55f;	// used to be .35f
	int showNum = count;
	if (showNum < 10)
		return GraphicsTool.getManagedImageColored("bubbleSource1.png", color, transLevel);
	else if (showNum < 100)
		return GraphicsTool.getManagedImageColored("bubbleSource2.png", color, transLevel);
	else if (showNum < 1000)
		return GraphicsTool.getManagedImageColored("bubbleSource3.png", color, transLevel);
	else if (showNum < 10000)
		return GraphicsTool.getManagedImageColored("bubbleSource4.png", color, transLevel);
	else if (showNum < 100000)
		return GraphicsTool.getManagedImageColored("bubbleSource5.png", color, transLevel);
	else if (showNum < 1000000)
		return GraphicsTool.getManagedImageColored("bubbleSource4.png", color, transLevel);
	else
		return GraphicsTool.getManagedImageColored("bubbleSource5.png", color, transLevel);		
	}

public static Font getSymbolFont()
	{
	if (! SS.isMacintosh && ! SS.isVista)
		return getDejaFontSized(14);	// Windows XP or Linux

	return null;
	}
	
public static Font getSymbolFontForBonus(Graphics2D g, int bonus)
	{	
	int positiveBonus = Math.abs(bonus);
	
	if (! SS.isMacintosh && ! SS.isVista)
		return getDejaFontSized(10+(positiveBonus*2));	// Windows XP or Linux
//		return new Font("DejaVu Sans Condensed", Font.PLAIN, 10+(positiveBonus*2));
		
//	SS.debug("getSymbolFontForBonus Mac/Vista using: "+g.getFont().getName());	
//	gives "SansSerif"
	return new Font(g.getFont().getName(), Font.PLAIN, 10+(positiveBonus*2));
	}


private static Map fontCache = new HashMap();

static public Font getDejaFontSized(int pointSize)
	{	
	Object font = fontCache.get(new Integer(pointSize));
	if (font != null)
		return (Font) font;
		
	// Not in the cache yet. Create and store it:	
//	SS.debug("Creating aquabutton font sized: "+ pointSize);

	Font vFont;
	try
		{
		vFont = Font.createFont(Font.TRUETYPE_FONT, GraphicsTool.class.getResourceAsStream("/DejaVuSans.ttf"));
		vFont = vFont.deriveFont(Font.PLAIN, pointSize);
		}
	catch (Exception e)
		{
		SS.debug("Unable to load the desired font DejaVuSans.ttf sized "+pointSize+". Will use Arial instead.");
        e.printStackTrace();
		vFont = new Font("Arial", Font.PLAIN, pointSize);
		}

	fontCache.put(new Integer(pointSize), vFont);
	return vFont;
	}



public static Dimension getDimensionOfText(String text, FontMetrics fontMetrics)
	{
	// Break the string at line-breaks
	String[] lines = new String[countLines(text)];
	int pos = 0;
	int boxWidth = 0;
	for (int i = 0; i < lines.length; i++)
		{
		int end = text.indexOf('\n', pos);
		if (end != -1)
			lines[i] = text.substring(pos, end);
		else
			lines[i] = text.substring(pos);
		pos += lines[i].length()+1;

		boxWidth = Math.max(boxWidth, fontMetrics.stringWidth(lines[i]));
		}

	int fontHeight = fontMetrics.getHeight();
	int boxHeight = (fontHeight*lines.length);

	return new Dimension(boxWidth, boxHeight);
	}
}
