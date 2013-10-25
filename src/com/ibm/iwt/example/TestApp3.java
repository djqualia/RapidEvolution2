package com.ibm.iwt.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ibm.iwt.IFrame;
import com.ibm.iwt.layout.GroupFlowLayoutConstraints;
import com.ibm.iwt.util.IWTUtilities;
import com.ibm.iwt.util.PaintUtilities;
import com.ibm.iwt.window.IWindowButton;
import com.ibm.iwt.window.IWindowTitleBar;

/**
 * Tests subclasses of the IWindowTitleBar.
 * @author MAbernethy
 */
public class TestApp3 extends IFrame
{
	public static void main(String[] args)
	{
		TestApp3 t = new TestApp3();
		t.setVisible(true);
		t.setTitle("Window");
	}
	
	public TestApp3()
	{
		IWTUtilities.setApplicationBorderSize(this, new Insets(3,3,3,3));
		getIContentPane().setBorder(new LineBorder(Color.red, 3));
		setTitleBar(new TitleBar());
	}
	
	private class TitleBar extends IWindowTitleBar implements ChangeListener
	{
		private Color c = new Color(0,0,0);
		private JSlider slider;
		
		public TitleBar()
		{
			setPreferredSize(new Dimension(0, 26));
			removeWindowDecorations();
			addWindowButton(IWindowButton.CLOSE, SwingConstants.LEFT);
			setWindowButtonColors(Color.RED, Color.WHITE);
			addTitle(getTitle(), SwingConstants.CENTER, new Font("Verdana", Font.BOLD, 14), Color.WHITE);
			slider = new JSlider();
			add(slider, new GroupFlowLayoutConstraints(SwingConstants.RIGHT, new Insets(3,3,3,3)));
			slider.addChangeListener(this);
			slider.setMaximum(255);
			slider.setMinimum(0);
			slider.setOpaque(false);
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			PaintUtilities.paintGradient(g, 0, 0, getWidth(), getHeight(), c, Color.WHITE, SwingConstants.VERTICAL);
		}

		public void stateChanged(ChangeEvent e)
		{
			c = new Color(slider.getValue(), 0, 0);
			repaint();
		}
	}
}
