package com.ibm.iwt.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

import com.ibm.iwt.IFrame;
import com.ibm.iwt.util.IWTUtilities;
import com.ibm.iwt.util.PaintUtilities;
import com.ibm.iwt.window.IWindowTitleBar;

/**
 * Tests transparency and everything together.
 * @author MAbernethy
 */
public class TestApp4 extends IFrame
{
	public static void main(String[] args)
	{
		TestApp4 t = new TestApp4();
		t.setVisible(true);
	}
	
	public TestApp4()
	{
		super();
		setTitle("Window");
		IWTUtilities.setApplicationBorderSize(this, new Insets(0,7,7,7));
		IWTUtilities.setDiagonalSize(20);
		getIContentPane().setBorder(new AppBorder());
		getIContentPane().setBackground(new Color(255, 255, 102));
		setTitleBar(new TitlePanel());		
	}
		
	private class TitlePanel extends IWindowTitleBar
	{	
		public TitlePanel()
		{
			setPreferredSize(new Dimension(800,35));
			setFont(new Font("Verdana", Font.BOLD, 22));
			removeWindowDecorations();
		}
		
		protected boolean isInsideTitleBar(int x, int y)
		{
			if (x < (int)getWidth()*.1 || x > (int)getWidth()*.9)
				return false;
			return true;
		}
		
		protected void isMouseOnBorder(int x, int y)
		{
			if (y > 10 && y > 16 && !isInsideTitleBar(x, y))
				isMouseOnBorder = true;
			else if (isInsideTitleBar(x, y) && y<3)
				isMouseOnBorder = true;
			else
				isMouseOnBorder = false;
		}
		
		public boolean isTransparent(int x, int y)
		{
			if ( (x < (int)getWidth()*.1 || x > (int)getWidth()*.9) &&
				y < 10)
				return true;
			return false;
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			int w = getWidth()-1;
			int h = getHeight()-1;
			
			g.setColor(new Color(255, 255, 102));
			g.fillRect(7, 18, w-14, h-17);
			
			g.setColor(Color.black);
			drawBorder(g, 0, 0, w, h);
			g.setColor(new Color(51, 102, 153));
			drawBorder(g, 1, 1, w, h);
			g.setColor(new Color(102, 153, 204));
			drawBorder(g, 2, 2, w, h);
			drawBorder(g, 3, 3, w, h);
			g.setColor(new Color(153, 204, 255));
			drawBorder(g, 4, 4, w, h);
			g.setColor(new Color(204, 255, 255));
			drawBorder(g, 5, 5, w, h);
			g.setColor(new Color(151, 183, 159));
			drawBorder(g, 6, 6, w, h);
			
			setTransparent(this, g, 0, 0, w+1, 10);

			PaintUtilities.paintDropShadow(g, (int)(w*.1), 0, (int)(w*.8), 27);
			Color c1 = new Color(67, 118, 135);
			Color c2 = new Color(105, 152, 199);
			PaintUtilities.paintGradient(g, (int)(w*.1), 0, (int)(w*.9), 14, c1, c2);
			PaintUtilities.paintGradient(g, (int)(w*.1), 14, (int)(w*.9), 13, c2, c1);

			Graphics2D g2 = (Graphics2D)g;
			if (rapid_evolution.RapidEvolution.aaEnabled)
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.white);
			int strW = SwingUtilities.computeStringWidth(g.getFontMetrics(), getTitle());
			int strH = g.getFontMetrics().getMaxAscent();
			g2.drawString(getTitle(), w/2-strW/2, h-strH/2);
            if (rapid_evolution.RapidEvolution.aaEnabled)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		
		private void drawBorder(Graphics g, int x, int y, int w, int h)
		{
			g.drawLine(x, 10+y, x, h);
			g.drawLine(x, 10+y, w-x, 10+y);
			g.drawLine(w-x, 10+y, w-x, h);
		}
		
	}
	
	private class AppBorder extends AbstractBorder
	{	
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			g.setColor(Color.black);
			g.drawLine(0, 0, 0, height-1);
			g.drawLine(0, height-1, width-1, height-1);
			g.drawLine(width-1, 0, width-1, height-1);
			g.setColor(new Color(51, 102, 153));
			g.drawLine(1, 0, 1, height-2);
			g.drawLine(1, height-2, width-2, height-2);
			g.drawLine(width-2, 0, width-2, height-2);
			g.setColor(new Color(102, 153, 204));
			g.drawLine(2, 0, 2, height-3);
			g.drawLine(2, height-3, width-3, height-3);
			g.drawLine(width-3, 0, width-3, height-3);
			g.drawLine(3, 0, 3, height-4);
			g.drawLine(3, height-4, width-4, height-4);
			g.drawLine(width-4, 0, width-4, height-4);
			g.setColor(new Color(153, 204, 255));			
			g.drawLine(4, 0, 4, height-5);
			g.drawLine(4, height-5, width-5, height-5);
			g.drawLine(width-5, 0, width-5, height-5);
			g.setColor(new Color(204, 255, 255));			
			g.drawLine(5, 0, 5, height-6);
			g.drawLine(5, height-6, width-6, height-6);
			g.drawLine(width-6, 0, width-6, height-6);
			g.setColor(new Color(151, 183, 159));			
			g.drawLine(6, 0, 6, height-7);
			g.drawLine(6, height-7, width-7, height-7);
			g.drawLine(width-7, 0, width-7, height-7);
			
		}
	}
}
