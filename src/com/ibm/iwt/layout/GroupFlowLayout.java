
package com.ibm.iwt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.Vector;

import javax.swing.SwingConstants;

/**
 * GroupFlowLayout works identically to FlowLayout except it groups
 * Components into 3 groups, left, center, and right.  The groups
 * are aligned based on their group name - in other words, left is left-aligned,
 * center is center-aligned, and right is right-aligned.  It also takes an insets
 * object for padding around each Component.
 * @author MAbernethy
 */
public class GroupFlowLayout implements LayoutManager2
{			
	private Vector vctLeft = new Vector();
	private Vector vctCenter = new Vector();
	private Vector vctRight = new Vector(); 

	/**
	 * Adds a component to the layout manager.  
	 * @param comp the Component to be added
	 * @param constraints type GroupFlowLayoutConstraints that contains
	 * the Insets used as padding and the group name for the Component
	 * @see GroupFlowLayoutConstraints
	 */
	public void addLayoutComponent(Component comp, Object constraints)
	{
		if (!(constraints instanceof GroupFlowLayoutConstraints))
			throw new IllegalArgumentException("Argument must be of type GroupFlowLayoutConstraints");
			
		GroupFlowLayoutConstraints cons = (GroupFlowLayoutConstraints)constraints;
		if (cons.group == SwingConstants.LEFT)
		{
			vctLeft.add(new GroupFlowLayoutData(comp, cons));
		}
		else if (cons.group == SwingConstants.CENTER)
		{
			vctCenter.add(new GroupFlowLayoutData(comp, cons));
		}
		else if (cons.group == SwingConstants.RIGHT)
		{
			vctRight.add(new GroupFlowLayoutData(comp, cons));
		}
	}

	/**
	 * Returns the maximum size this layout manager will size for.
	 * @param target the Container this layout manager is responsible for
	 * @return the maxium size
	 */
	public Dimension maximumLayoutSize(Container target)
	{
		return new Dimension(target.getWidth(), target.getHeight());
	}

	/**
	 * Returns the layout alignment along the x axis this layout manager uses.
	 * <br>This layout always returns 0.
	 * @param target the Container this layout manager is responsible for
	 * @return the alignment
	 */
	public float getLayoutAlignmentX(Container target)
	{
		return 0;
	}
	
	/**
	 * Returns the layout alignment along the y axis this layout manager uses.
	 * <br>This layout always returns 0.
	 * @param target the Container this layout manager is responsible for
	 * @return the alignment
	 */
	public float getLayoutAlignmentY(Container target)
	{
		return 0;
	}

	/**
	 * Invalidates the layout.
	 * @param target the Container this layout manager is responsible for
	 */
	public void invalidateLayout(Container target)
	{
	}

	/**
	 * Adds a component to this layout using the name only.
	 * <br>This function is not supported by GroupFlowLayout.
	 * @param name the Component name
	 * @param comp the Component to be added
	 */
	public void addLayoutComponent(String name, Component comp)
	{
	}

	/**
	 * Removes a component from the layout and from the container 
	 * this layout manager is responsible for.
	 * @param comp the Component to be removed
	 */
	public void removeLayoutComponent(Component comp)
	{
		vctCenter.remove(comp);
		vctLeft.remove(comp);
		vctRight.remove(comp);
		layoutContainer(comp.getParent());
	}

	/**
	 * Returns the preferred layout size of this layout manager.
	 * @param parent the Container this layout manager is responsible for
	 * @return the preferred size
	 */
	public Dimension preferredLayoutSize(Container parent)
	{
		return new Dimension(parent.getWidth(), parent.getHeight());
	}

	/**
	 * Returns the minimum size this layout manager needs to layout its
	 * components properly.
	 * @param parent the Container this layout manager is responsible for
	 * @return the minimum size
	 */
	public Dimension minimumLayoutSize(Container parent)
	{
		Component[] c = parent.getComponents();
		int totalWidth = 0;
		int maxHeight = 0;
		for (int i=0; i<c.length; i++)
		{
			totalWidth += c[i].getWidth();
			if (c[i].getHeight() > maxHeight)
				maxHeight = c[i].getHeight();
		}
		totalWidth += parent.getInsets().left + parent.getInsets().right;
		maxHeight += parent.getInsets().top + parent.getInsets().bottom;
		return new Dimension(totalWidth, maxHeight);
	}

	/**
	 * Lays out the container with all the components that have been added to the 
	 * container.
	 * @param parent the Container this layout manager is responsible for
	 */
	public void layoutContainer(Container parent)
	{
		int x = parent.getInsets().left;
		int y = parent.getInsets().top;
		int width = parent.getWidth() - x - parent.getInsets().right;
		int height = parent.getHeight() - y - parent.getInsets().bottom;
		
		// left components
		int xPos = x;
		for (int i=0; i<vctLeft.size(); i++)
		{
			GroupFlowLayoutData data = (GroupFlowLayoutData)vctLeft.get(i);
			Component c = data.c;
			GroupFlowLayoutConstraints cons = data.cons;
			c.setSize(c.getPreferredSize().width, c.getPreferredSize().height);
			c.setLocation(xPos+cons.insets.left, (height/2)-(c.getPreferredSize().height/2));
			xPos = xPos + c.getPreferredSize().width + cons.insets.right;
		}
		
		// center components
		int centerPixels = 0;
		for (int i=0; i<vctCenter.size(); i++)
		{
			GroupFlowLayoutData data = (GroupFlowLayoutData)vctCenter.get(i);
			Component c = data.c;
			GroupFlowLayoutConstraints cons = data.cons;
			centerPixels = centerPixels + c.getPreferredSize().width + cons.insets.left + cons.insets.right;
		}
		xPos = width/2 - centerPixels/2;
		for (int i=0; i<vctCenter.size(); i++)
		{
			GroupFlowLayoutData data = (GroupFlowLayoutData)vctCenter.get(i);
			Component c = data.c;
			GroupFlowLayoutConstraints cons = data.cons;
			c.setSize(c.getPreferredSize().width, c.getPreferredSize().height);
			c.setLocation(xPos+cons.insets.left, (height/2)-(c.getPreferredSize().height/2));
			xPos = xPos + c.getPreferredSize().width + cons.insets.right;
		}
		
		// right components
		xPos = width;
		for (int i=vctRight.size()-1; i>-1; i--)
		{
			GroupFlowLayoutData data = (GroupFlowLayoutData)vctRight.get(i);
			Component c = data.c;
			GroupFlowLayoutConstraints cons = data.cons;
			c.setSize(c.getPreferredSize().width, c.getPreferredSize().height);
			c.setLocation(xPos-c.getPreferredSize().width-cons.insets.right, (height/2)-(c.getPreferredSize().height/2));
			xPos = xPos - c.getPreferredSize().width-cons.insets.left;
		}
	}
	
	private class GroupFlowLayoutData
	{
		public Component c;
		public GroupFlowLayoutConstraints cons;	
		
		public GroupFlowLayoutData(Component c, GroupFlowLayoutConstraints cons)
		{
			this.c = c;
			this.cons = cons;	
		}
	}

}
