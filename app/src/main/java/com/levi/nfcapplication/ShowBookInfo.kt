package com.levi.nfcapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bookinfo.*

class ShowBookInfo:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookinfo)

        var bookIn = intent.getParcelableExtra<FruitItem>("item")
        val currentid = intent.getStringExtra("id")

        nameOfBook.text=bookIn.name
        nameOfAuthor.text=bookIn.author
        nameOfCom.text=bookIn.company
        imageOfBook.setImageResource(bookIn.resId)
        if(bookIn.booked =="0"){
            bookAva.text="현재 도서 대출이 가능합니다."
        } else{
            bookAva.text="현재 도서 대출이 불가능합니다."
        }
        imageOfShelf.setImageResource(bookIn.sheId)

        chaekGalpiButton.setOnClickListener {
            val intent3 = Intent(this,BookgalpiMain::class.java)
            intent3.putExtra("code",bookIn.code);
            intent3.putExtra("name",bookIn.name);
            intent3.putExtra("id",currentid)

            startActivity(intent3)
        }
    }
}