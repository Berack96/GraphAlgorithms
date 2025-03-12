package net.berack.upo.graph.view;

import java.awt.*;

/**
 * An interface for divide the "hitbox" and the "paint" of the various items
 *
 * @param <O> the object to paint
 * @author Berack96
 */
public interface GraphicalView<O> {
    
	/**
     * Box where the object is sensible at listeners (like Hitbox)
     *
     * @param obj    the object to draw
     * @param center the center point of the object
     * @return a rectangle where the object is sensible to the listeners
     */
    Rectangle getBox(O obj, Point center);
    
    /**
     * The paint function, aka the part where you can draw things (like Mesh)
     *
     * @param g2     the graphics object used for painting
     * @param obj    the object to paint
     * @param center the center point of the object
     */
    void paint(Graphics2D g2, O obj, Point center);
}
