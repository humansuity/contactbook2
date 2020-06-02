package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.unit_recycler_item.*
import kotlinx.coroutines.Dispatchers
import net.gas.gascontact.R
import net.gas.gascontact.business.adapters.UnitListAdapterOptimized
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.UnitsListFragmentBinding

open class UnitListFragment : Fragment() {

    private lateinit var binding: UnitsListFragmentBinding
    private val viewModel by activityViewModels<BranchListViewModel>()
    private lateinit var listAdapter: UnitListAdapterOptimized
    private lateinit var unitList: List<Units>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.units_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        viewModel.fragmentType = "UNITS"
        viewModel.floatingButtonState.value = true

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }



        viewModel.getPrimaryUnitList().observe(viewLifecycleOwner, Observer {

            listAdapter = UnitListAdapterOptimized(viewModel)
            listAdapter.setupList(it)
            binding.recyclerView.adapter = listAdapter
            viewModel.spinnerState.value = false
            unitList = it
        })
    }


}
