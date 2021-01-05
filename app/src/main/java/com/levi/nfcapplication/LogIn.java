package com.levi.nfcapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LogIn extends Activity {

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_CODE = "code";
    private static final String TAG_BOOKED = "booked";
    private static final String TAG_NAME = "name";
    private static final String TAG_COUNTRY = "country";

    JSONArray peoples = null;

    UserInfo userinfo;
    ArrayList<UserInfo> UserList = new ArrayList<UserInfo>();
    FruitItem bookinfo;
    ArrayList<FruitItem> BookList = new ArrayList<FruitItem>();
    UserInfo nowuserinfo = new UserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        getDataUser("http://192.168.123.117/H.php"); //수정 필요
        getDataBook("http://192.168.123.117/J.php"); //수정 필요

        Button btn=(Button)findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Boolean cor = false;
                EditText editid = (EditText)findViewById(R.id.textid);
                String currentId = editid.getText().toString();
                EditText editpass = (EditText)findViewById(R.id.textpass);
                for (int i = 0; i < UserList.size() ;i++){
                    if(UserList.get(i).getMember_name().equals(currentId)){
                        nowuserinfo = UserList.get(i);
                        Intent intent = new Intent(v.getContext(), FirstMenu.class);
                        intent.putParcelableArrayListExtra("booklist",BookList);
                        intent.putExtra("id", nowuserinfo.getMember_name()); //intent에 값을 넘겨줍니다. 키 이름과 값을 차례로 넣어줘요.
                        startActivity(intent);
                        cor=true;
                        break;
                    }
                }
                if(!cor){
                    Toast.makeText(LogIn.this, "아이디/비밀번호 오류입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



    protected void showListUser() {
        try {
            Intent intent = getIntent();

            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                userinfo = new UserInfo();
                JSONObject c = peoples.getJSONObject(i);
                String name = c.getString(TAG_NAME);
                String country = c.getString(TAG_COUNTRY);
                userinfo.setMember_name(name);
                userinfo.setMember_country(country);
                UserList.add(userinfo);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    protected void showListBook() {
        try {
            Intent intent = getIntent();
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String code = c.getString(TAG_CODE);
                String name = c.getString(TAG_NAME);
                String author = c.getString(TAG_AUTHOR);
                String company = c.getString(TAG_COMPANY);
                String booked = c.getString(TAG_BOOKED);
                bookinfo = new FruitItem(name,author,company,booked,0,0,code);
                BookList.add(bookinfo);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getDataUser(String url) {
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
                showListUser();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    public void getDataBook(String url) {
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
                showListBook();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}