package capstone.server;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private TextView received;
    private static DatagramSocket serverSocket;
    private static final int BUFFER_SIZE = 1024;
    private static byte[] receiveData;
    private static byte[] sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        received = (TextView)findViewById(R.id.receivedText);

        try {
            DatagramSocket serverSocket = new DatagramSocket(8080);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while(true){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                String printString = "RECEIVED: " + sentence;
                received.setText(printString);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }

        } catch (SocketException e) {
            System.err.println("SocketException");
        } catch (IOException e) {
            System.err.println("IO Exception");
        }
    }
}
