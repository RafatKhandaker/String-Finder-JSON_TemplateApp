package com.blackjacksmart.reddragon.html_string_finder__json_template;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
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
 * --CREATED BY RAFAT KHANDAKER --
 *
 Manually pulling http data from a server: creating and handling the connection;
 retrieving, buffering and reading the input stream line by line and pulling to view;
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

 ---------------- Added Bonus---- a potential web crawler to find string for web pages..
 or can be used to clone a web page
 **/

public class MainActivity extends AppCompatActivity {

    TextView responseTextView;
    TextView parseTextView;
    TextView searchURLText;
    TextView displayCount;
    TextView rawHTMLData;

    Button hitButton;
    Button searchButton;

    EditText enterSearchURL;
    EditText enterStringVal;

    int count = 0;
    String stringPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = (TextView) findViewById(R.id.response_text_view);
        parseTextView = (TextView) findViewById(R.id.parse_text_view);
        searchURLText = (TextView) findViewById(R.id.enter_url_tv);
        displayCount = (TextView) findViewById(R.id.display_count_tv);
        rawHTMLData = (TextView) findViewById(R.id.display_locate_tv);

        hitButton = (Button) findViewById(R.id.btnHit);
        searchButton = (Button) findViewById(R.id.search_btn);

        enterSearchURL = (EditText) findViewById(R.id.enter_url_et);
        enterStringVal =(EditText) findViewById(R.id.enter_string_et);

        rawHTMLData.setMovementMethod(new ScrollingMovementMethod());

        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ASyncTask1 async1 = new ASyncTask1();
                 async1.execute(
                        "http://jsonparsing.parseapp.com/jsonData/moviesDemoItem.txt"
                );
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Thread thread = new Thread();
                ASyncTask2 async2 = new ASyncTask2();

                count = 0;

                if(!enterSearchURL.getText().toString().equals("") &&
                      !enterStringVal.getText().toString().equals("")) {
                    async2.execute(
                            enterSearchURL.getText().toString()
                    );
                }else {
                    enterSearchURL.setTextColor(Color.RED);
                    enterStringVal.setTextColor(Color.RED);

                    enterSearchURL.setText("Value cannot be null");
                    enterStringVal.setText("Value cannot be null");

                    searchButton.setClickable(false);
                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    searchButton.setClickable(true);
                }

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String responseTxt = responseTextView.getText().toString();
        String parseTxt = parseTextView.getText().toString();
        String countTxt = displayCount.getText().toString();
        String rawHTMLTxt = rawHTMLData.getText().toString();

        outState.putString("response_text", responseTxt);
        outState.putString("parse_text", parseTxt);
        outState.putString("count_text", countTxt);
        outState.putString("RawHtml_text", rawHTMLTxt);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        responseTextView.setText(savedInstanceState
                .get("response_text")
                .toString());

        parseTextView.setText(savedInstanceState
                .get("parse_text")
                .toString());

        displayCount.setText(savedInstanceState
                .get("count_text")
                .toString());

        rawHTMLData.setText(savedInstanceState
                .get("RawHtml_text")
                .toString());
    }

//-----------------------------------find String method---------------------------------------------
    /** Method Tested on repl.it   it works correctly **/

    protected int findString(String search, String buffer) {
        count = 0;
        int searchSize = search.length();
        int bufferSize = buffer.length();

        for (int i = 0; i < (bufferSize - searchSize); i++) {
            if (buffer.substring(i,(i + searchSize)).equals(search)) {
                count++;
                stringPosition += (i + " ");
            }

        }
        return count;

    }

//--------------------------ASYNC TASK Running to pull and parse JSON Data -------------------------

    private class ASyncTask1 extends AsyncTask<String, String, String> {

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

    private class ASyncTask2 extends AsyncTask<String, String, String> {

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
       if(buffer != null) {
           return buffer.toString();
       }
        return "Null Value Call";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result.equals("Null Value Call")){
            displayCount.setText("null");
            rawHTMLData.setText(result);
            return;
        }

        String searchTxt = enterStringVal.getText().toString();
        int stringCount = findString(searchTxt, result);

        displayCount.setText(String.valueOf(stringCount));
        rawHTMLData.setText(result);
    }


}
}




