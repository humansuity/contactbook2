package net.gas.contactbook.ui.download

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook_kotlin.R
import kotlinx.android.synthetic.main.activity_download.*

class DownloadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
    }






    fun toastMe(view: View) {
        val myToast = Toast.makeText(this, "Ожидание подключения", Toast.LENGTH_SHORT )
        myToast.show()
    }


}
