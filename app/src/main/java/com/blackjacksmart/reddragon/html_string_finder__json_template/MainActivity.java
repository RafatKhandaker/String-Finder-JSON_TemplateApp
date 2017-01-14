package com.blackjacksmart.reddragon.html_string_finder__json_template;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**                             LEARNING OBJECTIVE:
 *
 Manually pulling http data from a server: creating and handling the connection;
 reatrieving, buffering and reading the input stream line by line and pulling to view;
 rules for thread: Main thread is UI, does not allow data from background thread to
 change UI thread

 ASYNCTASK --> Provides easy use of UI thread , it does work in a background thread
 and publish results onto the main UI thread. Designed around Threads and
 handlers. Asynchronous connection ( ability to run simultaneously )
 << two methods used by ASYNCTASK >>
 - doInBackground() - Run on a background thread
 - onPostExecute() - Run on main thread
 Synchronous --> runs insync with thread, only until the current process is finished will allow
 continuation into the UI thread.

 Manually parsing JSON object data...with default android - java library

 {"movies": [{"movie": "Avengers","year": 2012}]}
 **/

public class MainActivity extends AppCompatActivity {

    TextView responseTextView;
    TextView parseTextView;
    TextView searchURLText;
    Button hitButton;
    Button searchButton;
    EditText enterSearchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = (TextView) findViewById(R.id.response_text_view);
        hitButton = (Button) findViewById(R.id.btnHit);
        parseTextView = (TextView) findViewById(R.id.parse_text_view);
        searchButton = (Button) findViewById(R.id.search_btn);
        enterSearchText = (EditText) findViewById(R.id.enter_url_et);
        searchURLText = (TextView) findViewById(R.id.enter_url_tv);

//        findString("test","this is a test buffer test test test");

        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ASyncTask1().execute(
                        "http://jsonparsing.parseapp.com/jsonData/moviesDemoItem.txt");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

    }

//----------------------------------find String method---------------------------------------------
    /** Method Tested on repl.it   it works correctly **/

    public void findString(String search, String buffer) {
        int searchSize = search.length();
        int bufferSize = buffer.length();
        int count = 0;

        for (int i = 0; i < (bufferSize - searchSize); i++) {
            if (buffer.substring(i,(i + searchSize)).equals(search)) {
                count++;
            }

        }

    }

//--------------------------ASYNC TASK Running to pull and parse JSON Data -------------------------

    public class ASyncTask1 extends AsyncTask<String, String, String> {

        private StringBuffer buffer;
        private String finalJson;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                finalJson = buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (reader != null) {
                        reader.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // buffer.toString will be passed into variable onPostExecute
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            responseTextView.setText(result);

            try {
                JSONData();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void JSONData() throws JSONException {

            JSONObject parentObject = new JSONObject(finalJson);

            JSONArray parentArray = parentObject.getJSONArray("movies");
            JSONObject finalObject = parentArray.getJSONObject(0);

            String movieName = finalObject.getString("movie");
            int year = finalObject.getInt("year");

            parseTextView.setText(movieName + " - " + year);

            //-------------------------------------------------------------------------
                            /** For multiple JSON data  **/                          //
            //-------------------------------------------------------------------------
            // for( int = 0; i < parentArray.length(); i++ ){
            //     JSONObject finalObject = parentArray.getJSONObject(i);
            //
            //      String movieName = finalObject.getString("movie");
            //      int year = finalObject.getInt("year");
            //      finalBufferData.append(movieName + "  -  " + year);
            //
            // }
            //---------------------------------------------------------------------------
        }
    }
//--------------------------------------------------------------------------------------------------

    public class ASyncTask2 extends AsyncTask<String, String, String> {

    private StringBuffer buffer;

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            buffer = new StringBuffer();

            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            try {
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }


}
}




