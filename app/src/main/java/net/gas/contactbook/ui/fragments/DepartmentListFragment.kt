package net.gas.contactbook.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.DepartmentsListFragmentBinding
import com.example.contactbook.databinding.UnitsListFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.units_list_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.gas.contactbook.business.adapters.DepartmentListAdapter
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.Var

class DepartmentListFragment : Fragment() {


    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: BranchListViewModel


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

        viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)

        when (binding) {
            is DepartmentsListFragmentBinding -> {
                val adapter = DepartmentListAdapter(viewModel)
                binding.apply {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = adapter
                    recyclerView.addItemDecoration(
                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    )
                }
                viewModel.departmentList.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
                viewModel.spinnerState.observe(viewLifecycleOwner, Observer {
                    (binding as DepartmentsListFragmentBinding).progressBar3.isVisible = it
                })
                viewModel.unitEntity.observe(viewLifecycleOwner, Observer {
                    viewModel.toolbarTitle.value = it.name
                })
            }
        }
    }


}