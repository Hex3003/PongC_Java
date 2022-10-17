package Model;

import java.awt.*;

public abstract class GameComponent {
    private int x;
    private int y;

    private final int defaultX;
    private final int defaultY;
    private final Color color;
    private final int width;
    private final int height;

    public GameComponent(int x, int y, Color color, int width, int height){
        this.x = x;
        defaultX = x;
        this.y = y;
        defaultY = y;
        this.color = color;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDefaultX() {
        return defaultX;
    }

    public int getDefaultY() {
        return defaultY;
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setToDefaultLocation() {
        this.x = defaultX;
        this.y = defaultY;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
