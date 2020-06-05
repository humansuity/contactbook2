package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.coroutines.Dispatchers
import net.gas.gascontact.R
import net.gas.gascontact.business.adapters.DepartmentListAdapterOptimized
import net.gas.gascontact.business.database.entities.Departments
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.DepartmentsListFragmentBinding

class DepartmentListFragment : Fragment() {


    private lateinit var binding: DepartmentsListFragmentBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var listAdapter: DepartmentListAdapterOptimized
    private var departmentList: List<Departments>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.departments_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.appToolbarStateCallback?.invoke("Отделы", true)
        viewModel.floatingButtonState.value = true
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")

        val unitID = arguments?.let { DepartmentListFragmentArgs.fromBundle(it).ID }

        viewModel.onDepartmentItemClickedCallback = { departmentID ->
            if (unitID != null) {
                val action = DepartmentListFragmentDirections
                    .fromDepartmentListFragmentToPersonListFragment(unitID, departmentID)
                findNavController().navigate(action)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        if (unitID != null) {
            viewModel.getDepartmentList(unitID).observe(viewLifecycleOwner, Observer {
                listAdapter = DepartmentListAdapterOptimized(viewModel)
                listAdapter.setupList(it)
                binding.recyclerView.adapter = listAdapter
                viewModel.spinnerState.value = false
                departmentList = it
            })
        }
    }
}
