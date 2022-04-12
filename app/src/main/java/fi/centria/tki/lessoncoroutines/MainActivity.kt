package fi.centria.tki.lessoncoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
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
            val getresult = httpGet(("http://10.0.2.2:65000"))
            Log.d("result",getresult.toString())

        }
        Picasso.get().load("https://tki.centria.fi/Data/content/Risto%20Hietala_pieni.jpg").into(my_image)

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