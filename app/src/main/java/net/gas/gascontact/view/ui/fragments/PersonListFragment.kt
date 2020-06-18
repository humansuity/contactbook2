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
import androidx.recyclerview.widget.LinearLayoutManager
import net.gas.gascontact.R
import net.gas.gascontact.databinding.PersonListFragmentBinding
import net.gas.gascontact.utils.Constants
import net.gas.gascontact.view.adapters.PersonListAdapterOptimized
import net.gas.gascontact.view.viewmodel.BranchListViewModel

class PersonListFragment : Fragment() {

    private lateinit var binding: PersonListFragmentBinding
    private val viewModel by activityViewModels<BranchListViewModel>()
    private lateinit var listAdapter: PersonListAdapterOptimized
    private val screenOrientation by lazy { activity?.resources?.configuration?.orientation!! }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.person_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.spinnerState.value = true
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val unitID = arguments?.let { PersonListFragmentArgs.fromBundle(it).unitID }
        val departmentID = arguments?.let { PersonListFragmentArgs.fromBundle(it).departmentID }

        viewModel.floatingButtonState.value = true
        viewModel.appToolbarStateCallback?.invoke("Сотрудники", true)
        viewModel.optionMenuStateCallback?.invoke("FULLY_VISIBLE")
        Constants.hideSpinnerOnOrientationChanged(viewModel, screenOrientation)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            //addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        if (unitID != null && departmentID != null) {
            viewModel.dataModel.getPersonsEntitiesByIds(unitID, departmentID)
                .observe(viewLifecycleOwner, Observer {
                    listAdapter = PersonListAdapterOptimized(viewModel, viewLifecycleOwner)
                    listAdapter.setupList(it)
                    binding.recyclerView.adapter = listAdapter
                })
        }

        viewModel.onPersonItemClickedCallback = { personID ->
            viewModel.dataModel.getPersonEntityById(personID)
                .observe(viewLifecycleOwner, Observer { person ->
                    val action = PersonListFragmentDirections
                        .fromPersonListFragmentToPersonAdditionalFragment(person)
                    findNavController().navigate(action)
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
