package net.gas.contactbook.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import net.gas.contactbook.utils.Var

class MainListActivity : AppCompatActivity() {

    private lateinit var viewModel: BranchListViewModel
    private var isOptionMenuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        viewModel = ViewModelProvider(this).get(BranchListViewModel::class.java)

        if (viewModel.checkOpenableDatabase()) {
            if (!viewModel.isUnitFragmentActive)
                createUnitListFragment()
        } else {
            createAlertDialog()
        }

        viewModel.floatingButtonState.observe(this, Observer {
            floatingActionButton.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        viewModel.spinnerState.observe(this, Observer {
            unitProgressbar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        floatingActionButton.setOnClickListener {
            createSearchFragment()
        }

        initCallBacks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCallBacks() {
        viewModel.callIntentCallback = { intent, numberLength ->
            if (numberLength == 13) startActivity(intent)
            else Snackbar.make(unit_list_layout,
                "Невозможно определить номер!", Snackbar.LENGTH_LONG).show()
        }

        viewModel.unitFragmentCallback = {
            createDepartmentFragment()
            viewModel.isUnitFragmentActive = true
        }

        viewModel.addContactIntentCallBack = {
            startActivity(it)
        }

        viewModel.departmentFragmentCallback = {
            createPersonFragment()
        }

        viewModel.personFragmentCallBack = {
            createPersonAdditionalFragment()
        }

        viewModel.checkPermissionsCallBack = {
            if (checkInternetConnection()) {
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Var.STORAGE_PERMISSION_CODE
                        )
                    } else {
                        viewModel.startDownloading()
                    }
                } else {
                    Toast.makeText(applicationContext, "Lollipop", Toast.LENGTH_SHORT).show()
                    viewModel.startDownloading()
                }
            } else { viewModel.onNetworkErrorCallback?.invoke("NO_INTERNET_CONNECTION") }
        }

        viewModel.initUnitFragmentCallback = {
            createUnitListFragment()
        }

        viewModel.optionMenuStateCallback = {
            isOptionMenuVisible = it
            invalidateOptionsMenu()
        }
    }


    private fun checkInternetConnection() : Boolean {
        val connectManager = applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected ?: false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        when (requestCode) {
            Var.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    viewModel.startDownloading()
                }
                else { Snackbar.make(unit_list_layout,
                    "Права не предоставлены!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isOptionMenuVisible) {
            val databaseItem = menu?.findItem(R.id.action_db_update)
            databaseItem?.isVisible = true
            val settingsItem = menu?.findItem(R.id.action_settings)
            settingsItem?.isVisible = true
            val dbInfoItem = menu?.findItem(R.id.db_info)
            dbInfoItem?.isVisible = true
        } else {
            val databaseItem = menu?.findItem(R.id.action_db_update)
            databaseItem?.isVisible = false
            val settingsItem = menu?.findItem(R.id.action_settings)
            settingsItem?.isVisible = false
            val dbInfoItem = menu?.findItem(R.id.db_info)
            dbInfoItem?.isVisible = false
        }
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


    private fun createAlertDialog() {
        if (isDestroyed) return
        val fragment = AlertFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .commit()
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
        floatingActionButton.visibility = View.INVISIBLE
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