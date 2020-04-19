package fragment

import adapter.DashboardRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.payal.bookapp.R
import model.Book
import org.json.JSONException
import util.ConnectionManager


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recylerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout:RelativeLayout

    lateinit var progressBar:ProgressBar

    val bookInfoList= arrayListOf<Book>()

   // lateinit var btnCheckInternet:Button


//    val bookList= arrayListOf(
//        "P.S. I love You",
//        "The Great Leader",
//        "Time stops at Shamli",
//        "Wings of Fire",
//        "3 Mistakes of life",
//        "2 State",
//        "HalfGirlFriend",
//        "The subtle art of not giving a fuck",
//        "Rich Dad Poor Dad",
//        "Dairy of a Young Girl"
//    )





//    val bookInfoList= arrayListOf<Book>(
//        Book(bookName = "P.S. I Love You",bookAuthor = "Cecelia Ahern",bookCost = "Rs 900",bookRating = "4.9",
//            bookImage = "R.drawable.i_love_you"
//        ),
//        Book(bookName = "The Great Leader",bookAuthor = "Jim Harrison",bookCost = "Rs 875",bookRating = "4.8",
//            bookImage = "R.drawable.the_great_leader"
//        ),
//        Book(bookName = "Time stops at Shamli",bookAuthor = "Ruskin Bond",bookCost = "Rs 500",bookRating = "4.5",
//            bookImage = "R.drawable.time_stops_at_shamli"
//        ),
//        Book(bookName = "Wings of Fire",bookAuthor = "A.P.J. Abdul Kalam",bookCost = "Rs 1000",bookRating = "5",
//            bookImage = "R.drawable.wings_of_fire"
//        ),
//        Book(bookName = "3 Mistakes of life",bookAuthor = "Chetan Bhagat",bookCost = "Rs 980",bookRating = "4.7",
//            bookImage = "R.drawable.three_mistakes_of_life"
//        ),
//        Book(bookName = "2 State",bookAuthor = "Chetan Bhagat",bookCost = "Rs 930",bookRating = "4.6",
//            bookImage = "R.drawable.two_states"
//        ),
//        Book(bookName = "HalfGirlFriend",bookAuthor = "Chetan Bhagat",bookCost = "Rs 750",bookRating = "4.5",
//            bookImage = "R.drawable.half_girl_friend"
//        ),
//        Book(bookName = "The subtle art of not giving a fuck",bookAuthor = "Mark Manson",bookCost = "Rs 290",bookRating = "4.5",
//            bookImage = "R.drawable.the_subtle_art_of_not_giving_a_fuck"
//        ),
//        Book(bookName = "Rich Dad Poor Dad",bookAuthor = "Robert Kiyosaki",bookCost = "Rs 900",bookRating = "4.9",
//            bookImage = "R.drawable.rich_dad_poor_dad"
//        ),
//        Book(bookName = "Dairy of a Young Girl",bookAuthor = "Anne Frank",bookCost = "Rs 880",bookRating = "4.9",
//            bookImage = "R.drawable.dairy_of_young_girl"
//        )
//    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)

        progressLayout=view.findViewById(R.id.progressLayout)

        progressBar=view.findViewById(R.id.progressBar)

        progressLayout.visibility=View.VISIBLE

        layoutManager=LinearLayoutManager(activity)


        //  btnCheckInternet=view.findViewById(R.id.btnCheckInternet)


//        btnCheckInternet.setOnClickListener {
//            if(ConnectionManager().checkConnectivity(activity as Context)){
//                // Internet is available
//                val dialog=AlertDialog.Builder(activity as Context)
//                dialog.setTitle("Success")
//                dialog.setMessage("Internet Connection Found")
//                dialog.setPositiveButton("Ok"){
//                    text,listener->
//                    //Do nothing
//                }
//
//                dialog.setNegativeButton("Cancel"){
//                    text,listener->
//                    //Do nothing
//                }
//                dialog.create()
//                dialog.show()
//
//            }else{
//                //Internet is not available
//                val dialog=AlertDialog.Builder(activity as Context)
//                dialog.setTitle("Error")
//                dialog.setMessage("Internet Connection is not Found")
//                dialog.setPositiveButton("Ok"){
//                        text,listener->
//                    //Do nothing
//                }
//
//                dialog.setNegativeButton("Cancel"){
//                        text,listener->
//                    //Do nothing
//                }
//                dialog.create()
//                dialog.show()
//
//            }
//        }




        val queue=Volley.newRequestQueue(activity as Context)

        val url="http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest=object :JsonObjectRequest(Request.Method.GET,url,null,Response.Listener{

                //Here we will handle the response
                try{
                    progressLayout.visibility=View.GONE
                    val success=it.getBoolean("success")

                    if(success){
                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val bookJsonObject=data.getJSONObject(i)
                            val bookObject=Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")

                            )
                            bookInfoList.add(bookObject)
                            recylerAdapter= DashboardRecyclerAdapter(activity as Context,bookInfoList)

                            recyclerDashboard.adapter=recylerAdapter

                            recyclerDashboard.layoutManager=layoutManager

                            recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )

                            )
                        }
                    }else{
                        Toast.makeText(activity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                    }
                }catch (e:JSONException){
                    Toast.makeText(activity as Context,"Some unexpected error occurred!",Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener {

                //Here we will handle the error
                if(activity!=null){
                    Toast.makeText(activity as Context,"Volley error occurred!",Toast.LENGTH_SHORT).show()
                }


            }){
                override fun getHeaders():MutableMap<String,String>{
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="6f5311403e6661"
                    return headers
                }


            }

            queue.add(jsonObjectRequest)

        }else{
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting"){
                    text,listener->
                val settingIntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){
                    text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }


        return view
    }

}
