package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.SearchFragmentBinding
import kotlinx.android.synthetic.main.search_fragment.*
import net.gas.gascontact.business.adapters.PersonListAdapter
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: BranchListViewModel


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
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        viewModel.appToolbarStateCallback?.invoke("Поиск", true)
        viewModel.isUnitFragmentActive = true

        when (binding) {
            is SearchFragmentBinding -> {
                val adapter = PersonListAdapter(viewModel, viewLifecycleOwner)
                binding.apply {
                    personList.layoutManager = LinearLayoutManager(context)
                    personList.adapter = adapter
                    personList.addItemDecoration(
                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    )
                }
                searchView.setOnClickListener{
                    searchView.isIconified = false
                }
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText!!.isNotEmpty()) {
                            viewModel.getPersonListByTag(newText.trim())
                                .observe(viewLifecycleOwner, Observer {
                                    adapter.submitList(it)
                            })
                            personList.visibility = View.VISIBLE
                            text_alert.visibility = View.INVISIBLE
                        } else {
                            adapter.submitList(emptyList())
                            personList.visibility = View.INVISIBLE
                            text_alert.visibility = View.VISIBLE
                        }
                        (binding as SearchFragmentBinding).personList.smoothScrollToPosition(0)
                        return false
                    }
                })
            }
        }
        viewModel.floatingButtonState.value = false
    }

}