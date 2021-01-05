package com.levi.nfcapplication

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.levi.nfcapplication.DDD
import kotlinx.android.synthetic.main.activity_search.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class SearchBook:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val listView = findViewById<View>(R.id.listView) as ListView

        if (intent.hasExtra("booklist")) {
            var fruitList = intent.getParcelableArrayListExtra<FruitItem>("booklist")
            var resultList = ArrayList<FruitItem>()
            val adapter = FruitAdapter(fruitList)
            listView.adapter = adapter
            listView.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
                val selectItem = parent.getItemAtPosition(position) as String
                Toast.makeText(this, "클릭됨", Toast.LENGTH_SHORT).show()
            }

            val btnListener= View.OnClickListener {
                if(editText2.text==null) {
                    resultList.addAll(fruitList)

                }else{
                    for (i in fruitList.indices) {

                        if (fruitList!![i].name!!.contains(editText2.text)) {
                            resultList!!.add(fruitList!![i])
                        }
                    }

                }
                val adapter = FruitAdapter(resultList)
                listView.adapter = adapter

            }
            imageButton.setOnClickListener(btnListener)
        }



    }


    inner class FruitAdapter(items: ArrayList<FruitItem>) : BaseAdapter() {
        val itemList : ArrayList<FruitItem>  = items

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var fruitItemView : FruitItemView
            var view= convertView
            if (convertView != null) {
                fruitItemView = convertView as FruitItemView
            } else {
                fruitItemView = FruitItemView(applicationContext)
            }

            var item = itemList[position]
            var button: Button?=fruitItemView?.findViewById(R.id.chooseButton)

            val secondIntent = intent

            //var btnListener = BtnListener()
            val BtnListener=View.OnClickListener{
                var currentid = intent.getStringExtra("id")
                //Toast.makeText(this@searchActivity,item.name,Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SearchBook,ShowBookInfo::class.java)
                intent.putExtra("id",currentid)
                intent.putExtra("item",item)
                intent.putExtra("nameofbook",item.name)
                intent.putExtra("nameofauthor",item.author)
                intent.putExtra("nameOfCompany",item.company)
                intent.putExtra("booked",item.booked)
                intent.putExtra("resId",item.resId)
                startActivity(intent)

            }
            button?.setOnClickListener(BtnListener)

            fruitItemView.txtView.text = item.name
            fruitItemView.imgView.setImageResource(item.resId)


            return fruitItemView
        }

        override fun getItem(position: Int): Any {
            return itemList[position]
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getCount(): Int {
            return itemList.size
        }
    }
}