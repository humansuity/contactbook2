package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonListFragmentBinding
import net.gas.gascontact.business.adapters.DepartmentListAdapterOptimized
import net.gas.gascontact.business.adapters.PersonListAdapter
import net.gas.gascontact.business.adapters.PersonListAdapterOptimized
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import java.lang.Exception
import java.util.*

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

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.floatingButtonState.value = true
        viewModel.appToolbarStateCallback?.invoke("Сотрудники", true)
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        try {
            val animation = AnimationUtils.loadAnimation(context, nextAnim)
            animation.setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    listAdapter = PersonListAdapterOptimized(viewModel)
                    val dict: MutableMap<String, List<Any>> = mutableMapOf()

                    viewModel.personList.observe(viewLifecycleOwner, Observer {
                        dict["persons"] = it
                        if (dict.keys.size == 2) {
                            listAdapter.setupList(dict)
                            binding.recyclerView.adapter = listAdapter
                        }
                    })
                    viewModel.getPostsEntities().observe(viewLifecycleOwner, Observer {
                        dict["posts"] = it
                        if (dict.keys.size == 2) {
                            listAdapter.setupList(dict)
                            binding.recyclerView.adapter = listAdapter
                        }
                    })
                }
            })
            return animation
        } catch (e: Exception) {
            listAdapter = PersonListAdapterOptimized(viewModel)
            val dict: MutableMap<String, List<Any>> = mutableMapOf()

            viewModel.personList.observe(viewLifecycleOwner, Observer {
                dict["persons"] = it
                if (dict.keys.size == 2) {
                    listAdapter.setupList(dict)
                    binding.recyclerView.adapter = listAdapter
                }
            })
            viewModel.getPostsEntities().observe(viewLifecycleOwner, Observer {
                dict["posts"] = it
                if (dict.keys.size == 2) {
                    listAdapter.setupList(dict)
                    binding.recyclerView.adapter = listAdapter
                }
            })
            return null
        }
    }
}