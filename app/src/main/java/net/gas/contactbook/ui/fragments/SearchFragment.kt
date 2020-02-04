package net.gas.contactbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.example.contactbook.databinding.SearchFragmentBinding
import com.example.contactbook.databinding.UnitsListFragmentBinding
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.units_list_fragment.*
import kotlinx.android.synthetic.main.units_list_fragment.recyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.gas.contactbook.business.adapters.PersonListAdapter
import net.gas.contactbook.business.viewmodel.BranchListViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: BranchListViewModel
    var searchFragmentCallBack: (() -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(
            inflater, R.layout.search_fragment,
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
            is SearchFragmentBinding -> {
                val adapter = PersonListAdapter(viewModel, viewLifecycleOwner)
                binding.apply {
                    person_list.layoutManager = LinearLayoutManager(context)
                    person_list.adapter = adapter
                    person_list.addItemDecoration(
                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    )
                }
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText!!.isNotEmpty()) {
                            viewModel.findPersonsByTag("$newText%")
                                .observe(viewLifecycleOwner, Observer {
                                    adapter.submitList(it)
                            })
                        } else {
                            adapter.submitList(emptyList())
                        }
                        return false
                    }
                })
            }
        }
        searchFragmentCallBack?.invoke()
    }
}