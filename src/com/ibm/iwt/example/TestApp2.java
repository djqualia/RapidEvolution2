package com.ibm.iwt.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.border.LineBorder;

import com.ibm.iwt.IFrame;
import com.ibm.iwt.util.IWTUtilities;

/**
 * Tests changing the colors and sizes on the title bar.
 * @author MAbernethy
 */
public class TestApp2 extends IFrame
{
	public static void main(String[] args)
	{
		TestApp2 t = new TestApp2();
		t.setVisible(true);
	}
	
	public TestApp2()
	{
		IWTUtilities.setApplicationBorderSize(this, new Insets(3,3,3,3));
		setIContentPaneBorder(new LineBorder(Color.red, 3));
		setTitleBarHeight(35);
		setTitleBarBackground(Color.red);		
		setTitleBarButtonColors(Color.red, Color.white);
		setTitleBarButtonSize(new Dimension(26, 26));
		setTitle("Window");
	}
}
