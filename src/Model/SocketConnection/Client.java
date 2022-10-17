package Model.SocketConnection;

import Model.Ball;
import Model.Paddle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client extends Thread{
    private DatagramSocket toServer;
    private  byte[] sendingDataBuffer;
    private byte[] receivingDataBuffer;
    private DatagramPacket receivedPacket;
    private DatagramPacket sendPacket;
    private IntelligentBuffer intelligentBuffer;
    private Writer writer;
    private requestTimeout timeout;
    private Ball ball;
    private Paddle enemyPaddle;
    private final String IP;
    private final int PORT;
    private final int MAX;
    private final int SET_TIMER;

    public Client(String ipAddress, int port) throws SocketException {
        IP = ipAddress;
        PORT = port;
        MAX = 1024;
        SET_TIMER = 3;
        timeout = new requestTimeout(SET_TIMER);
        toServer = new DatagramSocket();
        intelligentBuffer = new IntelligentBuffer();
        sendingDataBuffer = new byte[MAX];
        receivingDataBuffer = new byte[MAX];

    }

    public void start(Paddle enemyPaddle, Ball ball) {
        this.enemyPaddle = enemyPaddle;
        this.ball = ball;
        super.start();
    }

    public static boolean isValidIP(String ip) {

        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";


        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        Pattern p = Pattern.compile(regex);

        if (ip == null) {
            return false;
        }

        Matcher m = p.matcher(ip);
        return m.matches();
    }
    private void setGameParameters(int[] parameters) {
        enemyPaddle.setX(parameters[0]);
        enemyPaddle.setY(parameters[1]);
        ball.setX(parameters[2]);
        ball.setY(parameters[3]);
    }
    public void receiveMessage() throws IOException {
        receivedPacket = new DatagramPacket(receivingDataBuffer,receivingDataBuffer.length);
        timeout.start();
        toServer.receive(receivedPacket);
        timeout.stop();
        intelligentBuffer.decodeReceivedMessage(new String(receivedPacket.getData()));
    }

    public void sendMessage(String message) throws IOException {
        sendingDataBuffer = message.getBytes();
        sendPacket = new DatagramPacket(sendingDataBuffer,sendingDataBuffer.length, InetAddress.getByName(IP), PORT);
        toServer.send(sendPacket);
    }

    public void closeConnection() {
        this.interrupt();
        writer.interrupt();
        toServer.close();
        sendingDataBuffer = null;
        receivingDataBuffer = null;
        receivedPacket = null;
        sendPacket = null;
    }

    public void setUpWriter(Paddle userPaddle) {
        writer = new Writer(userPaddle);
        writer.start();
    }

    public int[] getMessageValues(String message) {
        return intelligentBuffer.getValues(message);
    }

    private class IntelligentBuffer {
        private HashMap<String, int[]> elements;
        private final int MAX_DIMENSION = 10;
        public IntelligentBuffer() {
            elements = new HashMap<>();
        }

        public boolean decodeReceivedMessage(String Message) {
            String formattedMessage = Message.replaceAll("\u0000.*", "");
            if(formattedMessage.equals("DISCONNECT")) {
                closeConnection();
            }
            int[] info = new int[MAX_DIMENSION];
            String[] splitString = formattedMessage.split(":");


            try {
                int dim = 0;
                for (int i = 1; i < splitString.length; i++) {
                    for (int j = 0; j < splitString[i].split(",").length; j++) {
                        info[dim] = Integer.parseInt(splitString[i].split(",")[j]);
                        dim++;
                    }
                }
                elements.put(splitString[0], info);
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        public int[] getValues(String parameter) {
            if(elements.containsKey(parameter)) {
                return elements.get(parameter);
            } else {
                return null;
            }
        }




    }

    private class Writer extends Thread{
        private Paddle userPaddle;
        public Writer(Paddle userPaddle) {
            this.userPaddle = userPaddle;
        }

        @Override
        public void run() {
            while(toServer.isConnected()) {
                try {
                    sendMessage(String.valueOf(userPaddle.getY()));
                } catch (IOException e) {
                    System.out.println("Messaggio non inviato");
                }
            }
            System.out.println("Il writer ha finito il suo lavoro");
        }
    }

    private class requestTimeout extends TimerTask {
        private Timer timer;
        private final int DURATION;

        public requestTimeout(int duration) {
            DURATION = duration;
            timer = new Timer();
        }

        public void start() {
            timer.schedule(this, DURATION, 1);
        }

        public void stop() {
            timer.cancel();
        }

        @Override
        public void run() {
            closeConnection();
            System.exit(0);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                receiveMessage();
                int[] values = getMessageValues("GAME");
                if(values != null) {
                    setGameParameters(values);
                }
            } catch (IOException e) {
                System.out.println("Messaggio non Arrivato");
            }
        }
    }
}
