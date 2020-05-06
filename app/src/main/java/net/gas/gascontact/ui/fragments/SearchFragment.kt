package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.gas.gascontact.R
import net.gas.gascontact.databinding.SearchFragmentBinding
import kotlinx.android.synthetic.main.search_fragment.*
import net.gas.gascontact.business.adapters.PersonListAdapter
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: SearchFragmentBinding
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
        viewModel.floatingButtonState.value = false
        viewModel.onUnitFragmentBackPressed = {
            if (viewModel.parentId == 0) {
                viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
            }
        }

        val listAdapter = PersonListAdapter(viewModel, viewLifecycleOwner)

        binding.apply {
            personList.layoutManager = LinearLayoutManager(context)
            personList.adapter = listAdapter
            personList.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
            setupSearchViewListeners(binding, listAdapter)
        }

    }

    private fun setupSearchViewListeners(
        binding: SearchFragmentBinding,
        listAdapter: PersonListAdapter
    ) {
        binding.apply {
            searchView.setOnClickListener {
                searchView.isIconified = false
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    performSearching(newText, listAdapter)
                    return false
                }
            })
        }
    }

    private fun performSearching(searchText: String?, listAdapter: PersonListAdapter) {
        if (searchText!!.isNotEmpty()) {
            if (searchText.length >= 3) {
                /** search by phone number **/
                try {
                    var searchNumber = ""
                    if (!searchText.contains("+"))
                        searchNumber = "+$searchText"
                    else
                        searchNumber = searchText
                    if (searchNumber.toInt() > 0) {
                        viewModel.getPersonListByMobilePhoneTag(searchNumber)
                            .observe(viewLifecycleOwner, Observer {
                                listAdapter.submitList(it)
                                checkListByEmptiness(it)
                                personList.smoothScrollToPosition(0)
                            })
                    } else {
                        listAdapter.submitList(emptyList())
                        personList.visibility = View.INVISIBLE
                        text_alert.apply {
                            text = "Нет результата"
                            visibility = View.VISIBLE
                        }
                    }
                }
                /** search by FIO **/
                catch (e: NumberFormatException) {
                    var resultTag = ""

                    searchText
                        .trim()
                        .forEach { resultTag += "[" + it.toUpperCase() + it.toLowerCase() + "]" }

                    val list = emptyList<Persons>().toMutableList()
                    viewModel.getPersonListByLastNameTag("$resultTag*")
                        .observe(viewLifecycleOwner, Observer { personListByLastName ->
                            list.addAll(personListByLastName)
                            listAdapter.submitList(list.distinct())
                            checkListByEmptiness(list)
                            personList.smoothScrollToPosition(0)
                            viewModel.getPersonListByNameTag("$resultTag*")
                                .observe(viewLifecycleOwner, Observer { personListByName ->
                                    list.addAll(personListByName)
                                    listAdapter.submitList(list.distinct())
                                    checkListByEmptiness(list)
                                    personList.smoothScrollToPosition(0)
                                    viewModel.getPersonListByPatronymicTag("$resultTag*")
                                        .observe(
                                            viewLifecycleOwner,
                                            Observer { personListByPatronymic ->
                                                list.addAll(personListByPatronymic)
                                                listAdapter.submitList(list.distinct())
                                                checkListByEmptiness(list)
                                                personList.smoothScrollToPosition(0)
                                            })
                                })
                        })
                }
            }
        } else {
            listAdapter.submitList(emptyList())
            personList.visibility = View.INVISIBLE
            text_alert.apply {
                text = "Нет результата"
                visibility = View.VISIBLE
            }
        }
    }

    private fun checkListByEmptiness(list: List<Persons>) {
        if (list.isNullOrEmpty()) {
            personList.visibility = View.INVISIBLE
            text_alert.apply {
                text = "Нет результата"
                visibility = View.VISIBLE
            }
        } else {
            personList.visibility = View.VISIBLE
            text_alert.visibility = View.INVISIBLE
        }
    }
}
