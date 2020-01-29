package net.gas.contactbook.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.example.contactbook.databinding.ActivityUnitsListBinding
import net.gas.contactbook.business.viewmodel.UnitsListViewModel
import net.gas.contactbook.ui.fragments.DepartmentListFragment
import net.gas.contactbook.ui.fragments.UnitListFragment
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var.viewModelFactory

class UnitsListActivity : AppCompatActivity() {

    lateinit var viewModel: UnitsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityUnitsListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_units_list)

        val fragmentManagerHelper = FragmentManagerHelper(this)
        viewModel = ViewModelProvider(this, viewModelFactory {
            UnitsListViewModel(applicationContext, fragmentManagerHelper)
        }).get(UnitsListViewModel::class.java)

        viewModel.fragmentCallback = {
            createDepartmentFragment()
            viewModel.unitFragmentState = true
        }

        if (!viewModel.unitFragmentState)
            createUnitListFragment()
    }


    private fun createUnitListFragment() {
        if (isDestroyed) return
        val fragment = UnitListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
        if (!this.isDestroyed) {
            fragmentTransaction.commit()
        }
    }

    private fun createDepartmentFragment() {
        if (isDestroyed) return
        val fragment = DepartmentListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left, R.animator.enter_from_left, R.animator.exit_to_right)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
        if (!this.isDestroyed) {
            fragmentTransaction.commit()
        }
    }

}