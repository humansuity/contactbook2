package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.util.Log
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
import net.gas.gascontact.business.adapters.UnitListAdapterOptimized
import net.gas.gascontact.business.database.entities.Units
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
                val unitList = listOf(
                    Units(0, "Pir", "11111", 32312),
                    Units(1, "Pir", "11111", 32312),
                    Units(2, "Pir", "11111", 32312),
                    Units(3, "Pir", "11111", 32312),
                    Units(4, "Pir", "11111", 32312),
                    Units(5, "Pir", "11111", 32312),
                    Units(6, "Pir", "11111", 32312))


                val adapter = UnitListAdapter(viewModel)
                //val adapter = UnitListAdapterOptimized(unitList, viewModel)
                (binding as UnitsListFragmentBinding).recyclerView.layoutManager = LinearLayoutManager(context)
                (binding as UnitsListFragmentBinding).recyclerView.adapter = adapter
                (binding as UnitsListFragmentBinding).recyclerView.addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter.notifyDataSetChanged()

                viewModel.unitList.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }

        }
        viewModel.floatingButtonState.value = true
    }
}