package berack96.sim.util.graph.view.stuff;

import java.awt.*;

/**
 * Class that create a Polygon that has a shape of an arrow
 *
 * @author Berack96
 */
public class Arrow extends Polygon {
	private static final long serialVersionUID = 1L;

	/**
     * Create an arrow
     *
     * @param start    the starting point of your arrow (the base)
     * @param end      the ending point of your arrow (the head)
     * @param size     the size of the arrow base
     * @param headSize the size of the arrow's head
     */
    public Arrow(Point start, Point end, final int size, final int headSize) {
        final Point.Double vector = new Point.Double(end.x - start.x, end.y - start.y);
        /* vectors normalization */
        double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        vector.x = vector.x / length;
        vector.y = vector.y / length;

        final Point headStart = new Point((int) (end.x - vector.x * headSize), (int) (end.y - vector.y * headSize));

        /* rotating vector for the parallels */
        double cs = Math.cos(Math.PI / 2);
        double sn = Math.sin(Math.PI / 2);
        double x = vector.x * cs - vector.y * sn;
        double y = vector.x * sn + vector.y * cs;
        vector.setLocation(x, y);

        /* TODO here use some magic for create a curve arrow if vector.x == vector.y && vector.x == 0 */
        /* building arrow starting from A to G */
        /*
                         C
                         |\
                         | \
                A--------B  \
                |            D
                G--------F  /
                         | /
                         |/
                         E
         */
        addPoint((int) (start.x - vector.x * size), (int) (start.y - vector.y * size));
        addPoint((int) (start.x + vector.x * size), (int) (start.y + vector.y * size));

        addPoint((int) (headStart.x + vector.x * size), (int) (headStart.y + vector.y * size));
        addPoint((int) (headStart.x + vector.x * headSize), (int) (headStart.y + vector.y * headSize));
        addPoint(end.x, end.y);
        addPoint((int) (headStart.x - vector.x * headSize), (int) (headStart.y - vector.y * headSize));
        addPoint((int) (headStart.x - vector.x * size), (int) (headStart.y - vector.y * size));
    }
}
