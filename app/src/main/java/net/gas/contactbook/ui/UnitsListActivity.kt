package net.gas.contactbook.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook.R
import kotlinx.android.synthetic.main.activity_units_list.*

class UnitsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units_list)
        val unitsNameList = intent.getStringArrayListExtra("ARRAY")
        val arrayAdapter = ArrayAdapter<String>(applicationContext, R.layout.unit_list_item, R.id.txt, unitsNameList)
        unitsListView.adapter = arrayAdapter

    }

}