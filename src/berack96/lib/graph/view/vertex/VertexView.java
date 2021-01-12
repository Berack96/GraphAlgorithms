package berack96.lib.graph.view.vertex;

import berack96.lib.graph.view.GraphicalView;

import java.awt.*;

public class VertexView<V> implements GraphicalView<VertexComponent<V>> {

    private static final Font FONT = new Font("Comic Sans MS", Font.BOLD, 17);
    private static final int PADDING = 6;

    @Override
    public Rectangle getBox(VertexComponent<V> obj, Point center) {
        FontMetrics metrics = obj.getFontMetrics(FONT);
        int stringPixels = metrics.stringWidth(obj.vertex.get().toString());
        int size = Math.max(stringPixels, metrics.getHeight()) + 2 * PADDING;

        return new Rectangle(center.x - size / 2, center.y - size / 2, size, size);
    }

    @Override
    public void paint(Graphics2D g2, VertexComponent<V> obj, Point center) {
        boolean discovered = obj.vertex.getMarks().contains("discovered");
        boolean visited = obj.vertex.getMarks().contains("visited");
        boolean selected = obj.vertex.getMarks().contains("selected");

        FontMetrics metrics = obj.getFontMetrics(FONT);
        int stringPixels = metrics.stringWidth(obj.vertex.get().toString());
        int size = Math.max(stringPixels, metrics.getHeight()) + 2 * PADDING;

        center.x = center.x - size / 2;
        center.y = center.y - size / 2;

        g2.setFont(FONT);
        g2.setColor(visited || selected ? Color.RED : Color.ORANGE);
        g2.fillOval(center.x, center.y, size, size);
        g2.setColor(visited || discovered  || selected ? Color.ORANGE : Color.YELLOW);
        g2.fillOval(center.x + PADDING / 2, center.y + PADDING / 2, size - PADDING, size - PADDING);
        g2.setColor(Color.BLACK);
        g2.drawString(obj.vertex.get().toString(), center.x + PADDING + (size - 2 * PADDING - stringPixels) / 2, center.y + (size) / 2 + PADDING);
    }
}
