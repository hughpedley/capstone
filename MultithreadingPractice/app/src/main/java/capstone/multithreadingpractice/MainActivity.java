package capstone.multithreadingpractice;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends Activity {

    private int even = 0;
    private int odd = -1;
    private int calc;

    final Lock lock = new ReentrantLock();

    private TextView evenThread, oddThread, aCalc, threadCalc;
    private AtomicBoolean finishedCalculating = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        evenThread = (TextView)findViewById(R.id.threadAEven);
        oddThread = (TextView)findViewById(R.id.threadB);
        aCalc = (TextView)findViewById(R.id.threadACalc);
        threadCalc = (TextView)findViewById(R.id.threadC);

        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Thread evens = new Thread(new Runnable() {
                    public void run() {
                        try {
                            //while (finishedCalculating.get()) {
                                //lock.lock();
                                //Thread.sleep(500);
                                //handleACalc.sendMessage(handleACalc.obtainMessage());
                                //finishedCalculating.set(false);
                                //lock.unlock();
                            //}
                            while (even < 100) {
                                lock.lock();
                                Thread.sleep(500);
                                handleACalc.sendMessage(handleACalc.obtainMessage());
                                //finishedCalculating.set(false);
                                lock.unlock();
                                //Thread.sleep(500);
                                handleEvens.sendMessage(handleEvens.obtainMessage());
                            }
                        } catch (Throwable t) {
                        }
                    }
                });
                evens.start();
                Thread odds = new Thread(new Runnable() {
                    public void run() {
                        try {
                            while (odd < 100) {
                                Thread.sleep(500);
                                handleOdds.sendMessage(handleOdds.obtainMessage());
                            }
                        } catch (Throwable t) {
                        }
                    }
                });
                odds.start();
                Thread calcs = new Thread(new Runnable() {
                    public void run() {
                        try {
                            while(calc < 500) {
                                Thread.sleep(500);
                                finishedCalculating.set(true);
                                handleCalc.sendMessage(handleCalc.obtainMessage());
                            }
                        } catch (Throwable t) {
                        }
                    }
                });
                calcs.start();
            }
        });
    }

    public void showEven() {
        even = even + 2;
        evenThread.setText(String.valueOf(even));
    }

    public void showOdd() {
        odd = odd + 2;
        oddThread.setText(String.valueOf(odd));
    }

    public void showCalc() {
        calc = (even * 2) + (odd * 3);
        threadCalc.setText(String.valueOf(calc));
        //finishedCalculating.getAndSet(true);
    }

    public void showACalc() {
        aCalc.setText(String.valueOf(calc));
    }

    Handler handleEvens = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showEven();
        }
    };

    Handler handleOdds = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showOdd();
        }
    };

    Handler handleCalc = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showCalc();
        }
    };

    Handler handleACalc = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showACalc();
        }
    };
}