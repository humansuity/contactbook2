package net.gas.contactbook.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.example.contactbook.databinding.ActivityUnitsListBinding
import net.gas.contactbook.business.viewmodel.UnitsListViewModel
import net.gas.contactbook.ui.fragments.DepartmentListFragment
import net.gas.contactbook.ui.fragments.UnitListFragment
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var.viewModelFactory

class UnitsListActivity : AppCompatActivity() {

    private lateinit var viewModel: UnitsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityUnitsListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_units_list)

        val fragmentManagerHelper = FragmentManagerHelper(this)
        viewModel = ViewModelProvider(this, viewModelFactory {
            UnitsListViewModel(applicationContext, fragmentManagerHelper) })
            .get(UnitsListViewModel::class.java)

        createUnitListFragment()
    }


    private fun createUnitListFragment() {
        if (isDestroyed) { Log.e("destroy", "DESTROYED!"); return }
        else { Log.e("active", "fragment created!") }

        val fragment = UnitListFragment()
        fragment.onUnitClicked = {
            createDepartmentFragment()
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            add(R.id.fragmentHolder, fragment)
            commit()
        }
    }

    private fun createDepartmentFragment() {
        if (isDestroyed) { Log.e("destroy", "DESTROYED!"); return }
        val fragment = DepartmentListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            replace(R.id.fragmentHolder, fragment)
            commit()
        }
    }

}