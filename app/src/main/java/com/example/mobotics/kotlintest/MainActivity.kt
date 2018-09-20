package com.example.mobotics.kotlintest

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/*
val reader = BufferedReader(reader)
var line: String? = null;
while ({ line = reader.readLine(); line }() != null) {
    System.out.println(line);
}
*/



class MainActivity : AppCompatActivity() {

    var username=""
    var device_id = ""
    var password = ""
    var fcm_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

   inner class GetUserDetailAsync : AsyncTask<String, String, String>()
   {
       override fun doInBackground(vararg params: String?): String {
           TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       }

   }

    inner class LoginApiCall : AsyncTask<String, String, String>() {


       internal var pdLoading: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()

            //            pdLoading = new ProgressDialog(getActivity());
            //            pdLoading.setMessage("\tVerifying...");
            //            pdLoading.setCancelable(false);
            //            pdLoading.show();

        }

        override fun doInBackground(vararg urls: String): String? {

            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL(urls[0])
                connection = url.openConnection() as HttpURLConnection

                connection.readTimeout = 10000
                connection.connectTimeout = 15000
                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true

                val builder = Uri.Builder()

                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", password)
                        .appendQueryParameter("device_id", device_id)
                        .appendQueryParameter("fcm_id", fcm_id)


                val query = builder.build().query
                val os = connection.outputStream
                val writer = BufferedWriter(
                        OutputStreamWriter(os))
                writer.write(query)
                writer.flush()
                writer.close()
                os.close()
                connection.connect()

                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))

                var line : String? = ""
                val buffer = StringBuffer()

               /* while ((line = reader.readLine()) != null) {
                    buffer.append(line)
                }*/

                do {
                    line = reader.readLine()

                    if (line == null)

                        break

                    else
                        buffer.append(line)
                  //  println(line)

                }
               // while (true)
                while (line == null)

                val finaljson = buffer.toString()

                val parentobjt = JSONObject(finaljson)

                //               ShippingAddressModel shippingAddressModel=new ShippingAddressModel();
                //
                //               shippingAddressModel.setMsg(parentobjt.getString("msg"));
                //               shippingAddressModel.setStatus(parentobjt.getBoolean("status"));

                return finaljson

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            return null
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)

            try {

                val parentobjt = JSONObject(response)

                val status = parentobjt.getString("status")
                // String result = parentobjt.getString("result");
                val msg = parentobjt.getString("msg")

                if (status == "true") {

                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()

                    val editor = getSharedPreferences("truck", Context.MODE_PRIVATE).edit()

                    editor.putString("username", username)

                    editor.putString("device_id", device_id)
                    editor.commit()

                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()

                }

                //Toast.makeText(Login.this,status+result+msg,Toast.LENGTH_LONG).show();

            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("expst", " Nullpointer exception")
            }

        }
    }

}
