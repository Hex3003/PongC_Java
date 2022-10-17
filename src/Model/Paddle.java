package Model;

import java.awt.*;

public class Paddle extends GameComponent{

    private int movementSpeed;
    public Paddle(int x, int y, Color color, int width, int height, int movementSpeed) {
        super(x, y, color, width, height);
        this.movementSpeed = movementSpeed;
    }

    public void moveUp() {
        setY(getY() + movementSpeed);
    }

    public void moveDown() {
        setY(getY() - movementSpeed);
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
