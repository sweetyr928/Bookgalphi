package com.levi.nfcapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class FirstMenu extends Activity {

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_COUNTRY = "country";
    ArrayList<FruitItem> BookList = new ArrayList<FruitItem>();
    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

    TextView textofname;
    TextView textofcount;

    String currentid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_menu);
        Intent intent = getIntent();
        currentid = intent.getStringExtra("id");
        BookList = intent.getParcelableArrayListExtra("booklist");

        personList = new ArrayList<HashMap<String, String>>();
        getData("http://192.168.123.117/H.php"); //수정 필요


        final Button searchbtn=(Button)findViewById(R.id.search_bookname);
        Button borrowbtn=(Button)findViewById(R.id.borrow_direct);


        searchbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent searchintent = new Intent(v.getContext(), SearchBook.class);
                searchintent.putExtra("id",currentid);
                searchintent.putParcelableArrayListExtra("booklist",BookList);
                startActivity(searchintent);
            }
        });

        borrowbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent borrowintent = new Intent(v.getContext(), MainActivity.class);
                borrowintent.putExtra("id",currentid);
                borrowintent.putParcelableArrayListExtra("booklist",BookList);
                startActivity(borrowintent);
            }
        });


    }


    protected void showList() {
        try {
            textofname = (TextView)findViewById(R.id.firstmenuuser);
            textofcount = (TextView)findViewById(R.id.firstmenucount);
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String name = c.getString(TAG_NAME);
                String country = c.getString(TAG_COUNTRY);
                if(!currentid.equals(name)){
                    continue;
                }

                HashMap<String, String> persons = new HashMap<String, String>();

                textofname.setText(name);
                textofcount.setText(country);
                persons.put(TAG_NAME, name);
                persons.put(TAG_COUNTRY, country);

                personList.add(persons);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }


            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}