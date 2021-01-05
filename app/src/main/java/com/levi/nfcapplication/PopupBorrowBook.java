package com.levi.nfcapplication;


import android.app.ProgressDialog;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import com.levi.nfcapplication.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PopupBorrowBook extends Activity {
    String bookname;
    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;
    private ArrayList<PersonalData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditTextSearchKeyword;
    private String mJsonString;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀 바 없애기
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.pop_up_borrowbook);
        //final Intent intent =new Intent(this,FirstMenu.class);

        Intent intent2 = getIntent();//태그값 가져온는 인텐트//팝업창 띄우기위한 인텐트
        //String Tag_value = intent2.getExtras().getString("Tag_value");

        /*
        if(Tag_value.equals("AB E0 40 03 90 00")){

            bookname="배려" ;
            intent.putExtra("BookName",bookname);

        }
        else if(Tag_value.equals("0B F0 40 03 90 00")){
            bookname="힐러리처럼 일하고 콘디처럼 승리하라" ;
            intent.putExtra("BookName",bookname);

        }
        else if(Tag_value.equals("04 7D DF 62 5F 66 80 90 00")){
            bookname="5가지 사랑의 언어" ;
            intent.putExtra("BookName",bookname);

        }
        else if(Tag_value.equals("04 C2 E1 62 5F 66 80 90 00")){
            bookname="위대한 개츠비" ;
            intent.putExtra("BookName",bookname);

        }
        else if(Tag_value.equals("8B E4 40 03 90 00")){
            bookname="노동 없는 미래" ;
            intent.putExtra("BookName",bookname);

        }
        else if(Tag_value.equals("04 F5 E0 62 5F 66 80 90 00")){
            bookname="아무도 무릎 꿇지 않은 밤" ;
            intent.putExtra("BookName",bookname);

        }
        startActivity(intent);*/

    }

    public void mOnClose(View v) {

        Intent intent2 = getIntent();
        String Tag_value = intent2.getExtras().getString("Tag_value");
        String name = intent2.getStringExtra("id");
        String bookname = intent2.getExtras().getString("name");
        String bookcode = intent2.getExtras().getString("code");
        InsertDataUser task1 = new InsertDataUser();
        InsertDataBook task2 = new InsertDataBook();
        InsertData task = new InsertData();

        if (bookcode.equals(".")) {
            task.execute("http://192.168.123.117/insert.php", name,"");
            task2.execute("http://192.168.123.117/J_insert.php", Tag_value, "aaaa", "aaaaaaa", "aaaa", "aaaa");
            Intent intent = new Intent(this, FirstMenu.class);
            intent.putExtra("id", name);
            startActivity(intent);
        } else if (bookcode.equals(Tag_value)) {

            task1.execute("http://192.168.123.117/insert.php", name, "here");
            task2.execute("http://192.168.123.117/J_insert.php", Tag_value, "aaaa", "aaaaaaa", "aaaa", "aaaa");
            Intent intent = new Intent(this, FirstMenu.class);
            intent.putExtra("id", name);
            startActivity(intent);
        }


    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        return;
    }

    private static String TAG = "phpexample";


    class InsertDataUser extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupBorrowBook.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String) params[1];
            String country = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "name=" + name + "&country=" + country;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    class InsertDataBook extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupBorrowBook.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String code = (String) params[1];
            String name = (String) params[2];
            String author = (String) params[3];
            String company = (String) params[4];
            String booked = (String) params[5];

            String serverURL = (String) params[0];
            String postParameters = "name=" + name + "&author=" + author + "&company" + company + "&booked=" + booked + "&code=" + code;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupBorrowBook.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String) params[1];
            String country = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "name=" + name + "&country=" + country;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


}
