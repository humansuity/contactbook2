package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.UnitsListFragmentBinding
import net.gas.gascontact.business.adapters.UnitListAdapterOptimized
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class UnitListFragment : Fragment() {

    private lateinit var binding: UnitsListFragmentBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var listAdapter: UnitListAdapterOptimized


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
        viewModel.setupUnitList()
        viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        viewModel.floatingButtonState.value = true

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.unitList.observe(viewLifecycleOwner, Observer {
            listAdapter = UnitListAdapterOptimized(viewModel)
            listAdapter.setupList(it)
            binding.recyclerView.adapter = listAdapter
            viewModel.spinnerState.value = false
        })
    }


}