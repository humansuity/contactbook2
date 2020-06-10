package net.gas.gascontact.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import net.gas.gascontact.R
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.ui.AlarmHelper
import net.gas.gascontact.ui.NotificationHelper
import net.gas.gascontact.ui.fragments.*
import net.gas.gascontact.utils.Var
import java.text.SimpleDateFormat
import java.util.*


class MainListActivity : AppCompatActivity() {
    private val viewModel by viewModels<BranchListViewModel>()
    private val navController by lazy { findNavController(R.id.fragmentHolder) }
    private var optionItemMenuFlag = ""
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        preferences = getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        initResources()

        /** check if app launched from notification **/
        if (!isOpenedViaIntent()) {
            if (!Var.checkIfDatabaseValid(applicationContext, viewModel)) {
                navController.setGraph(R.navigation.app_nav_graph)
                navigateToAlertFragment()
            } else {
                setupStartNavGraph()
            }
        }
    }

    private fun initResources() {
        viewModel.sharedDatabaseSize = getDatabaseSize()
        viewModel.databaseUpdateTime = getDatabaseUpdateTime()

        setupCallbacksAndListeners()
        putAlarmScheduleTimeToPrefs()
        createNotificationChannels()
        setupObservers()
    }


    private fun setupStartNavGraph() {
        viewModel.spinnerState.value = true
        viewModel.getPrimaryUnitList().observe(this, Observer {
            val bundle = Bundle()
            bundle.putParcelableArray("listOfUnits", it.toTypedArray())
            navController.setGraph(R.navigation.app_nav_graph, bundle)
        })
    }


    private fun putAlarmScheduleTimeToPrefs() {
        if (!AlarmHelper.getInitNotificationState(applicationContext)) {
            preferences.edit {
                putString(Var.WEEKDAY_NOTIFICATION_SCHEDULE_TIME, "8:0")
                putString(Var.HOLIDAY_NOTIFICATION_SCHEDULE_TIME, "10:0")
                Log.e("AlarmHelper", "Setting up alarm time preferences")
            }
        }
    }


    private fun isOpenedViaIntent(): Boolean {
        return if (intent.hasExtra("PERSON_ID")) {
            if (Var.checkIfDatabaseValid(applicationContext, viewModel)) {
                viewModel.dataModel.getPersonEntityById(intent.getIntExtra("PERSON_ID", 0))
                    .observe(this, Observer { person ->
                        val bundle = Bundle()
                        bundle.putParcelable("person", person)
                        navController.navigate(R.id.actionToAdditionalPersonFragmentGlobal, bundle)
                    })
                true
            } else false
        } else false
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun getDatabaseSize(): Long {
        return if (preferences.contains(Var.APP_DATABASE_SIZE)) {
            preferences.getLong(Var.APP_DATABASE_SIZE, 0)
        } else 0
    }


    private fun getDatabaseUpdateTime(): String {
        return if (preferences.contains(Var.APP_DATABASE_UPDATE_TIME)) {
            preferences.getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")!!
        } else "Не определена"
    }


    private fun createNotificationChannels() {
        NotificationHelper(applicationContext, emptyList(), emptyList(), emptyList())
            .apply {
                createNotificationChannel(
                    Var.FOREGROUND_NOTIFICATION_SERVICE_CHANNEL,
                    Var.FOREGROUND_NOTIFICATION_NAME,
                    "Управление уведомлениями о днях рождения"
                )
                createNotificationChannel(
                    Var.BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL,
                    Var.BIRTHDAY_NOTIFICATION_NAME,
                    ""
                )
            }
    }


    private fun setupCallbacksAndListeners() {
        viewModel.callIntentCallback = {
            startActivity(it)
        }

        viewModel.sendEmailIntentCallback = {
            startActivity(Intent.createChooser(it, "Отправка на email"))
        }


        viewModel.addContactIntentCallBack = {
            startActivity(it)
        }


        viewModel.checkPermissionsCallBack = {
            if (checkInternetConnection()) {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Var.STORAGE_PERMISSION_CODE
                        )
                    } else {
                        navigateToLoginFragment("DOWNLOAD")
                    }
                } else {
                    navigateToLoginFragment("DOWNLOAD")
                }
            } else {
                viewModel.onNetworkErrorCallback?.invoke("NO_INTERNET_CONNECTION")
            }
        }


        viewModel.optionMenuStateCallback = {
            optionItemMenuFlag = it
            invalidateOptionsMenu()
        }


        viewModel.onReceiveDatabaseSizeCallBack = {
            preferences.edit().putLong(Var.APP_DATABASE_SIZE, it).apply()
        }

        viewModel.onDatabaseUpdated = {
            if (it) {
                val dateFormatter = SimpleDateFormat(
                    "dd.MM.yyyy hh:mm a",
                    Locale.forLanguageTag("en")
                )
                val currentDateTime = dateFormatter.format(Date())
                preferences.edit().putString(Var.APP_DATABASE_UPDATE_TIME, currentDateTime).apply()

                finish()
                viewModel.updateDatabase()
                startActivity(intent)
            } else {
                navigateToStartPoint()
            }
        }

        viewModel.onLoginCallback = {
            navigateToLoginFragment("DOWNLOAD")
        }

        viewModel.afterSuccessLoginCallback = {
            navController.navigateUp()
        }

        viewModel.onCreateUnitListFragment = {
            viewModel.dataModel.updateDatabase()
            navigateToStartPoint()
        }

        viewModel.appToolbarStateCallback = { value, navButtonState ->
            if (navButtonState) main_toolbar.navigationIcon =
                resources.getDrawable(R.drawable.ic_back_20)
            else main_toolbar.navigationIcon = null
            main_toolbar.title = value
        }

        floatingActionButton.setOnClickListener {
            navController.navigate(R.id.actionToSearchFragment)
        }
    }


    private fun setupObservers() {
        viewModel.floatingButtonState.observe(this, Observer {
            floatingActionButton.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        viewModel.spinnerState.observe(this, Observer {
            unitProgressbar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }


    private fun checkInternetConnection(): Boolean {
        val connectManager = applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected ?: false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Var.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    navigateToLoginFragment("DOWNLOAD")
                } else {
                    Snackbar.make(
                        root,
                        "Права не предоставлены!", Snackbar.LENGTH_LONG
                    ).show()
                }
                return
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
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_db_update -> {
                openAlertDialog()
            }
            R.id.action_settings -> {
                navController.navigate(R.id.actionToAboutAppFragment)
            }
            R.id.action_birthday -> {
                navController.navigate(R.id.actionToBirthdayPersonListFragment)
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

        view.findViewById<Button>(R.id.btnCancelUpdate)
            .setOnClickListener { dialog.dismiss() }
        view.findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            if (checkInternetConnection()) {
                navController.navigate(R.id.actionToLoginFragmentGlobal)
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun navigateToLoginFragment(type: String) {
        if (navController.currentDestination?.id == R.id.AlertFragment) {
            val action = AlertFragmentDirections.fromAlertFragmentToLoginFragment(type)
            navController.navigate(action)
            Log.e("Navigation", "Navigate to login fragment")
        }
    }


    private fun navigateToAlertFragment() {
        viewModel.spinnerState.value = false
        if (navController.currentDestination?.id == R.id.UnitListFragment) {
            val action = UnitListFragmentNavigationDirections.fromUnitListFragmentToAlertFragment()
            navController.navigate(action)
        }
        Log.e("Navigation", "Navigate to alert fragment, destination = ${navController.currentDestination}")
    }


    private fun navigateToStartPoint() {
        if (Var.checkIfDatabaseValid(applicationContext, viewModel))
            setupStartNavGraph()
        else
            navController.navigate(R.id.AlertFragment)
    }
}
