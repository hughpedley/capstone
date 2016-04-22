/**
 * This is the Follower app, to be paired with Leader for my capstone project.
 *
 * Receives a UDP transmission from leader app, and will notify user that they should get to
 * safety. At this point, the user should open their GPS app and get to safety
 */

package capstone.follower;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
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

        new Thread(new Runnable() {
            public void run() {
                try {
                    //All followers will receive through port 8085
                    DatagramSocket serverSocket = new DatagramSocket(8085);
                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        String sentence = new String(receivePacket.getData());
                        if (sentence.equalsIgnoreCase("disaster")) {
                            //if follower sends out that there is a disaster, set text on screen
                            //give phone notification
                            //and sleep thread for 10 minutes so that it does not repeatedly notify/waste time and resources
                            received.setText("Disaster detected! Get to shelter!");
                            displayNotification();
                            Thread.sleep(600000);
                        }
                        InetAddress IPAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();
                        String capitalizedSentence = sentence.toUpperCase();
                        sendData = capitalizedSentence.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }
                } catch (SocketException se) {
                    System.err.println("SocketException");
                } catch (IOException ioe) {
                    System.err.println("IO Exception");
                } catch (InterruptedException ie) {
                    //If interrupted, do nothing
                    System.err.println("interrupted exception");
                }
            }
        }).start();
    }

    //Makes a notification
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void displayNotification(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Disaster imminent!")
                .setContentText("Get to safety!")
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }
}
