package net.gas.gascontact.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import net.gas.gascontact.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.ui.fragments.*
import net.gas.gascontact.utils.Var
import java.text.SimpleDateFormat
import java.util.*


class MainListActivity : AppCompatActivity() {
    private lateinit var viewModel: BranchListViewModel
    private var optionItemMenuFlag = ""
    private lateinit var preferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        preferences = getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(BranchListViewModel::class.java)

        initCallBacks()
        viewModel.sharedDatabaseSize = getDatabaseSize()
        viewModel.databaseUpdateTime = getDatabaseUpdateTime()

        if (viewModel.checkOpenableDatabase()) {
            if (!viewModel.isUnitFragmentActive) {
                viewModel.setupPrimaryUnitList()
                createUnitListFragment()
            }
        } else {
            createAlertFragment()
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
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        viewModel.onUnitFragmentBackPressed?.invoke()
        super.onBackPressed()
    }


    private fun getDatabaseSize() : Long {
        return if (preferences.contains(Var.APP_DATABASE_SIZE)) {
            preferences.getLong(Var.APP_DATABASE_SIZE, 0)
        } else 0
    }

    private fun getDatabaseUpdateTime() : String {
        return if (preferences.contains(Var.APP_DATABASE_UPDATE_TIME)) {
            preferences.getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")!!
        } else "Не определена"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCallBacks() {
        viewModel.appToolbarStateCallback = { value, navButtonState ->
            if (navButtonState) main_toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_back_20)
            else main_toolbar.navigationIcon = null
            main_toolbar.title = value
        }

        viewModel.callIntentCallback = {
          startActivity(it)
        }

        viewModel.sendEmailIntentCallback = {
            startActivity(Intent.createChooser(it, "Отправка на email"))
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
                        createLoginFragment("DOWNLOAD")
                    }
                } else {
                    createLoginFragment("DOWNLOAD")
                }
            } else { viewModel.onNetworkErrorCallback?.invoke("NO_INTERNET_CONNECTION") }
        }


        viewModel.optionMenuStateCallback = {
            optionItemMenuFlag = it
            invalidateOptionsMenu()
        }

        viewModel.initUnitFragmentCallback = {
            createAddUnitListFragment()
        }

        viewModel.onReceiveDatabaseSizeCallBack = {
            val editor = preferences.edit()
            editor.putLong(Var.APP_DATABASE_SIZE, it)
            editor.apply()
        }

        viewModel.onDatabaseUpdated = {
            if (it) {
                val dateFormatter = SimpleDateFormat(
                    "dd.MM.yyyy hh:mm a",
                    Locale.forLanguageTag("en")
                )
                val currentDateTime = dateFormatter.format(Date())
                val editor = preferences.edit()
                editor.putString(Var.APP_DATABASE_UPDATE_TIME, currentDateTime)
                editor.apply()

                finish()
                startActivity(intent)
            } else {
                if (supportFragmentManager.backStackEntryCount > 0) {

                    val firstFragment: FragmentManager.BackStackEntry =
                        supportFragmentManager.getBackStackEntryAt(0)

                    supportFragmentManager.popBackStack(
                        firstFragment.id,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    createUnitListFragment()
                } else createUnitListFragment()
            }
        }

        viewModel.onLoginCallback = {
            createLoginFragment("DOWNLOAD")
        }

        viewModel.afterSuccessLoginCallback = {
            createAlertFragment()
        }

        viewModel.onCreateUnitListFragment = {
            createUnitListFragment()
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
                    createLoginFragment("DOWNLOAD")
                }
                else { Snackbar.make(root,
                    "Права не предоставлены!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when (optionItemMenuFlag) {
            "FULLY_VISIBLE" -> {
                val databaseItem = menu?.findItem(R.id.action_db_update)
                databaseItem?.isVisible = true
            }
            else -> {
                menu?.findItem(R.id.action_db_update)?.isVisible = false
                menu?.findItem(R.id.action_settings)?.isVisible = false
                menu?.findItem(R.id.action_birthday)?.isVisible = false
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_db_update -> {
                openAlertDialog()
            }
            R.id.action_settings -> {
                createAboutAppFragment()
            }
            R.id.action_birthday -> {
                createViewPagerFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun openAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_db_update, null)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        val background = ColorDrawable(Color.TRANSPARENT)
        val margins = InsetDrawable(background, 50)
        dialog.window?.setBackgroundDrawable(margins)

        view.findViewById<Button>(R.id.btn_cancel_update).setOnClickListener { dialog.dismiss() }
        view.findViewById<Button>(R.id.btn_ok_update).setOnClickListener {
            if (checkInternetConnection()) {
                createLoginFragment("UPDATE")
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun createAlertFragment() {
        if (isDestroyed) return
        val fragment = AlertFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .commit()
    }


    private fun createUnitListFragment() {
        viewModel.isUnitFragmentActive = true
        if (isDestroyed) return
        val fragment = UnitListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.fragmentHolder, fragment, "INIT_FRAGMENT")
            .commit()
    }


    private fun createAddUnitListFragment() {
        viewModel.appToolbarStateCallback?.invoke("Филиалы", true)
        if (isDestroyed) return
        val fragment = UnitListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(
                R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun createDepartmentFragment() {
        if (isDestroyed) return
        val fragment = DepartmentListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(
                R.animator.enter_from_right, R.animator.exit_to_left,
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
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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

    private fun createAboutAppFragment() {
        if (isDestroyed) return
        val fragment = AboutAppFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun createViewPagerFragment() {
        if (isDestroyed) return
        val fragment = BirthdayPeriodFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun createLoginFragment(type: String) {
        if (isDestroyed) return
        val fragment = LoginFragment.newInstance("TYPE", type)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(null)
            .commit()
    }
}
