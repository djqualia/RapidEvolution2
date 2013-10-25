
package com.ibm.iwt.util;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

import com.ibm.iwt.IFrame;

/**
 * The IWTUtilities class contains all the needed computations and numbers
 * needed for using with the IFrame.
 */
public class IWTUtilities
{
	/** the diagonal size of the borders - the number of pixels along
	 * each border where the diagonal cursor should appear, such as the 
	 * NE cursor of the SW cursor */
	public static int DIAGONAL_RESIZE_SIZE = 15;
	
	private static int screenHeight = 768;
	private static int screenWidth = 1024;
	
	/**
	 * Returns the diagonal size of the border.  The diagonal size is defined as
	 * the number of pixels along
	 * each border where the diagonal cursor should appear, such as the 
	 * NE cursor of the SW cursor.
	 * @return int the size in pixels
	 */
	public static int getDiagonalSize()
	{
		return DIAGONAL_RESIZE_SIZE;	
	}
	
	/**
	 * Returns the restore size for the window given its current size.  The restore
	 * size is defined as the size of the window when the window is at full size
	 * and the "Restore Down" button is pressed and the user has not
	 * manually set the restore size yet.
	 * @return Dimension the restore size
	 */
	public static Dimension getRestoreSize()
	{
		int RESTORE_WIDTH = 1024;
		int RESTORE_HEIGHT = 768;
		if (screenWidth == 1024)
			RESTORE_WIDTH = 800;
		else if (screenWidth == 800)
			RESTORE_WIDTH = 640;
		else if (screenWidth == 640)
			RESTORE_WIDTH = 640;
			
		if (screenHeight == 768)
			RESTORE_HEIGHT = 600;
		else if (screenHeight == 600)
			RESTORE_HEIGHT = 480;
		else if (screenHeight == 480)
			RESTORE_HEIGHT = 480;
			
		return new Dimension(RESTORE_WIDTH, RESTORE_HEIGHT);
	}
	
	/**
	 * Returns the maximum height available for an application window
	 * in this OS.
	 * @return the maximum height in pixels
	 */
	public static int getScreenHeight() 
	{
		return screenHeight;
	}
	
	/**
	 * Gets the maximum working screen size.  This function differs
	 * from the <code>Toolkit.getScreenSize()</code> by allowing for the space
	 * that any permanent fixtures
	 * that native application windows do not have access to in a given OS.  As
	 * an example, the space for the toolbar in Windows - native applications do
	 * not get that pixel area for their use.
	 * @param window the window that will use the screen 
	 * @return the size of the available screen in pixels
	 */
	public static Dimension getScreenSize(Window window)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		GraphicsConfiguration gc = window.getGraphicsConfiguration();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		screenSize.width = screenSize.width - insets.left - insets.right;
		screenSize.height = screenSize.height - insets.top - insets.bottom;
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
    	return screenSize;
	}

	/**
	 * Returns the maximum width available for an application window
	 * in this OS.
	 * @return the maximum width in pixels
	 */
	public static int getScreenWidth() 
	{
		return screenWidth;
	}
	
	/**
	 * Returns whether the window is maximized or not, based on the size
	 * of the window and the screen size.
	 * @param w the specified window
	 * @return whether the window is maximized
	 */
	public static boolean isWindowMaximized(Window w)
	{
		return (w.getSize().equals(getScreenSize(w)));
	}
	
	/**
	 * A convenience method for setting a border around the entire application window.
	 * If the border is a standard rectangular border, this method will set the 
	 * borders on the title bar and the icontent pane.
	 * @param frame the iframe to set the border to
	 * @param borderSize the size of the border in pixels
	 */
	public static void setApplicationBorderSize(IFrame frame, Insets borderSize)
	{
		frame.getTitleBar().setBorderSize(new Insets(borderSize.top, borderSize.left, 0, borderSize.right));
		frame.getIContentPane().setBorderSize(new Insets(0, borderSize.left, borderSize.bottom, borderSize.right));
	}
	
	/**
	 * Sets the diagonal size of the border.  The diagonal size is defined as
	 * the number of pixels along
	 * each border where the diagonal cursor should appear, such as the 
	 * NE cursor of the SW cursor.
	 * @param size the size in pixels
	 */
	public static void setDiagonalSize(int size)
	{
		DIAGONAL_RESIZE_SIZE = size;	
	}

}
