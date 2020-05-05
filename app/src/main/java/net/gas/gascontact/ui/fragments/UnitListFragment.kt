package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import net.gas.gascontact.R
import net.gas.gascontact.databinding.UnitsListFragmentBinding
import net.gas.gascontact.business.adapters.UnitListAdapterOptimized
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class UnitListFragment : Fragment() {

    private lateinit var binding: UnitsListFragmentBinding
    private lateinit var viewModel: BranchListViewModel
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

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        viewModel.floatingButtonState.value = true
        viewModel.onUnitFragmentBackPressed = {
            if (!unitList.isNullOrEmpty()) {
                if (unitList[0].parent_id != 0) {
                    viewModel.unitList = liveData(Dispatchers.IO) {
                        emitSource(viewModel.dataModel.getUnitEntitiesByParentId(unitList[0].parent_id!!))
                    }
                    viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
                }
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.unitList.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                if (it.isNullOrEmpty()) {
                    viewModel.departmentList = liveData(Dispatchers.IO) {
                        emitSource(viewModel.dataModel.getDepartmentEntitiesById(viewModel.unitId))
                    }
                    viewModel.unitFragmentCallback?.invoke()
                }
            }

            listAdapter = UnitListAdapterOptimized(viewModel)
            listAdapter.setupList(it)
            binding.recyclerView.adapter = listAdapter
            viewModel.spinnerState.value = false
            unitList = it
        })
    }


}
