package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.gas.gascontact.R
import net.gas.gascontact.business.adapters.UnitListAdapterOptimized
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.UnitsListFragmentBinding

class UnitListFragmentNavigation : Fragment() {

    private lateinit var binding: UnitsListFragmentBinding
    private val viewModel by activityViewModels<BranchListViewModel>()
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


        val unitList = arguments?.let { UnitListFragmentNavigationArgs.fromBundle(it).listOfUnits }
        binding.apply {
            listAdapter = UnitListAdapterOptimized(viewModel)
            unitList?.toList()?.let { listAdapter.setupList(it) }
            recyclerView.adapter = listAdapter
            viewModel.spinnerState.value = false
        }


        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        viewModel.floatingButtonState.value = true

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }



        viewModel.onUnitItemClickedCallback = { id ->
            viewModel.dataModel.getSecondaryEntities(id).observe(viewLifecycleOwner, Observer { unitList ->
                if (!unitList.isNullOrEmpty()) {
                    val action = UnitListFragmentNavigationDirections.actionToSelf(unitList.toTypedArray())
                    viewModel.isPrimaryList = false
                    findNavController().navigate(action)
                } else {
                    val arguments = Bundle()
                    arguments.putInt("ID", id)
                    findNavController().navigate(R.id.fromUnitListFragmentToDepartmentListFragment, arguments)
                }
            })
        }
    }

}