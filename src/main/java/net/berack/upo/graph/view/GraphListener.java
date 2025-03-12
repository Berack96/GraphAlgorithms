package net.berack.upo.graph.view;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * An interface for creating a listener of the Graph.
 *
 * @author Berack96
 */
public interface GraphListener extends MouseListener, MouseMotionListener, KeyListener {
	
	/**
	 * Remove the listener to the graph.
	 * This function is called when the listener is removed to the graph.
	 * Here you could remove any other thing that you have done.
	 */
	void remove();
	
	/**
	 * Get the description of this listener, in a way to interact with the user.
	 * 
	 * @return a string describing the functionalities of this listener
	 */
	String getDescription();
}
