package net.gas.contactbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonListFragmentBinding
import kotlinx.android.synthetic.main.units_list_fragment.*
import net.gas.contactbook.business.adapters.PersonListAdapter
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var

class PersonListFragment : Fragment() {

    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: BranchListViewModel


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
        viewModel = ViewModelProvider(
            requireActivity(),
            Var.viewModelFactory {
                BranchListViewModel(
                    context!!,
                    FragmentManagerHelper(context as AppCompatActivity)
                )
            }).get(BranchListViewModel::class.java)



        when (binding) {
            is PersonListFragmentBinding -> {
                val adapter = PersonListAdapter(viewModel)
                binding.apply {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = adapter
                    recyclerView.addItemDecoration(
                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    )
                }
                viewModel.personList.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }
        }
    }
}