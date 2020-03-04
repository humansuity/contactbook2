package net.gas.gascontact.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import kotlinx.android.synthetic.main.activity_birthday_person_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.gas.gascontact.business.adapters.BirthdayPagerAdapter
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.ui.fragments.PersonAdditionalFragment

class BirthdayPersonListActivity : AppCompatActivity() {

    private lateinit var viewModel: BranchListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birthday_person_list)

        viewModel = ViewModelProvider(this).get(BranchListViewModel::class.java)
        val pagerAdapter = BirthdayPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_back_20)
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (viewModel.isPersonFragmentActive) viewPager.visibility = View.GONE
        else {
            GlobalScope.launch(Dispatchers.Default) {
                delay(200)
                launch(Dispatchers.Main) { viewPager.visibility = View.VISIBLE }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isPersonFragmentActive) viewPager.visibility = View.GONE
        else {
            GlobalScope.launch(Dispatchers.Default) {
                delay(200)
                launch(Dispatchers.Main) { viewPager.visibility = View.VISIBLE }
            }
        }
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
        viewModel.isPersonFragmentActive = true
        viewPager.visibility = View.GONE
    }

}