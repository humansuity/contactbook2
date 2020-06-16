package net.gas.gascontact.view.ui.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.gas.gascontact.R
import net.gas.gascontact.databinding.UnitsListFragmentBinding
import net.gas.gascontact.utils.Constants
import net.gas.gascontact.view.adapters.UnitListAdapter
import net.gas.gascontact.view.viewmodel.BranchListViewModel

class UnitListFragment : Fragment() {

    private lateinit var binding: UnitsListFragmentBinding
    private val viewModel by activityViewModels<BranchListViewModel>()
    private lateinit var listAdapter: UnitListAdapter
    private val screenOrientation by lazy { activity?.resources?.configuration?.orientation!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.spinnerState.value = true
        binding = DataBindingUtil.inflate(
            inflater, R.layout.units_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        viewModel.floatingButtonState.value = true
        Constants.hideSpinnerOnOrientationChanged(viewModel, screenOrientation)

        val unitList = arguments?.let { UnitListFragmentArgs.fromBundle(it).listOfUnits }
        binding.apply {
            if (unitList != null) {
                listAdapter = UnitListAdapter(viewModel)
                listAdapter.setupList(unitList.toList())
                recyclerView.apply {
                    adapter = listAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                }
                viewModel.appToolbarStateCallback
                    ?.invoke("Филиалы", unitList[0].parent_id != 0)
                if (viewModel.isFirstEntry) {
                    viewModel.isFirstEntry = false
                    viewModel.spinnerState.value = false
                }
                viewModel.setNotificationAlarm()
            }
        }

        viewModel.onUnitItemClickedCallback = { unitID ->
            viewModel.dataModel.getUnitSecondaryEntities(unitID).observe(viewLifecycleOwner, Observer { unitList ->
                if (!unitList.isNullOrEmpty()) {
                    /** Navigation to itself if the selected branch has subsidiaries **/
                    val action = UnitListFragmentDirections.actionToSelf(unitList.toTypedArray())
                    findNavController().navigate(action)
                } else {
                    /** Navigation to department list if the selected branch has no subsidiaries **/
                    viewModel.getDepartmentListByUnitID(unitID)
                        .observe(viewLifecycleOwner, Observer { primaryDepartments ->
                            if (!primaryDepartments.isNullOrEmpty()) {
                                val fragmentArguments = Bundle()
                                fragmentArguments.putParcelableArray("departmentList", primaryDepartments.toTypedArray())
                                fragmentArguments.putInt("unitID", unitID)

                                findNavController().navigate(
                                    R.id.fromUnitListFragmentToDepartmentListFragment,
                                    fragmentArguments
                                )
                            }
                        })
                }
            })
        }
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
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                    viewModel.spinnerState.value = false
                }
            })
        }

        return animation
    }

}