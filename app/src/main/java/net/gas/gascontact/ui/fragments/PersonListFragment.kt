package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.gas.gascontact.R
import net.gas.gascontact.business.adapters.PersonListAdapterOptimized
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.PersonListFragmentBinding

class PersonListFragment : Fragment() {

    private lateinit var binding: PersonListFragmentBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var listAdapter: PersonListAdapterOptimized


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.person_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val unitID = arguments?.let { PersonListFragmentArgs.fromBundle(it).unitID }
        val departmentID = arguments?.let { PersonListFragmentArgs.fromBundle(it).departmentID }

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.floatingButtonState.value = true
        viewModel.appToolbarStateCallback?.invoke("Сотрудники", true)
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        listAdapter = PersonListAdapterOptimized(viewModel, viewLifecycleOwner)
        if (unitID != null && departmentID != null) {
            viewModel.dataModel.getPersonsEntitiesByIds(unitID, departmentID).observe(viewLifecycleOwner, Observer {
                listAdapter.setupList(it)
                binding.recyclerView.adapter = listAdapter
                viewModel.spinnerState.value = false
            })
        }
    }
}
