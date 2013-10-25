package com.ibm.iwt.layout;

import java.awt.Insets;

import javax.swing.SwingConstants;

/**
 * Specifies constraints used for laying out components in the GroupFlowLayout
 * @author MAbernethy 
 */
public class GroupFlowLayoutConstraints
{	
	/** The group the component should be grouped with */
	public int group = SwingConstants.LEFT;
	/** The insets used for padding around each component */
	public Insets insets;
	
	/**
	 * Creates a GroupFlowLayoutConstraints object in the left group
	 * with a 3 inset all around.
	 */
	public GroupFlowLayoutConstraints()
	{
		this(SwingConstants.LEFT, new Insets(3,3,3,3));
	}
	
	/**
	 * Creates a GroupFlowLayoutConstaint object in the specified group
	 * with the specified padding around the component.
	 * @param group the group the component is to be grouped with
	 * @param insets the padding around the component
	 */
	public GroupFlowLayoutConstraints(int group, Insets insets)
	{
		if (group != SwingConstants.RIGHT && group != SwingConstants.CENTER && group != SwingConstants.LEFT)
			throw new IllegalArgumentException("Inappropriate Argument.  Must be SwingConstant.LEFT, CENTER, or RIGHT");
		this.group = group;
		this.insets = insets;	
	}
}
