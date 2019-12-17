package net.gas.contactbook.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.example.contactbook.databinding.ActivityUnitsListBinding
import net.gas.contactbook.business.viewmodel.UnitsListViewModel
import net.gas.contactbook.ui.fragments.UnitListFragment
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var
import net.gas.contactbook.utils.Var.viewModelFactory

class UnitsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentManagerHelper = FragmentManagerHelper(this)
        val viewModel = ViewModelProvider(this, viewModelFactory {
            UnitsListViewModel(applicationContext, fragmentManagerHelper) })
            .get(UnitsListViewModel::class.java)
        val binding: ActivityUnitsListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_units_list)

        createUnitListFragment()
    }


    private fun createUnitListFragment() {
        val fragment = UnitListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(R.id.fragmentHolder, fragment)
        if (!this.isDestroyed) {
            fragmentTransaction.commit()
        }
    }

}