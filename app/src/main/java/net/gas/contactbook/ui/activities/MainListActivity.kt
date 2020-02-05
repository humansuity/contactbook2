package net.gas.contactbook.ui.activities

import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.ui.fragments.*

class MainListActivity : AppCompatActivity() {

    private lateinit var viewModel: BranchListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        main_toolbar.navigationIcon = getDrawable(R.drawable.ic_back_20)

        viewModel = ViewModelProvider(this).get(BranchListViewModel::class.java)

        viewModel.callIntentCallback = { intent, numberLength ->
            if (numberLength == 13) startActivity(intent)
            else Snackbar.make(unit_list_layout,
                "Невозможно определить номер!", Snackbar.LENGTH_LONG).show()
        }

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

        floatingActionButton.setOnClickListener {
            createSearchFragment()
        }

        viewModel.floatingButtonState.observe(this, Observer {
            floatingActionButton.isVisible = it
        })
        viewModel.spinnerState.observe(this, Observer {
            unitProgressbar.isVisible = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        if (title_text.text == "Меню") {
//            val databaseItem = menu?.findItem(R.id.action_db_update)
//            databaseItem?.isVisible = true
//            val settingsItem = menu?.findItem(R.id.action_settings)
//            settingsItem?.isVisible = true
//            val dbInfoItem = menu?.findItem(R.id.db_info)
//            dbInfoItem?.isVisible = true
//        } else {
//            val databaseItem = menu?.findItem(R.id.action_db_update)
//            databaseItem?.isVisible = false
//            val settingsItem = menu?.findItem(R.id.action_settings)
//            settingsItem?.isVisible = false
//            val dbInfoItem = menu?.findItem(R.id.db_info)
//            dbInfoItem?.isVisible = false
//        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_db_update -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun createUnitListFragment() {
        viewModel.spinnerState.value = true
        if (isDestroyed) return
        val fragment = UnitListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment, "INIT_FRAGMENT")
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
        floatingActionButton.isVisible = false
    }

    private fun createSearchFragment() {
        if (isDestroyed) return
        val fragment = SearchFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }
}