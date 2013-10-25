package com.ibm.iwt.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.SwingConstants;

/**
 * A collection of utility methods used for painting more detailed graphics.
 * @author MAbernethy
 */
public class PaintUtilities
{
	
	/**
	 * Paints a rectangular drop shadow with a 5 pixel offset.  The bounds passed
	 * into the function should be the actual area that is to be shadowed.  The
	 * function calculates the shadow's coordinates itself.
	 * @param g the Graphics instance
	 * @param x the x position of the component
	 * @param y the y position of the component
	 * @param width the width of the component
	 * @param height the height of the component
	 */
	public static void paintDropShadow(Graphics g, int x, int y, int width, int height)
	{
		paintDropShadow(g, x, y, width, height, 5);
	}
	
	/**
	 * Paints a rectangular drop shadow with the specified pixel offset.  The bounds passed
	 * into the function should be the actual area that is to be shadowed.  The
	 * function calculates the shadow's coordinates itself.
	 * @param g the Graphics instance
	 * @param x the x position of the component
	 * @param y the y position of the component
	 * @param width the width of the component
	 * @param height the height of the component
	 * @param offset the offset of the shadow in pixels
	 */
	public static void paintDropShadow(Graphics g, int x, int y, int width, int height, int offset)
	{
		Graphics2D g2 = (Graphics2D)g;
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int xOff = x+offset;
		int yOff = y+offset;
		g2.setColor(new Color(45, 45, 45, 80));
		g2.fillRoundRect(xOff, yOff, width, height, offset, offset);
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	/**
	 * Paints a smooth gradient in a rectangle given the bounds x, y, width, height.  
	 * This defaults to drawing a horizontal gradient.
	 * @param g the Graphics instance
	 * @param x the x position
	 * @param y the y position
	 * @param width the width of the region
	 * @param height the height of the region
	 * @param startColor the start color of the gradient
	 * @param endColor the end color of the gradient
	 */
	public static void paintGradient(Graphics g, int x, int y, int width, int height, Color startColor, Color endColor)
	{
		paintGradient(g, x, y, width, height, startColor, endColor, SwingConstants.HORIZONTAL);
	}	
	
	/**
	 * Paints a smooth gradient in a rectangle given the bounds x, y, width, height and orientation. 
	 * @param g the Graphics instance
	 * @param x the x position
	 * @param y the y position
	 * @param width the width of the region
	 * @param height the height of the region
	 * @param startColor the start color of the gradient
	 * @param endColor the end color of the gradient
	 * @param orientation the orientation of the gradient - either horizontal or vertical
	 */
	public static void paintGradient(Graphics g, int x, int y, int width, int height, Color startColor, Color endColor, int orientation) 
	{
		double radix = height;
		if (orientation == SwingConstants.VERTICAL)
			radix = width;
		double redDelta = (endColor.getRed() - startColor.getRed()) / radix;
		double greenDelta =	(endColor.getGreen() - startColor.getGreen()) / radix;
		double blueDelta = (endColor.getBlue() - startColor.getBlue()) / radix;
		double alphaDelta =	(endColor.getAlpha() - startColor.getAlpha()) / radix;
		Color c = startColor;
		double currentRed = redDelta;
		double currentBlue = blueDelta;
		double currentGreen = greenDelta;
		double currentAlpha = alphaDelta;
		int end = height;
		if (orientation == SwingConstants.VERTICAL)
			end = width;
		for (int i=0; i < end; i++) 
		{
			g.setColor(c);
			if (orientation == SwingConstants.HORIZONTAL)
				g.drawLine(x, y+i, width, y+i);
			else
				g.drawLine(x+i, y, x+i, height);
			int red = (int) (startColor.getRed() + currentRed);
			int green = (int) (startColor.getGreen() + currentGreen);
			int blue = (int) (startColor.getBlue() + currentBlue);
			int alpha = (int) (startColor.getAlpha() + currentAlpha);
			currentRed = currentRed + redDelta;
			currentBlue = currentBlue + blueDelta;
			currentGreen = currentGreen + greenDelta;
			currentAlpha = currentAlpha + alphaDelta;
			c = new Color(red, green, blue, alpha);
		}
	}
}
