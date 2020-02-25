package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.UnitsListFragmentBinding
import kotlinx.android.synthetic.main.units_list_fragment.*
import net.gas.gascontact.business.adapters.UnitListAdapter
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class UnitListFragment : Fragment() {

    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: BranchListViewModel


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

        viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)
        viewModel.setupUnitList()
        viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")


        when (binding) {
            is UnitsListFragmentBinding -> {
                val adapter = UnitListAdapter(viewModel)
                binding.apply {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = adapter
                    recyclerView.addItemDecoration(
                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    )
                }
                viewModel.unitList.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }
        }
        viewModel.floatingButtonState.value = true
    }
}