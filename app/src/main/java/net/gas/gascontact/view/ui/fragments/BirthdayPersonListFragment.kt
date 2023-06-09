package net.gas.gascontact.view.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.gas.gascontact.R
import net.gas.gascontact.databinding.PersonListFragmentBinding
import net.gas.gascontact.view.adapters.BirthdayPersonListAdapter
import net.gas.gascontact.view.viewmodel.BranchListViewModel

class BirthdayPersonListFragment : Fragment() {

    private lateinit var binding: PersonListFragmentBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var listAdapter: BirthdayPersonListAdapter

    companion object {
        fun newInstance(key: String, value: String): BirthdayPersonListFragment {
            val fragment = BirthdayPersonListFragment()
            fragment.arguments = Bundle().apply { putString(key, value) }
            return fragment
        }
    }

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
        viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)

        viewModel.spinnerState.value = true

        arguments?.takeIf { it.containsKey("PERIOD") }?.apply {
            viewModel.setUpcomingPersonsWithBirthday(getString("PERIOD")!!)
        }

        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")
        viewModel.floatingButtonState.value = false
        viewModel.isUnitFragmentActive = true

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        listAdapter = BirthdayPersonListAdapter(viewModel, viewLifecycleOwner)
        viewModel.birthdayPersonList.observe(viewLifecycleOwner, Observer {
            viewModel.viewModelScope.launch(Dispatchers.Default) {
                delay(200)
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    listAdapter.setupList(it)
                    binding.recyclerView.adapter = listAdapter
                    viewModel.spinnerState.value = false
                }
            }
        })


        viewModel.onPersonItemClickedCallback = { personID ->
            viewModel.dataModel.getPersonEntityById(personID)
                .observe(viewLifecycleOwner, Observer { person ->
                    val bundle = Bundle()
                    bundle.putParcelable("person", person)
                    findNavController().navigate(R.id.actionToAdditionalPersonFragmentGlobal, bundle)
                })
        }

    }


}
