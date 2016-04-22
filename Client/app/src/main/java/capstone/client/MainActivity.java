package capstone.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        userText = (EditText)findViewById(R.id.editText);

        button.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v) {
                        try {
                            String sendSentence = userText.getText().toString();
                            DatagramSocket clientSocket = new DatagramSocket();
                            InetAddress IPAddress = InetAddress.getByName("localhost");
                            byte[] sendData = new byte[1024];
                            byte[] receiveData = new byte[1024];
                            sendData = sendSentence.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8080);
                            clientSocket.send(sendPacket);
                            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                            clientSocket.receive(receivePacket);
                            clientSocket.close();
                        }catch(SocketException se){
                            System.err.println("Socket exception");
                        }catch(UnknownHostException uhe){
                            System.err.println("Unknown host exception");
                        }catch(IOException ioe){
                            System.err.println("IO Exception");
                        }
                    }
                }
        );
    }
}
