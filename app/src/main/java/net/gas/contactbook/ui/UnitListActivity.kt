package net.gas.contactbook.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.ActivityUnitsListBinding
import kotlinx.android.synthetic.main.activity_download.*
import net.gas.contactbook.business.adapters.UnitListAdapter
import net.gas.contactbook.business.network.DataBaseDownloadTask
import net.gas.contactbook.business.viewmodel.UnitListViewModel

class UnitListActivity : AppCompatActivity() {

    lateinit var viewModel: UnitListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityUnitsListBinding = DataBindingUtil.setContentView(this, R.layout.activity_units_list)
        viewModel = ViewModelProvider(this).get(UnitListViewModel::class.java)
        val unitListAdapter = UnitListAdapter(viewModel)

        binding.lifecycleOwner = this
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = unitListAdapter
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.unitList.observe(this, Observer {
            unitListAdapter.submitList(it)
        })
    }
}