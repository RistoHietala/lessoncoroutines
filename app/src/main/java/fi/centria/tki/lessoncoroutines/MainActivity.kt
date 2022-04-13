package fi.centria.tki.lessoncoroutines

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var my_image :ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        my_image = findViewById(R.id.my_image)
        lifecycleScope.launch {
            //val getresult = httpGet(("http://10.0.2.2:65000"))
            //Log.d("result",getresult.toString())
            //sendPost("http://10.0.2.2:65000/login")

        }
        //testokHttp()
        //Picasso.get().load("https://tki.centria.fi/Data/content/Risto%20Hietala_pieni.jpg").into(my_image)

        val text = readJsonFile(this,"route.json")
        val gson = Gson()
        var routeType = object: TypeToken<ArrayList<Route>>() {}.type
        var routes: ArrayList<Route> = gson.fromJson(text, routeType)
        Log.d("routes",routes.toString())
        //Log.d("file:", text.toString())
        /*var token = JSONTokener(text)
        var array = token.nextValue() as JSONArray
        for (x in 0 until array.length()){
            Log.d("array item", array.get(x).toString())
            var route: JSONObject = array.getJSONObject(x)
            var id = route.optString("id")
            var day = route.optString("day")
            var name = route.optString("name")
            var ti = route.optString("time")
            var points: JSONArray = route.getJSONArray("points")
            for (y in 0 until points.length())
            {
                Log.d("points item",points.get(y).toString())
                var point: JSONObject = points.getJSONObject(y)
            }
        }*/

    }

    fun readJsonFile(context: Context, fileName: String): String?
    {
        val jsonString: String
        try{
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
        catch (ioException: IOException){
            ioException.printStackTrace()
            return null
        }


        return jsonString
    }

    fun testokHttp()
    {
        val client = OkHttpClient()
        fun run(){
            val request = Request.Builder()
                .url("http://10.0.2.2:65000")
                .build()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use{
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        for ((name, value) in response.headers){
                            Log.d("response header","$name: $value")
                        }
                        Log.d("response body",response.body!!.string())
                    }
                }
            })
        }
        run()
    }

    suspend fun sendPost(url: String){
        var json: JSONObject = JSONObject()
        json.put("username","test")
        json.put("password","testpass")
        var array: JSONArray = JSONArray()
        val ret = requestPOST(url,json)
        Log.d("return string",ret.toString())
    }

    suspend fun requestPOST(myURL: String?, postData: JSONObject):String?{
        val result = withContext(Dispatchers.IO){
            var url = URL(myURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.readTimeout = 3000
            conn.connectTimeout = 3000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true
            val os: OutputStream = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(postData.toString())
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = conn.responseCode // check for 200
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                val inp = BufferedReader(InputStreamReader(conn.inputStream))
                val sb = StringBuffer("")
                var line: String? = ""
                while(inp.readLine().also{line = it} != null){
                    sb.append(line)
                    break
                }
                inp.close()
                conn.disconnect()
                sb.toString()
            }
            else{
                conn.disconnect()
                "Failed"
            }

        }
        return result
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {

        val bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(inputStream))
        var line:String? = bufferedReader?.readLine()
        var result:String = ""
        while (line != null) {
            result += line
            line = bufferedReader?.readLine()
        }
        inputStream.close()
        Log.d("input",result.toString())
        return result
    }

    suspend fun httpGet(myURL: String?): String?{
        try{
            val result = withContext(Dispatchers.IO){
                val inputStream: InputStream
                val url: URL = URL(myURL)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                //conn.connectTimeout = 5000
                //conn.readTimeout = 5000
                conn.connect()
                inputStream = conn.inputStream
                if (inputStream != null){
                    convertInputStreamToString(inputStream)
                }
                else{
                    "Did not work"
                }


            }
            return result
        }
        catch (e: Exception){
            e.printStackTrace()
            return ""
        }
    }
}