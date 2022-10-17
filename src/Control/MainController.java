package Control;

import Model.SocketConnection.Client;
import View.GameUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.SocketException;

public class MainController implements ActionListener, KeyListener, WindowListener {
    private GameUI gameFrame;
    private Client connection;
    private Timer animationTimer;
    private boolean inGame;


    public MainController(GameUI gameFrame) {
        this.gameFrame = gameFrame;
        this.animationTimer = new Timer(2, this);
        this.gameFrame.getOperationButton().addActionListener(this);
        this.gameFrame.addKeyListener(this);
        this.gameFrame.addWindowListener(this);

        inGame = false;
    }

    public void setUpGame(int paddlesWidht, int paddlesHeight, int ballDimension, boolean reverse) {
        if(!animationTimer.isRunning()) {
            animationTimer.start();
        }
        gameFrame.beginGame(paddlesWidht, paddlesHeight, ballDimension, reverse);
        gameFrame.switchPanel(gameFrame.getInGame());
        inGame = true;
    }

    private void connect(ActionEvent a) {
        if(a.getSource() == gameFrame.getOperationButton()) {
            String ip = gameFrame.getMessageField().getText();
            if(Client.isValidIP(ip)) {
                try {
                    connection = new Client(ip, 2000);
                    connection.sendMessage("READY");
                    connection.receiveMessage();
                    int[] dati = connection.getMessageValues("START");
                    if(dati != null) {
                        boolean reverse;
                        if(dati[3] == 1) {
                            reverse = false;
                        } else {
                            reverse = true;
                        }
                        setUpGame(dati[0], dati[1], dati[2], reverse);
                        connection.start(gameFrame.getInGame().getEnemyPaddle(), gameFrame.getInGame().getBall());
                        connection.setUpWriter(gameFrame.getInGame().getUserPaddle());
                    }
                } catch (Exception e) {
                    gameFrame.getOutputArea().append("Error: trying to connect to server but something went wrong, retry with a different IP\n");
                }
            } else {
                gameFrame.getOutputArea().append("Error: not valid IP\n");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(!inGame) {
            connect(actionEvent);
        } else {
            gameFrame.getInGame().repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(inGame) {
            if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println("TestUP");
                gameFrame.getInGame().getUserPaddle().moveUp();
            } else if(keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                System.out.println("TestDown");
                gameFrame.getInGame().getUserPaddle().moveDown();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        System.out.println("TestNO");
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        if(windowEvent.getSource() == gameFrame) {
            if(inGame) {
                try {
                    connection.sendMessage("DISCONNECT");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {

    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }

    public static void main(String args[]) {
        GameUI g = new GameUI(1200, 600);
        MainController m = new MainController(g);
    }
}
