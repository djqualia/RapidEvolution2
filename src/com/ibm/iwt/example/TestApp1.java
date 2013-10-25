package com.ibm.iwt.example;

import com.ibm.iwt.IFrame;

/**
 * Test of Default IFrame.
 * @author MAbernethy
 */
public class TestApp1 extends IFrame
{
	public static void main(String[] args)
	{
		TestApp1 t = new TestApp1();
		t.setVisible(true);
	}
	
	public TestApp1()
	{
		setTitle("Window");
	}
}
