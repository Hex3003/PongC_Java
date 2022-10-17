package View;

import Model.Ball;
import Model.Paddle;

import javax.swing.*;
import java.awt.*;

public class InGamePanel extends JPanel {
    private Paddle userPaddle;
    private Paddle enemyPaddle;
    private Ball ball;
    private final Color enemyColor = Color.RED;
    private final Color userColor = Color.BLUE;
    private final Color ballColor = Color.WHITE;
    private final int leftPosition;
    private final int rightPosition;
    private final int centerPosition;
    private final int defaultHeight;
    private final int defaultPaddlesSpeed;

    public InGamePanel(int width, int height ,int paddlesWidth, int paddlesHeight, int ballDimension, boolean reverse) {
        setBackground(Color.BLACK);

        setSize(width, height);

        leftPosition = 20;
        rightPosition = getWidth() - (leftPosition + paddlesWidth);
        centerPosition = getWidth() / 2;
        defaultHeight = getHeight() / 2;
        defaultPaddlesSpeed = 5;

        if(!reverse) {
            userPaddle = new Paddle(leftPosition, defaultHeight, userColor, paddlesWidth, paddlesHeight, defaultPaddlesSpeed);
            enemyPaddle = new Paddle(rightPosition, defaultHeight, enemyColor, paddlesWidth, paddlesHeight, defaultPaddlesSpeed);
        } else {
            enemyPaddle = new Paddle(leftPosition, defaultHeight, enemyColor, paddlesWidth, paddlesHeight, defaultPaddlesSpeed);
            userPaddle = new Paddle(rightPosition, defaultHeight, userColor, paddlesWidth, paddlesHeight, defaultPaddlesSpeed);
        }

        ball = new Ball(centerPosition, defaultHeight, ballColor, ballDimension);
    }

    public Paddle getUserPaddle() {
        return userPaddle;
    }

    public Paddle getEnemyPaddle() {
        return enemyPaddle;
    }

    public Ball getBall() {
        return ball;
    }

    public Color getEnemyColor() {
        return enemyColor;
    }

    public Color getUserColor() {
        return userColor;
    }

    public Color getBallColor() {
        return ballColor;
    }

    public int getLeftPosition() {
        return leftPosition;
    }

    public int getRightPosition() {
        return rightPosition;
    }

    public int getCenterPosition() {
        return centerPosition;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    public int getDefaultPaddlesSpeed() {
        return defaultPaddlesSpeed;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        userPaddle.draw(g);
        enemyPaddle.draw(g);
        ball.draw(g);
    }
}
