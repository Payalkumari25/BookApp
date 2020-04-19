package activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import database.BookDatabase
import com.payal.bookapp.R
import com.squareup.picasso.Picasso
import database.BookEntity
import kotlinx.android.synthetic.main.activity_description.*
import org.json.JSONObject
import util.ConnectionManager

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookImage: ImageView
    lateinit var txtBookDesc:TextView
    lateinit var btnAddToFav:Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout

    lateinit var toolbar: Toolbar

    var bookId: String?="100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName=findViewById(R.id.txtBookName)
        txtBookAuthor=findViewById(R.id.txtBookAuthor)
        txtBookPrice=findViewById(R.id.txtBookPrice)
        txtBookRating=findViewById(R.id.txtBookRating)
        txtBookImage=findViewById(R.id.imgBookImage)
        txtBookDesc=findViewById(R.id.txtBookDesc)
        btnAddToFav=findViewById(R.id.btnAddToFav)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility=View.VISIBLE
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book Details"

        if(intent!=null){
            bookId=intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!",Toast.LENGTH_SHORT).show()
        }

        if(bookId=="100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!",Toast.LENGTH_SHORT).show()
        }

        //Post Request
        val queue=Volley.newRequestQueue(this@DescriptionActivity)
        val url ="http://13.235.250.119/v1/book/get_book/"

        val jsonParams=JSONObject()
        jsonParams.put("book_id",bookId)

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){

            val jsonRequest=object :JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener{

                try {

                    val success=it.getBoolean("success")
                    if(success){
                        val bookJSONObject=it.getJSONObject("book_data")
                        progressLayout.visibility=View.GONE

                        val bookImageUrl=bookJSONObject.getString("image")
                        Picasso.get().load(bookJSONObject.getString("image")).error(R.drawable.default_book).into(imgBookImage)
                        txtBookName.text=bookJSONObject.getString("name")
                        txtBookAuthor.text=bookJSONObject.getString("author")
                        txtBookPrice.text=bookJSONObject.getString("price")
                        txtBookRating.text=bookJSONObject.getString("rating")
                        txtBookDesc.text=bookJSONObject.getString("description")

                        val bookEntity=BookEntity(
                            bookId?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDesc.text.toString(),
                            bookImageUrl
                        )
                        val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                        val isFav=checkFav.get()

                        if(isFav){
                            btnAddToFav.text="Remove from Favourites"
                            val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                            btnAddToFav.setBackgroundColor(favColor)
                        }else{
                            btnAddToFav.text="Add to Favourite"
                            val noFavColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                            btnAddToFav.setBackgroundColor(noFavColor)
                        }

                        btnAddToFav.setOnClickListener {

                            if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){

                                val async= DBAsyncTask(applicationContext,bookEntity,2).execute()
                                val result=async.get()
                                if(result){
                                    Toast.makeText(this@DescriptionActivity,"Book added to favourite",Toast.LENGTH_SHORT).show()

                                    btnAddToFav.text="Remove from Favourites"
                                    val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                                }
                            }else{

                                val async= DBAsyncTask(applicationContext,bookEntity,3).execute()
                                val result=async.get()
                                if(result){
                                    Toast.makeText(this@DescriptionActivity,"Book remove from favourites",Toast.LENGTH_SHORT).show()

                                    btnAddToFav.text="Add to Favourites"
                                    val nofavColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                    btnAddToFav.setBackgroundColor(nofavColor)
                                }else{
                                    Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                                }


                            }
                        }


                    }else{
                        Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {

                Toast.makeText(this@DescriptionActivity,"Volly Error $it",Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="6f5311403e6661"
                    return headers
                }
            }
            queue.add(jsonRequest)

        }else{
            val dialog= AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting"){
                    text,listener->
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }

            dialog.setNegativeButton("Exit"){
                    text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()

        }



    }

    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int) :AsyncTask<Void,Void,Boolean>(){

        /*
        Mode 1->Check DB if the book is favourite or not
        Mode 2-> Save the book into DB as favourite
        Mode 3->Remove the favourite book
         */

        val db =Room.databaseBuilder(context, BookDatabase::class.java,"book-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode) {

                1->{

                    //Check DB if book is favourite or not
                    val book:BookEntity?=db.bookDao().getBookId(bookEntity.book_id.toString())
                    db.close()
                    return book !=null
                }

                2->{

                    //Save the book into DB as favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3->{

                    //Remove the favourite book
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }
}
