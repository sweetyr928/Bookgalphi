package com.levi.nfcapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class BorrowBook extends AppCompatActivity {


    //Intent intent2 = getIntent();
    //Intent intent2 = getIntent();
    //String name = intent2.getExtras().getString("Tag_value");



    public void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        setContentView(R.layout.borrow_a_book);

        //Intent intent2 = getIntent();
        //String name = intent2.getExtras().getString("Tag_value");



       /*Log.d("Tag_value",intent2.getExtras().getString("Tag_value"));*/

    }

    public void mOnPopupClick(View v){
        Intent intent2 = getIntent();
        String userid = intent2.getExtras().getString("id");
        String Tag_value = intent2.getExtras().getString("Tag_value");
        String bookname = intent2.getStringExtra("name");
        String bookcode = intent2.getStringExtra("code");
        Intent intent =new Intent(this,PopupBorrowBook.class);
        intent.putExtra("Tag_value",Tag_value);
        intent.putExtra("id",userid);
        intent.putExtra("name",bookname);
        intent.putExtra("code",bookcode);
        startActivity(intent);
        /*intent.putExtra("Tag_value",Tag_value);
        startActivityForResult(intent,1);*/
    }


}


