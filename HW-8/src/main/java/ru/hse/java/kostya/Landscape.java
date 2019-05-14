package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ru.hse.java.kostya.Painter.*;

/**
 * Sprite which represents land in game.
 * Land consists of few segments which go from left border of screen to right border
 *  and intersect only in there ends
 * For any X coordinate it is either segments intersection in that position
 *  or exactly one line has such coordinate
 */
public class Landscape implements Sprite {

    public static final double LAND_LINES_WIDTH = 0.5;

    private final List<Line> lines = new ArrayList<>();
    private final GraphicsContext graphicsContext;

    /**
     * Creates landscape, by creating lines that makes it up.
     */
    public Landscape(@NotNull GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        addLine(0, 24, 18, 32, graphicsContext);
        addLine(18, 32, 20, 18, graphicsContext);
        addLine(20, 18, 24, 28, graphicsContext);
        addLine(24, 28, 36, 28, graphicsContext);
        addLine(36, 28, 42, 16, graphicsContext);
        addLine(42, 16, 48, 26, graphicsContext);
    }

    private void addLine(double startX, double startY, double endX, double endY,
                         GraphicsContext graphicsContext) {
        lines.add(Painter.addLine(startX, startY, endX, endY, graphicsContext));
    }

    /**
     * For given vertical line find Y coordinate of it's intersection with landscape.
     */
    public double getY(double x) {
        double position = HEIGHT;
        final double eps = 1e-8;
        for (Line line : lines) {
            final double startX = pixelsToWidth(line.getStartX(), graphicsContext);
            final double startY = pixelsToHeight(line.getStartY(), graphicsContext);
            final double endX = pixelsToWidth(line.getEndX(), graphicsContext);
            final double endY = pixelsToHeight(line.getEndY(), graphicsContext);
            if (startX - eps < x && x < endX + eps) {
                double intersection = (x - startX) / (endX - startX) * (endY - startY) + startY;
                position = Math.min(position, intersection);
            }
        }
        return position;
    }

    /**
     * Draws lines, that make up landscape.
     * Lines have LAND_LINES_WIDTH width and GREEN color
     */
    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setLineWidth(Painter.widthToPixels(LAND_LINES_WIDTH, graphicsContext));
        graphicsContext.setStroke(Color.GREEN);
        lines.forEach(line -> Painter.drawLine(line, graphicsContext));
    }

    /**
     * Landscape does not update with time.
     */
    @Override
    public void update(double timeNano) {

    }

    /**
     * Landscape always is alive.
     */
    @Override
    public boolean alive() {
        return true;
    }

}
