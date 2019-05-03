package ru.hse.java.kostya;

import javafx.scene.canvas.GraphicsContext;

public interface Sprite {

    void draw(GraphicsContext graphicsContext);

    void update(double timeNano);
}
