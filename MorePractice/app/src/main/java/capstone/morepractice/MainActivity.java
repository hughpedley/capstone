package capstone.morepractice;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.ArrayList;

//Asyncronous
public class MainActivity extends ListActivity {
    private static String[] items={"try", "some", "random",
            "words", "out", "here",
            "and", "see", "what",
            "happens", "at", "the",
            "end", "great", "googley",
            "moogley", "goodness", "gracious",
            "great", "balls",
            "of", "fire"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList()));

        new AddStringTask().execute();
    }

    class AddStringTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            for (String item : items) {
                publishProgress(item);
                SystemClock.sleep(600                               );
            }

            return(null);
        }

        @Override
        protected void onProgressUpdate(String... item) {
            ((ArrayAdapter)getListAdapter()).add(item[0]);
        }

        @Override
        protected void onPostExecute(Void unused) {
            Toast
                    .makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}