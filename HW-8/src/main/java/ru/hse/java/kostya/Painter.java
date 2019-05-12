package ru.hse.java.kostya;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;


public class Painter {

    public final static double WIDTH = 48.0; //In meters
    public final static double HEIGHT = 32.0; //In meters

    public static Line addLine(double startX, double startY, double endX, double endY, GraphicsContext graphicsContext) {
        final var line = new Line(widthToPixels(startX, graphicsContext), heightToPixels(startY, graphicsContext),
                widthToPixels(endX, graphicsContext), heightToPixels(endY, graphicsContext));
        line.startXProperty().bind(widthToPixelsProperty(startX, graphicsContext));
        line.startYProperty().bind(heightToPixelsProperty(startY, graphicsContext));
        line.endXProperty().bind(widthToPixelsProperty(endX, graphicsContext));
        line.endYProperty().bind(heightToPixelsProperty(endY, graphicsContext));
        return line;
    }

    public static void drawLine(double startX, double startY, double endX, double endY, GraphicsContext graphicsContext) {
        graphicsContext.strokeLine(widthToPixels(startX, graphicsContext), heightToPixels(startY, graphicsContext),
                widthToPixels(endX, graphicsContext), heightToPixels(endY, graphicsContext));
    }


    public static void drawLine(Line line, GraphicsContext graphicsContext) {
        graphicsContext.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    public static void drawCircle(double centerX, double centerY, double radius, GraphicsContext graphicsContext) {
        graphicsContext.fillOval(widthToPixels(centerX, graphicsContext), heightToPixels(centerY, graphicsContext),
                widthToPixels(radius, graphicsContext), widthToPixels(radius, graphicsContext));
        graphicsContext.strokeOval(widthToPixels(centerX, graphicsContext), heightToPixels(centerY, graphicsContext),
                widthToPixels(radius, graphicsContext), widthToPixels(radius, graphicsContext));
    }

    public static DoubleBinding widthToPixelsProperty(double width, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().widthProperty().multiply(width / WIDTH);
    }

    public static DoubleBinding heightToPixelsProperty(double height, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().heightProperty().multiply(height / HEIGHT);
    }

    public static double widthToPixels(double width, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().getWidth() * (width / WIDTH);
    }

    public static double heightToPixels(double height, GraphicsContext graphicsContext) {
        return graphicsContext.getCanvas().getHeight() * (height / HEIGHT);
    }

    public static double pixelsToWidth(double pixels, GraphicsContext graphicsContext) {
        return WIDTH * (pixels / graphicsContext.getCanvas().getWidth());
    }

    public static double pixelsToHeight(double pixels, GraphicsContext graphicsContext) {
        return HEIGHT * (pixels / graphicsContext.getCanvas().getHeight());
    }
}
