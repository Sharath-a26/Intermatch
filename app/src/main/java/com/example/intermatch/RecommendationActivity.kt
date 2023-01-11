package com.example.intermatch

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_info_alert.*
import kotlinx.android.synthetic.main.activity_info_alert.view.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_recommendation.*
import kotlinx.android.synthetic.main.activity_upload_user_details.*
import kotlinx.android.synthetic.main.filter_layout.*
import kotlinx.android.synthetic.main.filter_layout.view.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import kotlin.random.Random


/**
    recommendation page
    1. User can navigate to liked projects and profile
    2. Like projects and send email to faculty
 */
var k : JSONArray = JSONArray()
var user_interest : JSONArray = JSONArray()
var i:Int = 0
var recom_type = "dept"
var domains : JSONArray = JSONArray()
var faculty_email : String = ""
var temp : String = ""
var temp2 : String = ""
var desc = ""
var user_github = ""
var user_linkedin = ""
var stype = "Projects"
var user_department = ""
var user_pass = ""
var user_off_email = ""
var name_user = ""
class RecommendationActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var gridView : GridView? = null
    private var arrayList : ArrayList<LanguageItem>? = null
    private var languageAdapter : LanguageAdapter? = null
    var x1 by Delegates.notNull<Float>()
    var x2 by Delegates.notNull<Float>()
    var y1 by Delegates.notNull<Float>()
    var y2 by Delegates.notNull<Float>()
    var like_count : Int = 0
    lateinit var spinnerItems : ArrayList<spinner_item>
    lateinit var adapter : spinnerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        supportActionBar?.hide()



        recommendation_layout.isVisible = false


        val dialog = ProgressDialog(this)
        dialog.setMessage("Finding best projects")
        dialog.setCancelable(false)
        dialog.setInverseBackgroundForced(false)
        dialog.show()
        val alertbuilder = AlertDialog.Builder(this)
        /**
         * Make all the color of
         */
        /**
         * username obtained from login
         */

        var username = intent.getStringExtra("username")
        var user_type = intent.getStringExtra("usertype")
        var dept_of_user = intent.getStringExtra("user_dept")
        if (dept_of_user != null) {
            user_department = dept_of_user
        }
        if (username != null) {
            name_user = username
        }
        /**
         * initializing spinner
         */
        initList()
        var spinner : Spinner =findViewById(R.id.search_filter)
        adapter = spinnerAdapter(this,spinnerItems)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {

                // It returns the clicked item.
                val clickedItem: spinner_item = parent.getItemAtPosition(position) as spinner_item
                val name: String = clickedItem.getSpinnerItemName()
                if (name == "Projects") {
                    stype = "Projects"
                }
                else if (name == "Profile") {
                    stype = "Profile"
                }
                else if (name == "Tags") {
                    stype = "Tags"
                }
                if (view != null) {
                    view.isVisible = false
                }
                Toast.makeText(this@RecommendationActivity, "$name selected", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        if (username != null) {
            temp = username
        }
        else
        {
            username = temp
        }

        if (user_type != null) {
            temp2 = user_type
        }
        else {
            user_type = temp2
        }
        val volleyQueue = Volley.newRequestQueue(this)

        /**
         * getting the user areas of interest through an api call to user collection
         */


        val url_user = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"

         val jsonfile_user = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","User")
            put("filter", JSONObject().apply {
                put("username",username)
            })
        }

        val request_user_details : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_user, jsonfile_user,
            Response.Listener<JSONObject> { response ->

                user_interest = response.getJSONObject("document").getJSONArray("areas_of_interest")
                user_github = response.getJSONObject("document").get("github").toString()
                user_linkedin = response.getJSONObject("document").get("linkedin").toString()
                user_pass = response.getJSONObject("document").get("password").toString()
                user_off_email = response.getJSONObject("document").get("email").toString()
            },
            Response.ErrorListener { error ->
                prj_name.text = error.toString()
            },

            ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json");
                headers.put(
                    "api-key",
                    "52y3eVGzd6zZUik2FCunXVfxWCX4Olar386TTdangtvH1xP0Sunj52wOJxNFqr2K"
                );
                headers.put("Access-Control-Request-Headers","*");

                return headers
            }
        }


        val MY_SOCKET_TIMEOUT_MS = 50000;
        request_user_details.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        volleyQueue.add(request_user_details)


        /**
         * navigating to different screens with bottom nav View
         */
        Log.d(null,"user type = " + user_type.toString())
        val main_view : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navView)


        if (user_type == "Student") {
                menuInflater.inflate(R.menu.bottom_menu_student, main_view.menu)
                var intent1 = Intent()
                main_view.selectedItemId = R.id.item1
                main_view.setOnItemSelectedListener(
                    BottomNavigationView.OnNavigationItemSelectedListener {
                        when (it.itemId) {
                            R.id.item1 -> {
                                intent1 = Intent(this, RecommendationActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                startActivity(intent1)
                            }
                            R.id.item2 -> {
                                intent1 = Intent(this, LikedActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                var interests = ArrayList<String>()
                                for (x in 0 until user_interest.length()){
                                    interests.add(user_interest.getString(x))
                                }
                                intent1.putExtra("user_inter",interests)
                                intent1.putExtra("github", user_github)
                                intent1.putExtra("linkedin", user_linkedin)
                                startActivity(intent1)
                            }
                            R.id.item3 -> {
                                intent1 = Intent(this, ProfileActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                startActivity(intent1)
                            }

                            R.id.item4 -> {
                                intent1 = Intent(this, AddIdea::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                startActivity(intent1)
                            }
                        }
                        true
                    }
                )
            } else {
                menuInflater.inflate(R.menu.bottom_menu_faculty, main_view.menu)

                var intent1 = Intent()
                main_view.selectedItemId = R.id.item_fac_1
                main_view.setOnItemSelectedListener(
                    BottomNavigationView.OnNavigationItemSelectedListener {
                        when (it.itemId) {
                            R.id.item_fac_1 -> {
                                intent1 = Intent(this, RecommendationActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                startActivity(intent1)
                            }
                            R.id.item_fac_2 -> {
                                intent1 = Intent(this, LikedActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                var interests = ArrayList<String>()
                                for (x in 0 until user_interest.length()){
                                    interests.add(user_interest.getString(x))
                                }
                                intent1.putExtra("user_inter",interests)
                                intent1.putExtra("github", user_github)
                                intent1.putExtra("linkedin", user_linkedin)
                                startActivity(intent1)
                            }

                            R.id.item_fac_3 -> {
                                intent1 = Intent(this, AddProjectActivity::class.java)
                                intent1.putExtra("username", username)
                                startActivity(intent1)
                            }

                            R.id.item_fac_4 -> {
                                intent1 = Intent(this, StudentRequestActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                startActivity(intent1, null)
                            }
                            R.id.item_fac_5 -> {
                                intent1 = Intent(this, ProfileActivity::class.java)
                                intent1.putExtra("username", username)
                                intent1.putExtra("usertype", user_type)
                                startActivity(intent1)
                            }
                        }
                        true
                    }
                )

            }

        /**
         * recommendation for the user
         */
        if (k.length() == 0) {


            val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"

            lateinit var jsonfile : JSONObject
            if (recom_type == "dept") {
                Log.d(null,"Department = " + dept_of_user)
                jsonfile = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "Project")
                    put("filter", JSONObject().apply {
                        put("dept", dept_of_user)
                    })
                }
            }
            else if (recom_type == "all") {
                jsonfile = JSONObject().apply {
                    put("dataSource","Cluster0")
                    put("database","Intermatch")
                    put("collection","Project")
                    put("filter",JSONObject().apply {

                    })
                }

            }

            else {
                jsonfile = JSONObject().apply {
                    put("dataSource","Cluster0")
                    put("database","Intermatch")
                    put("collection","Project")
                    put("filter",JSONObject().apply {

                    })
                }
            }

            val request : JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url, jsonfile,
                Response.Listener<JSONObject> { response ->
                    k = response.getJSONArray("documents")
                    var you : ArrayList<Int> = ArrayList()
                    for (x in 0 until k.length()) {
                        if (k.getJSONObject(x).get("faculty_name") == username) {
                            you.add(x)
                        }
                    }

                    for (x in 0 until you.size) {
                        k.remove(you.get(x))
                    }

                    if (k.length() != 0) {
                        var indices: ArrayList<Int> = ArrayList<Int>()
                        if (recom_type == "inter") {
                            for (x in 0 until k.length()) {
                                var count1 = 0
                                var tempor = ArrayList<String>()
                                for (y in 0 until k.getJSONObject(x).getJSONArray("domains")
                                    .length()) {
                                    tempor.add(
                                        k.getJSONObject(x).getJSONArray("domains").getString(y)
                                    )
                                }

                                for (y in 0 until user_interest.length()) {
                                    if (user_interest.getString(y) in tempor) {
                                        count1++
                                    }
                                }
                                if (count1 == 0) {
                                    indices.add(x)
                                }

                            }

                            for (x in 0 until indices.size) {
                                k.remove(indices.get(x))
                            }

                            Log.d(null, "Length = " + k.length())
                        }

                        if (recom_type == "all") {
                            Log.d(null, "Length = " + k.length())
                        }


                        for (r in k.length() - 1 downTo 0) {
                            var j = Random.nextInt(r + 1)
                            var temp = k.getJSONObject(j)
                            k.put(j, k.getJSONObject(r))
                            k.put(r, temp)

                        }

                        Log.d(null, k.toString())


                        val prj = k.getJSONObject(intent.getIntExtra("text", i))
                            .get("name").toString()


                        val dept = k.getJSONObject(intent.getIntExtra("text", i))
                            .get("dept").toString()
                        val facname = k.getJSONObject(intent.getIntExtra("text", i))
                            .get("faculty_name").toString()

                        domains = k.getJSONObject(intent.getIntExtra("text", i))
                            .getJSONArray("domains")
                        faculty_email = k.getJSONObject(intent.getIntExtra("text", i))
                            .get("faculty_email").toString()
                        prj_name.text = prj
                        dept_name.text = dept

                        desc = k.getJSONObject(intent.getIntExtra("text", i)).get("desc").toString()

                        val domain_array = k.getJSONObject(intent.getIntExtra("text", i))
                            .getJSONArray("domains")

                        //prj_desc.text = response.getJSONArray("documents").getJSONObject(intent.getIntExtra("text",i))
                        //.get("desc").toString()

                        val temp: ArrayList<String> = ArrayList<String>()
                        var count1 = 0
                        for (i in 0 until domain_array.length()) {
                            temp.add(domain_array.getString(i))
                        }
                        for (i in 0 until user_interest.length()) {
                            if (user_interest.getString(i) in temp) {
                                count1++

                            }
                        }
                        Log.d(null, "count = " + count1.toString())
                        researcher_name.text = facname
                        progressBar.progress =
                            ((count1.toFloat() / domain_array.length()) * 100.0).roundToInt()
                        match_percentage.text =
                            ((count1.toFloat() / domain_array.length()) * 100.0).roundToInt()
                                .toString() + "%"


                        gridView = findViewById(R.id.my_grid_view)
                        arrayList = ArrayList()
                        arrayList = setDataList()
                        languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
                        gridView?.adapter = languageAdapter
                        gridView?.onItemClickListener = this


                        recommendation_layout.isVisible = true

                        dialog.hide()

                    }
                    else {
                        Toast.makeText(this,"You have no project in your recommendations",Toast.LENGTH_LONG).show()
                        dialog.hide()
                    }


                },
                Response.ErrorListener { error ->
                    prj_name.text = error.toString()
                },

                ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers.put("Content-Type", "application/json");
                    headers.put(
                        "api-key",
                        "52y3eVGzd6zZUik2FCunXVfxWCX4Olar386TTdangtvH1xP0Sunj52wOJxNFqr2K"
                    );
                    headers.put("Access-Control-Request-Headers","*");

                    return headers
                }
            }


            val MY_SOCKET_TIMEOUT_MS = 50000;
            request.setRetryPolicy(
                DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            volleyQueue.add(request)
        }

        /**
         * if the array of projects is already retrieved from database
         * No need to retrieve again, retrieve from a local array to minimise response time
         */
        else
        {




            val prj = k.getJSONObject(intent.getIntExtra("text",i))
                .get("name").toString()


            val dept = k.getJSONObject(intent.getIntExtra("text",i))
                .get("dept").toString()
            val facname = k.getJSONObject(intent.getIntExtra("text",i))
                .get("faculty_name").toString()
            domains = k.getJSONObject(intent.getIntExtra("text",i))
                .getJSONArray("domains")

            faculty_email = k.getJSONObject(intent.getIntExtra("text",i))
                .get("faculty_email").toString()
            prj_name.text = prj
            dept_name.text = dept
            researcher_name.text = facname


            desc = k.getJSONObject(intent.getIntExtra("text",i)).get("desc").toString()

            //prj_desc.text = k.getJSONObject(intent.getIntExtra("text",i))
                //.get("desc").toString()


            val domain_array = k.getJSONObject(intent.getIntExtra("text",i))
                .getJSONArray("domains")

            val temp : ArrayList<String> = ArrayList<String>()
            var count1 = 0
            for (i in 0 until domain_array.length()) {
                temp.add(domain_array.getString(i))
            }
            for (i in 0 until user_interest.length()) {
                if (user_interest.getString(i) in temp) {
                    count1++

                }
            }
            Log.d(null,count1.toString())
            progressBar.progress = ((count1.toFloat()/domain_array.length())*100.0).roundToInt()
            match_percentage.text = ((count1.toFloat()/domain_array.length())*100.0).roundToInt().toString() + "%"

            gridView = findViewById(R.id.my_grid_view)
            arrayList = ArrayList()
            arrayList = setDataList()
            languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
            gridView?.adapter = languageAdapter
            gridView?.onItemClickListener = this
            recommendation_layout.isVisible = true
            dialog.hide()
        }


        /**
         * changing the color of like button when pressed
         * if the like button is pressed then it is added to liked projects if it is not already present
         */

        like_btn.setOnClickListener {
            like_count++
            if (like_count % 2 == 1) {
                like_btn.setImageResource(R.drawable.heart_pressed)

                val url2 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                val jsonfile2 = JSONObject().apply {
                    put("dataSource","Cluster0")
                    put("database","Intermatch")
                    put("collection","Liked_projects")
                    put("document", JSONObject().apply {
                        put("name",k.getJSONObject(intent.getIntExtra("text",i)).get("name").toString())
                        put("faculty_name",k.getJSONObject(intent.getIntExtra("text",i)).get("faculty_name").toString())
                        put("username",username)
                    })
                }



                val request2 : JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url2, jsonfile2,
                    Response.Listener<JSONObject> { response2 ->

                        Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                    },
                    Response.ErrorListener { error2 ->
                        Toast.makeText(this,error2.message, Toast.LENGTH_LONG).show()
                    },

                    ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json");
                        headers.put(
                            "api-key",
                            "52y3eVGzd6zZUik2FCunXVfxWCX4Olar386TTdangtvH1xP0Sunj52wOJxNFqr2K"
                        );
                        headers.put("Access-Control-Request-Headers","*");

                        return headers
                    }
                }


                val MY_SOCKET_TIMEOUT_MS = 50000;
                request2.setRetryPolicy(
                    DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )

                volleyQueue.add(request2)
            }
            /**
             * if like button is pressed twice, remove the project from liked projects
             */
            else {

                like_btn.setImageResource(R.drawable.heart__1_)
                if (like_count > 0) {
                    val url2 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/deleteMany"

                    val jsonfile2 = JSONObject().apply {
                        put("dataSource","Cluster0")
                        put("database","Intermatch")
                        put("collection","Liked_projects")
                        put("filter", JSONObject().apply {
                            put("name",k.getJSONObject(intent.getIntExtra("text",i)).get("name").toString())
                            put("faculty_name",k.getJSONObject(intent.getIntExtra("text",i)).get("faculty_name").toString())

                        })
                    }


                    val request2 : JsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url2, jsonfile2,
                        Response.Listener<JSONObject> { response2 ->

                            Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                        },
                        Response.ErrorListener { error2 ->
                            Toast.makeText(this,error2.message, Toast.LENGTH_LONG).show()
                        },

                        ) {
                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            val headers = HashMap<String, String>()
                            headers.put("Content-Type", "application/json");
                            headers.put(
                                "api-key",
                                "52y3eVGzd6zZUik2FCunXVfxWCX4Olar386TTdangtvH1xP0Sunj52wOJxNFqr2K"
                            );
                            headers.put("Access-Control-Request-Headers","*");

                            return headers
                        }
                    }


                    val MY_SOCKET_TIMEOUT_MS = 50000;
                    request2.setRetryPolicy(
                        DefaultRetryPolicy(
                            MY_SOCKET_TIMEOUT_MS,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )

                    volleyQueue.add(request2)
                }
            }

        }









        /**
         * navigating to the search screen when the search item is entered
         */
        searchbtn.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (stype == "Projects") {
                        val intent = Intent(this@RecommendationActivity, SearchActivity::class.java)
                        intent.putExtra("keyword", searchbtn.query.toString())
                        var interests = ArrayList<String>()
                        for (x in 0 until user_interest.length()) {
                            interests.add(user_interest.getString(x))
                        }
                        intent.putExtra("user_interest", interests)
                        intent.putExtra("github", user_github)
                        intent.putExtra("linkedin", user_linkedin)
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                    else if (stype == "Profile") {
                        val intent = Intent(this@RecommendationActivity,ShowProfileActivity::class.java)
                        intent.putExtra("shown_user",searchbtn.query.toString())
                        startActivity(intent)
                    }

                    else if (stype == "Tags") {
                        val intent = Intent(this@RecommendationActivity,SearchTagActivity::class.java)
                        intent.putExtra("tag_key",searchbtn.query.toString())
                        startActivity(intent)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            }
        )



        /**
         * selecting recom type when filter is pressed
         */
        var view1 = layoutInflater.inflate(R.layout.filter_layout,null)
        filter_btn.setOnClickListener {




            alertbuilder.setView(view1)
            alertbuilder.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->
                    if (view1.switch_inter.isChecked == true) {
                        recom_type = "inter"
                        val intent1 = Intent(this,RecommendationActivity::class.java)
                        i = 0
                        while (k.length() > 0) {
                            k.remove(0)
                        }
                        intent1.putExtra("text",i)
                        startActivity(intent1)

                    }
                    else if (view1.switch_dept.isChecked == true){
                        recom_type = "dept"
                        val intent1 = Intent(this,RecommendationActivity::class.java)
                        i = 0
                        while (k.length() > 0) {
                            k.remove(0)
                        }
                        intent1.putExtra("text",i)
                        startActivity(intent1)
                    }
                    else if (view1.switch_allprjs.isChecked == true) {
                        recom_type = "all"
                        val intent1 = Intent(this,RecommendationActivity::class.java)
                        i = 0
                        while (k.length() > 0) {
                            k.remove(0)
                        }
                        intent1.putExtra("text",i)
                        startActivity(intent1)
                    }
                    Toast.makeText(this, recom_type,Toast.LENGTH_LONG).show()
                })
            alertbuilder.create().show()



        }

        /**
         * drop down
         */



        /**
         * sending an email to faculty if setdatak button pressed
         * 1. Takes user to send_email page
         */
        outlook_btn.setOnClickListener {
            val intent : Intent = Intent(this,SendEmailActivity::class.java)
            intent.putExtra("faculty_email", faculty_email)
            intent.putExtra("project_name",prj_name.text)
            intent.putExtra("faculty_name",researcher_name.text)
            intent.putExtra("username",username)
            intent.putExtra("match",match_percentage.text)
            intent.putExtra("github", user_github)
            intent.putExtra("linkedin", user_linkedin)
            startActivity(intent)
        }


        /**
         * displaying an alert dialog which contains the info about the project
         */


        info_btn.setOnClickListener {



            val dialogBuilder = AlertDialog.Builder(this)
// ...Irrelevant code for customizing the buttons and title
            val dialogView = layoutInflater.inflate(R.layout.activity_info_alert, null)
            dialogBuilder.setView(dialogView)

            val editText =  dialogView.findViewById<TextView>(R.id.prj_desc)
            editText.setText(desc)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }

        /**
         * showing profile if the researcher name is clicked
         */
        researcher_name.setOnClickListener{
            startActivity(Intent(this,ShowProfileActivity::class.java).putExtra("shown_user",researcher_name.text))
        }
    }

    /**
     * function for initializing dropdown
     */
    fun initList() {
        spinnerItems = ArrayList<spinner_item>()
        spinnerItems.add(spinner_item("Projects"))
        spinnerItems.add(spinner_item("Profile"))
        spinnerItems.add(spinner_item("Tags"))
    }

    /**
     * function to implement the grid view
     */
    private fun setDataList() : ArrayList<LanguageItem> {
        var arrayList:ArrayList<LanguageItem> = ArrayList()
        for (i in 0 until domains.length()) {
            arrayList.add(LanguageItem(domains.getString(i)))
        }

        return arrayList

    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                x1 = event.x
                y1 = event.y
            }
            else if (event.action == MotionEvent.ACTION_UP) {
                x2 = event.x
                y2 = event.y
                if (x1 < x2) {
                    if (intent.getIntExtra("text",i) >0) {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", --i)


                        startActivity(intent)
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                    }
                    else {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", 0)

                        startActivity(intent)
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                    }
                }
                else if (x1 > x2) {
                    if (intent.getIntExtra("text",i) < k.length()-1) {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", ++i)

                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    else {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", k.length()-1)

                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }

            }

        }

        return super.onTouchEvent(event)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var languageItem: LanguageItem = arrayList!!.get(position)
        Toast.makeText(applicationContext,languageItem.item, Toast.LENGTH_LONG).show()
    }

    }
