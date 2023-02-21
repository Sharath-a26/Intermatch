package com.example.intermatch

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_upload_project.*
import org.json.JSONArray


import org.json.JSONObject

/**
 * register a new user page
 * asks for email, username, password
 * identifies user as Student or faculty from the mail
 */
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        signin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }


        backbutton.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        /**
         * showing an alert dialog for selecting department
         */
        sel_dept.setOnClickListener {
            var departments : Array<CharSequence> = arrayOf("AEE","AIE","ARE","CCE","CHE","CIE","CVI","CSE","CYS","EAC","ECE","EEE","EIE","ELC","MEE")
            var alertbuilder2 = AlertDialog.Builder(this)
            lateinit var dept_selected : String
            alertbuilder2.setTitle("Select Project Department")
            alertbuilder2.setSingleChoiceItems(departments,0,
                DialogInterface.OnClickListener { dialog, which ->
                dept_selected = departments[which] as String
            })

            alertbuilder2.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                sel_dept.text = dept_selected
            })
            alertbuilder2.create().show()
        }

        val volleyQueue = Volley.newRequestQueue(this)
        /**
         * adding new user to the Project Database when signed in
         */
        signButton.setOnClickListener {
            val dialog = ProgressDialog(this)
            dialog.setMessage("Registering user...")
            dialog.setCancelable(false)
            /* dialog.setInverseBackgroundForced(false)*/
            dialog.show()
            val temp : JSONArray = JSONArray(
                listOf(JSONObject().apply {  put("username",username.text.toString())},
                JSONObject().apply {  put("email",email.text.toString())}))
            val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
            val jsonfile_reg = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","User")
                put("filter",JSONObject().apply {

                    put("$"+"or",temp)

                })
            }

            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url, jsonfile_reg,
                Response.Listener<JSONObject> { response ->

                    if (!response.get("document").equals(null)) {
                        dialog.hide()
                        Toast.makeText(this,"User already registered, Please Login to Continue",Toast.LENGTH_LONG).show()
                    }
                    else {
                        if ("amrita.edu" in email.text) {
                            var username = username.text.toString()
                            var user_email = email.text.toString()
                            var password = password.text.toString()

                            var department = sel_dept.text.toString()

                            lateinit var user_type : String
                            // val docfile = JSONObject(doc)
                            if ("students" in user_email) {
                                //docfile.put("Type","Student")
                                user_type = "Student"
                            }
                            else {
                                //docfile.put("Type","Faculty")
                                user_type = "Faculty"
                            }

                            if (!(password.equals("")) && !(username.equals("")) && !(department.equals(""))) {
                                dialog.hide()
                            if (!password.contains(" ")) {
                                dialog.hide()
                                if (password.length > 5) {

                                    val intent1 = Intent(this@RegisterActivity,UploadUserDetails::class.java)
                                    intent1.putExtra("username",username)
                                    intent1.putExtra("user_email",user_email)
                                    intent1.putExtra("password",password)
                                    intent1.putExtra("sel_dept",department)
                                    intent1.putExtra("usertype",user_type)
                                    startActivity(intent1)
                                }
                                else {

                                    Toast.makeText(this,"Password length must be greater than 5 characters",Toast.LENGTH_LONG).show()
                                }
                            }
                            else {
                                dialog.hide()
                                Toast.makeText(this,"Password must not contain any spaces",Toast.LENGTH_LONG).show()
                            }
                            }
                            else {
                                dialog.hide()
                                Toast.makeText(this,"Some fields are empty",Toast.LENGTH_LONG).show()
                            }




                        }

                        else {
                            dialog.hide()
                            Toast.makeText(this,"Please provide your amrita email id",Toast.LENGTH_LONG).show()
                        }
                    }
                },
                Response.ErrorListener { error ->
                    dialog.hide()
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();

                }) {



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
            //changing the default timeout to be 50000ms in volley
            val MY_SOCKET_TIMEOUT_MS = 50000;
            request.setRetryPolicy(
                DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            volleyQueue.add(request)

            /*if ("amrita.edu" in email.text) {
                var username = username.text.toString()
                var user_email = email.text.toString()
                var password = password.text.toString()

                var department = sel_dept.text.toString()
                /*val volleyQueue = Volley.newRequestQueue(this)
                val url =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"*/


               /* val doc = """
                {
        "username":$username,
        "email" : $user_email,
        "password":$password,
        "dept": $department
      }
        
            """.trimIndent()*/
                lateinit var user_type : String
               // val docfile = JSONObject(doc)
                if ("students" in user_email) {
                    //docfile.put("Type","Student")
                    user_type = "Student"
                }
                else {
                    //docfile.put("Type","Faculty")
                    user_type = "Faculty"
                }

               /* val info = """
                {
      "dataSource": "Cluster0",
      "database": "Intermatch",
      "collection": "User",
      "document": ${docfile.toString()}
  } """.trimIndent()*/
                /*
                val jsonfile = JSONObject(info)

                val request: JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url, jsonfile,
                    Response.Listener<JSONObject> { response ->
                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();

                    }) {



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
                //changing the default timeout to be 50000ms in volley
                val MY_SOCKET_TIMEOUT_MS = 50000;
                request.setRetryPolicy(
                    DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )
                volleyQueue.add(request)

                */
                val intent1 = Intent(this@RegisterActivity,UploadUserDetails::class.java)
                intent1.putExtra("username",username)
                intent1.putExtra("user_email",user_email)
                intent1.putExtra("password",password)
                intent1.putExtra("sel_dept",department)
                intent1.putExtra("usertype",user_type)
                startActivity(intent1)



            }

            else {
                Toast.makeText(this,"Please provide your amrita email id",Toast.LENGTH_LONG).show()
            }


*/


        }
    }
}