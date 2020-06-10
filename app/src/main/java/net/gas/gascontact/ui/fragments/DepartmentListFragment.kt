package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.gas.gascontact.R
import net.gas.gascontact.business.adapters.DepartmentListAdapterOptimized
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.DepartmentsListFragmentBinding
import net.gas.gascontact.utils.Var

class DepartmentListFragment : Fragment() {


    private lateinit var binding: DepartmentsListFragmentBinding
    private val viewModel by activityViewModels<BranchListViewModel>()
    private lateinit var listAdapter: DepartmentListAdapterOptimized
    private val screenOrientation by lazy { activity?.resources?.configuration?.orientation!! }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.departments_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.spinnerState.value = true
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.appToolbarStateCallback?.invoke("Отделы", true)
        viewModel.floatingButtonState.value = true
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        Var.hideSpinnerOnOrientationChanged(viewModel, screenOrientation)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        val departmentList = arguments?.let { DepartmentListFragmentArgs.fromBundle(it).departmentList }
        val unitID = arguments?.let { DepartmentListFragmentArgs.fromBundle(it).unitID }

        departmentList?.let {
            listAdapter = DepartmentListAdapterOptimized(viewModel)
            listAdapter.setupList(it.toList())
            binding.recyclerView.adapter = listAdapter
        }


        viewModel.onDepartmentItemClickedCallback = { innerDepartmentID ->
            viewModel.dataModel.getDepartmentSecondaryEntities(innerDepartmentID)
                .observe(viewLifecycleOwner, Observer { departments ->
                    if (!departments.isNullOrEmpty()) {
                        /** Navigation to itself if the selected department has subsidiaries **/
                        unitID?.let {
                            val action = DepartmentListFragmentDirections.actionToSelf(departments.toTypedArray(), unitID)
                            findNavController().navigate(action)
                        }
                    } else {
                        /** Navigation to person list if the selected department has no subsidiaries **/
                        unitID?.let {
                            val action = DepartmentListFragmentDirections
                                .fromDepartmentListFragmentToPersonListFragment(unitID, innerDepartmentID)
                            findNavController().navigate(action)
                        }
                    }
                })
        }

//        viewModel.onDepartmentItemClickedCallback = {
//            if (unitID != null) {
//                val action = DepartmentListFragmentDirections
//                    .fromDepartmentListFragmentToPersonListFragment(unitID, it)
//                findNavController().navigate(action)
//            }
//        }
    }


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation = super.onCreateAnimation(transit, enter, nextAnim)
        if (animation == null && nextAnim != 0)
            animation = AnimationUtils.loadAnimation(requireContext(), nextAnim)

        if (animation != null) {
            view?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                    viewModel.spinnerState.value = false
                }

                override fun onAnimationStart(animation: Animation?) {
                }

            })
        }

        return animation
    }
}
