package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.R
import com.example.contactbook.databinding.DepartmentsListFragmentBinding
import kotlinx.android.synthetic.main.units_list_fragment.*
import net.gas.gascontact.business.adapters.DepartmentListAdapterOptimized
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import java.lang.Exception

class DepartmentListFragment : Fragment() {


    private lateinit var binding: DepartmentsListFragmentBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var listAdapter: DepartmentListAdapterOptimized


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

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.appToolbarStateCallback?.invoke("Отделы", true)
        viewModel.floatingButtonState.value = true
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
                    viewModel.departmentList.observe(viewLifecycleOwner, Observer {
                        listAdapter = DepartmentListAdapterOptimized(viewModel)
                        listAdapter.setupList(it)
                        binding.recyclerView.adapter = listAdapter
                        viewModel.spinnerState.value = false
                    })
                }
            })
            return animation
        } catch (e: Exception) {
            viewModel.departmentList.observe(viewLifecycleOwner, Observer {
                listAdapter = DepartmentListAdapterOptimized(viewModel)
                listAdapter.setupList(it)
                binding.recyclerView.adapter = listAdapter
                viewModel.spinnerState.value = false
            })
            return null
        }

    }


}