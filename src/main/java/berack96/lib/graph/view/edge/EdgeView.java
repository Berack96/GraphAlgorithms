package berack96.lib.graph.view.edge;

import berack96.lib.graph.view.GraphicalView;
import berack96.lib.graph.view.stuff.Arrow;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;

public class EdgeView<V> implements GraphicalView<EdgeComponent<V>> {

    private static final Font FONT = new Font("Papyrus", Font.BOLD, 14);

    @Override
    public Rectangle getBox(EdgeComponent<V> edge, Point center) {
        /* CALCULATING BOUNDS AND ARROW STARTING AND ENDING POINTS */
        Point srcLoc = edge.source.getLocation();
        Point desLoc = edge.destination.getLocation();
        /* getting the radius */
        int srcRadius = edge.source.getHeight() / 2;
        int desRadius = edge.destination.getHeight() / 2;
        /* centering the location */
        srcLoc.translate(srcRadius, srcRadius);
        desLoc.translate(desRadius, desRadius);

        /* using vector for moving to the edge of the circumference and finding location of int box */
        final Point.Double vector = getVector(srcLoc, desLoc);

        /* CALCULATING THE NUMBER SPACE */
        int boxDistance = (int) (srcLoc.distance(desLoc) / 2.7);
        FontMetrics metrics = edge.getFontMetrics(FONT);
        int dimString = metrics.stringWidth(String.valueOf(edge.weight));
        int dimRect = Math.max(dimString, metrics.getHeight());
        return new Rectangle(
                (int) ((desLoc.x - (vector.x * boxDistance)) - (dimRect / 2)),
                (int) ((desLoc.y - (vector.y * boxDistance)) - (dimRect / 2)),
                dimRect, dimRect);
    }

    @Override
    public void paint(Graphics2D g2, EdgeComponent<V> edge, Point center) {
        /* CALCULATING BOUNDS AND ARROW STARTING AND ENDING POINTS */
        Point srcLoc = edge.source.getLocation();
        Point desLoc = edge.destination.getLocation();
        /* getting the radius */
        int srcRadius = edge.source.getHeight() / 2;
        int desRadius = edge.destination.getHeight() / 2;
        /* centering the location */
        srcLoc.translate(srcRadius, srcRadius);
        desLoc.translate(desRadius, desRadius);

        /* using vector for moving to the edge of the circumference and finding location of int box */
        final Point.Double vector = getVector(srcLoc, desLoc);

        /* CALCULATING THE NUMBER SPACE */
        int boxDistance = (int) (srcLoc.distance(desLoc) / 2.7);
        FontMetrics metrics = edge.getFontMetrics(FONT);
        int dimString = metrics.stringWidth(String.valueOf(edge.weight));
        int dimRect = Math.max(dimString, metrics.getHeight());
        Rectangle integerRect = new Rectangle(
                (int) ((desLoc.x - (vector.x * boxDistance)) - (dimRect / 2)),
                (int) ((desLoc.y - (vector.y * boxDistance)) - (dimRect / 2)),
                dimRect, dimRect);

        /* moving to a distance R to the center */
        srcLoc.translate((int) (vector.x * srcRadius), (int) (vector.y * srcRadius));
        desLoc.translate((int) (-vector.x * desRadius), (int) (-vector.y * desRadius));

        /* THE COLOR OF THE ARROW */
        Collection<Object> marksD = edge.destination.vertex.getMarks();
        Collection<Object> marksS = edge.source.vertex.getMarks();

        boolean isChild = marksD.contains(edge.source.vertex.get());
        boolean selected = marksS.contains("selected");
        boolean isMod = marksD.contains("modD") && marksS.contains("modS");

        Color arrowColor = isChild || selected ? Color.RED : Color.BLACK;
        Color boxColor = isChild || selected || isMod ? Color.ORANGE : Color.BLUE;
        Color stringColor = isChild || selected || isMod ? Color.BLACK : Color.CYAN;

        g2.setFont(FONT);
        /* draw all */
        g2.setColor(arrowColor);
        //g2d.drawLine(arrowStart.x, arrowStart.y, arrowEnd.x, arrowEnd.y);
        Polygon arrow = new Arrow(srcLoc, desLoc, 1, 10);
        //g2d.draw(arrow);
        g2.fillPolygon(arrow);

        /* draw the integer space */
        g2.setColor(boxColor);
        g2.fill(integerRect);
        g2.setColor(arrowColor);
        g2.draw(integerRect);
        g2.setColor(stringColor);
        g2.drawString(String.valueOf(edge.weight), (float) (integerRect.x + (dimRect - dimString) / 2), (float) (integerRect.y + (dimRect + metrics.getHeight() / 2) / 2));
    }

    private Point.Double getVector(Point a, Point b) {
        final Point.Double vector = new Point2D.Double(b.x - a.x, b.y - a.y);
        /* normalizing vector */
        double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        if(length != 0) {
	        vector.x = vector.x / length;
	        vector.y = vector.y / length;
        }

        return vector;
    }
}
