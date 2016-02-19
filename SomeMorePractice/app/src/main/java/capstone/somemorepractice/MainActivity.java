package capstone.somemorepractice;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private TextView Watch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Watch = (TextView)findViewById(R.id.txtWatch);
        displayCurrentTime();
    }

    public void displayCurrentTime() {
        Date dt = new Date();
        int hours = dt.getHours();
        if(hours > 12)
            hours = hours - 12;
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = hours + ":" + minutes + ":" + seconds;
        Watch.setText(curTime);
    }

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            displayCurrentTime();
        }
    };

    AtomicBoolean ContinueThread = new AtomicBoolean(false);

    public void onStart() {
        super.onStart();
        Thread background=new Thread(new Runnable() {
            public void run() {
                try {
                    while(ContinueThread.get()) {
                        Thread.sleep(1000);
                        handler.sendMessage(handler.obtainMessage());
                    }
                }
                catch (Throwable t) {
                }
            }
        });
        ContinueThread.set(true);
        background.start();
    }
}
