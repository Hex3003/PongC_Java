package View;

import javax.swing.*;

public class GameUI extends JFrame{
    private JPanel panel;
    private InGamePanel inGame;
    private JButton operationButton;
    private JTextField messageField;
    private JLabel messageLabel;
    private JTextArea outputArea;
    private JLabel outputLabel;

    public GameUI(int width, int height) {
        setContentPane(panel);
        setSize(width, height);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Pong Game");
        setFocusable(true);
        requestFocus();
    }

    public JPanel getPanel() {
        return panel;
    }

    public InGamePanel getInGame() {
        return inGame;
    }

    public void beginGame(int paddlesWidht, int paddlesHeight, int ballDimension, boolean reverse) {
        inGame = new InGamePanel(getWidth(), getHeight(), paddlesWidht, paddlesHeight, ballDimension, reverse);
    }

    public void switchPanel(JPanel panel) {
        getContentPane().removeAll();
        setContentPane(panel);
        getContentPane().revalidate();
    }

    public void changePanelLook() {
        operationButton.setText("Resume");
        messageField.setVisible(false);
        messageLabel.setVisible(false);
    }

    public JButton getOperationButton() {
        return operationButton;
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }

    public JLabel getOutputLabel() {
        return outputLabel;
    }
}
