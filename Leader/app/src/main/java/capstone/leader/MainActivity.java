/**
 * This is the Leader app, to be paired with Follower for Capstone 1980.
 * Users with this app on their phone would receive in buffer a UDP transmission from
 * the ad-hoc network of Raspberry Pis, and then relay that to all
 * followers in network.
 *
 * To be used in tsunami preparedness
 */

package capstone.leader;

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
import java.net.UnknownHostException;

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
                    //All leaders will receive through port 8080
                    DatagramSocket serverSocket = new DatagramSocket(8080);
                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        String sentence = new String(receivePacket.getData());
                        if (sentence.equalsIgnoreCase("authorized warning")) {
                            //if leader receives warning of a disaster, set text on screen
                            //give phone notification
                            //send out UDP datagrampacket for all followers
                            //and sleep thread for 10 minutes so that it doesn't kill resources
                            //It's UDP, so to maximize chances it's received we want to send it out more frequently
                            received.setText("Disaster detected! Get to shelter! Informing followers...");
                            displayNotification();

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        String sendSentence = "disaster";
                                        DatagramSocket clientSocket = new DatagramSocket();
                                        InetAddress IPAddress = InetAddress.getByName("localhost");
                                        byte[] sendData = new byte[1024];
                                        byte[] receiveData = new byte[1024];
                                        sendData = sendSentence.getBytes();
                                        //send to port 8085 to hit all followers in wifi network
                                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8085);
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
                            }).start();

                            //Sleep thread for only 2 minutes, because we want to send out to followers as
                            //frequently as possible without killing battery
                            //this is because UDP is very fast, but not as reliable as TCP
                            Thread.sleep(120000);
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
