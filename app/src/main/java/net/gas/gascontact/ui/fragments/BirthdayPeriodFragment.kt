package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.gas.gascontact.R
import kotlinx.android.synthetic.main.fragment_viewpager.*
import net.gas.gascontact.business.adapters.BirthdayPagerAdapter
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class BirthdayPeriodFragment : Fragment() {

    private lateinit var viewModel: BranchListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viewpager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.appToolbarStateCallback?.invoke("Сотрудники", true)
        viewModel.floatingButtonState.value = false
        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")
        viewModel.onUnitFragmentBackPressed = {
            if (viewModel.parentId == 0) {
                viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
            }
        }

        val pagerAdapter = BirthdayPagerAdapter(parentFragmentManager)
        viewPager.adapter = pagerAdapter
    }
}
