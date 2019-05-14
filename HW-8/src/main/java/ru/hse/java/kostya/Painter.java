package ru.hse.java.kostya;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;


/**
 * Class that contains few static functions helpful in drawing shapes.
 * All calculations in game made in Meters and this class
 *  helps to translate between Meters and pixels
 */
public class Painter {

    /**
     * Width of screen in meters.
     */
    public static final double WIDTH = 48.0; //In meters
    /**
     * Height of screen in meters.
     */
    public static final double HEIGHT = 32.0; //In meters

    /**
     * Adds line with given in meters parameters.
     * Binds it to given graphicsContext's canvas to allow resizing
     */
    public static Line addLine(double startX, double startY, double endX, double endY,
                               GraphicsContext graphicsContext) {
        final var line = new Line(
                widthToPixels(startX, graphicsContext), heightToPixels(startY, graphicsContext),
                widthToPixels(endX, graphicsContext), heightToPixels(endY, graphicsContext));
        line.startXProperty().bind(widthToPixelsProperty(startX, graphicsContext));
        line.startYProperty().bind(heightToPixelsProperty(startY, graphicsContext));
        line.endXProperty().bind(widthToPixelsProperty(endX, graphicsContext));
        line.endYProperty().bind(heightToPixelsProperty(endY, graphicsContext));
        return line;
    }

    /**
     * Draw line with given in meters parameters.
     */
    public static void drawLine(double startX, double startY, double endX, double endY,
                                GraphicsContext graphicsContext) {
        graphicsContext.strokeLine(
                widthToPixels(startX, graphicsContext), heightToPixels(startY, graphicsContext),
                widthToPixels(endX, graphicsContext), heightToPixels(endY, graphicsContext));
    }

    /**
     * Draws line with parameters in pixels already.
     */
    public static void drawLine(Line line, GraphicsContext graphicsContext) {
        graphicsContext.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    /**
     * Draws circle with parameters given in meters.
     */
    public static void drawCircle(double centerX, double centerY, double radius,
                                  GraphicsContext graphicsContext) {
        graphicsContext.fillOval(
                widthToPixels(centerX, graphicsContext), heightToPixels(centerY, graphicsContext),
                widthToPixels(radius, graphicsContext), widthToPixels(radius, graphicsContext));
        graphicsContext.strokeOval(
                widthToPixels(centerX, graphicsContext), heightToPixels(centerY, graphicsContext),
                widthToPixels(radius, graphicsContext), widthToPixels(radius, graphicsContext));
    }

    /**
     * Returns a binding which gives pixels value of given in meters width.
     */
    private static DoubleBinding widthToPixelsProperty(double width, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().widthProperty().multiply(width / WIDTH);
    }

    /**
     * Returns a binding which gives pixels value of given in meters height.
     */
    private static DoubleBinding heightToPixelsProperty(double height, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().heightProperty().multiply(height / HEIGHT);
    }

    /**
     * Translates width in meters to pixels.
     */
    public static double widthToPixels(double width, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().getWidth() * (width / WIDTH);
    }

    /**
     * Translates height in meters to pixels.
     */
    public static double heightToPixels(double height, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().getHeight() * (height / HEIGHT);
    }

    /**
     * Translates pixels to width in meters.
     */
    public static double pixelsToWidth(double pixels, GraphicsContext graphicsContext) {
        return WIDTH * (pixels / graphicsContext.getCanvas().getWidth());
    }

    /**
     * Translates pixels to height in meters.
     */
    public static double pixelsToHeight(double pixels, GraphicsContext graphicsContext) {
        return HEIGHT * (pixels / graphicsContext.getCanvas().getHeight());
    }
}
