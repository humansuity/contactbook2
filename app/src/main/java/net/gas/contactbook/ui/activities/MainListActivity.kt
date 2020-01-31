package net.gas.contactbook.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.ui.fragments.DepartmentListFragment
import net.gas.contactbook.ui.fragments.PersonAdditionalFragment
import net.gas.contactbook.ui.fragments.PersonListFragment
import net.gas.contactbook.ui.fragments.UnitListFragment
class MainListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units_list)

        val viewModel = ViewModelProvider(this).get(BranchListViewModel::class.java)

        viewModel.unitFragmentCallback = {
            createDepartmentFragment()
            viewModel.isUnitFragmentActive = true
        }

        viewModel.departmentFragmentCallback = {
            createPersonFragment()
        }

        viewModel.personFragmentCallBack = {
            createPersonAdditionalFragment()
        }

        if (!viewModel.isUnitFragmentActive)
            createUnitListFragment()
    }


    private fun createUnitListFragment() {
        if (isDestroyed) return
        val fragment = UnitListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .commit()
    }

    private fun createDepartmentFragment() {
        if (isDestroyed) return
        val fragment = DepartmentListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun createPersonFragment() {
        if (isDestroyed) return
        val fragment = PersonListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun createPersonAdditionalFragment() {
        if (isDestroyed) return
        val fragment = PersonAdditionalFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }

}