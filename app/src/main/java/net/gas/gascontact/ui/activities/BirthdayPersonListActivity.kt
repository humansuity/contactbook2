package net.gas.gascontact.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import kotlinx.android.synthetic.main.activity_birthday_person_list.*
import net.gas.gascontact.business.viewmodel.BirthdayViewModel
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.ui.fragments.PersonAdditionalFragment
import net.gas.gascontact.ui.fragments.ViewPagerFragment

class BirthdayPersonListActivity : AppCompatActivity() {

    private lateinit var viewModel: BranchListViewModel
    private var isViewPagerActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birthday_person_list)

        //viewModel = ViewModelProvider(this).get(BranchListViewModel::class.java)
        val viewModel = ViewModelProvider(this).get(BirthdayViewModel::class.java)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_back_20)

//        if (!viewModel.isPersonFragmentActive)
        createViewPagerFragment()
        viewModel.personFragmentCallBack = { createPersonAdditionalFragment() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun createPersonAdditionalFragment() {
        if (isDestroyed) return
        val fragment = PersonAdditionalFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun createViewPagerFragment() {
        if (isDestroyed) return
        val fragment = ViewPagerFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(R.id.fragmentHolder, fragment)
            .commit()
        isViewPagerActive = true
        viewModel.isPersonFragmentActive = true
    }

}