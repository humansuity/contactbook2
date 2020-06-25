package net.gas.gascontact.view.ui.fragments

import android.os.Bundle
import android.util.Log
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
        val departmentList = arguments?.let { UnitListFragmentArgs.fromBundle(it).listOfDepartments }
        binding.apply {
            unitList?.let {
                listAdapter = UnitListAdapter(viewModel)

                if (!departmentList.isNullOrEmpty())
                    listAdapter.setupLists(unitList.toList(), departmentList.toList())
                else
                    listAdapter.setupLists(unitList.toList(), null)

                Log.e("List Adapter", "${departmentList?.size}")
                recyclerView.apply {
                    adapter = listAdapter
                    layoutManager = LinearLayoutManager(context)
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


        viewModel.onUnitItemClickedCallback = { clickedItemId, isDepartment ->
            if (!isDepartment) {
                viewModel.unitIdForUnitListFragment = clickedItemId
                viewModel.dataModel.getUnitSecondaryEntities(clickedItemId).observe(viewLifecycleOwner,
                    Observer { unitList ->
                        if (!unitList.isNullOrEmpty()) {
                            /** Navigation to itself if the selected branch has subsidiaries **/

                            viewModel.getDepartmentListByUnitID(clickedItemId).observe(
                                viewLifecycleOwner,
                                Observer { departmentList ->

                                    if (!departmentList.isNullOrEmpty()) {
                                        val action = UnitListFragmentDirections.actionToSelf(
                                            unitList.toTypedArray(),
                                            departmentList.toTypedArray()
                                        )
                                        findNavController().navigate(action)
                                    } else {
                                        val action = UnitListFragmentDirections.actionToSelf(
                                            unitList.toTypedArray(),
                                            null
                                        )
                                        findNavController().navigate(action)
                                    }

                                }
                            )
                        } else {
                            viewModel.getDepartmentListByUnitID(clickedItemId).observe(
                                viewLifecycleOwner,
                                Observer { primaryDepartments ->
                                    if (!primaryDepartments.isNullOrEmpty()) {
                                        if (primaryDepartments.size > 1) {
                                            val fragmentArguments = Bundle()
                                            fragmentArguments.putParcelableArray(
                                                "departmentList",
                                                primaryDepartments.toTypedArray()
                                            )
                                            fragmentArguments.putInt("unitID", clickedItemId)

                                            findNavController().navigate(
                                                R.id.fromUnitListFragmentToDepartmentListFragment,
                                                fragmentArguments
                                            )
                                        } else {
                                            val action = UnitListFragmentDirections
                                                .fromUnitListFragmentToPersonListFragment(
                                                    clickedItemId,
                                                    primaryDepartments[0].id
                                                )
                                            findNavController().navigate(action)
                                        }
                                    } else {
                                        viewModel.dataModel.getPersonsByUnitId(clickedItemId).observe(
                                            viewLifecycleOwner,
                                            Observer { persons ->
                                                if (!persons.isNullOrEmpty()) {
                                                    val action = UnitListFragmentDirections
                                                        .fromUnitListFragmentToPersonListFragment(
                                                            clickedItemId
                                                        )
                                                    findNavController().navigate(action)
                                                }
                                            }
                                        )
                                    }
                                })
                        }
                    })
            } else {
                viewModel.dataModel.getDepartmentSecondaryEntities(clickedItemId).observe(
                    viewLifecycleOwner,
                    Observer { primaryDepartments ->
                        if (!primaryDepartments.isNullOrEmpty()) {
                            /** Navigation to departmentList if the selected department has subsidiaries **/
                            val fragmentArguments = Bundle()
                            fragmentArguments.putParcelableArray(
                                "departmentList",
                                primaryDepartments.toTypedArray()
                            )
                            fragmentArguments.putInt("unitID", viewModel.unitIdForUnitListFragment)

                            findNavController().navigate(
                                R.id.fromUnitListFragmentToDepartmentListFragment,
                                fragmentArguments
                            )
                        } else {
                            /** Navigation to person list if the selected department has no subsidiaries **/
                            viewModel.dataModel.getUnitIdByDepartmentId(clickedItemId).observe(
                                viewLifecycleOwner,
                                Observer { unitId ->
                                    val action = UnitListFragmentDirections
                                        .fromUnitListFragmentToPersonListFragment(
                                            unitId,
                                            clickedItemId
                                        )
                                    findNavController().navigate(action)
                                }
                            )
                        }
                    }
                )
            }
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