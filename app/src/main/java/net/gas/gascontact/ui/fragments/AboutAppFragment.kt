package net.gas.gascontact.ui.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_about_app.*
import net.gas.gascontact.R
import net.gas.gascontact.business.BirthdayAlarmReceiver
import net.gas.gascontact.business.BirthdayNotificationService
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.utils.Var
import org.joda.time.DateTime

class AboutAppFragment : Fragment() {

    private lateinit var viewModel: BranchListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about_app, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)


        viewModel.appToolbarStateCallback?.invoke("Настройки", true)
        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")
        viewModel.floatingButtonState.value = false
        viewModel.isUnitFragmentActive = true
        viewModel.onUnitFragmentBackPressed = {
            if (viewModel.parentId == 0) {
                viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
            }
        }

        val preferences = requireContext().getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)

        if (preferences.contains(Var.APP_NOTIFICATION_ALARM_STATE)) {
            notification_switch.isChecked = preferences.getBoolean(Var.APP_NOTIFICATION_ALARM_STATE, false)
        }

        notification_switch.setOnCheckedChangeListener { _, isChecked ->
            alarmSettingsButton.isEnabled = isChecked
            if (!isChecked) {
                cancelBirthdayNotification()
                preferences.edit().putBoolean(Var.APP_NOTIFICATION_ALARM_STATE, false).apply()
                Snackbar.make(root, "Уведомления отключены", Snackbar.LENGTH_LONG).show()
                Log.e("Alarm manager", "Alarm manager was canceled")
            } else {
                preferences.edit().putBoolean(Var.APP_NOTIFICATION_ALARM_STATE, true).apply()
                Var.setNotificationAlarm(requireContext())
                Snackbar.make(root, "Уведомления активны (8:00)", Snackbar.LENGTH_LONG).show()
                Log.e("Alarm manager", "Alarm manager has been initialized at 8:00")
            }
        }

        try {
            val pkgInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            app_version_text.text = pkgInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            app_version_text.text = "1.0.0"
        }

        val databaseUpdateTime = requireContext()
            .getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")
        textDatabaseUpdateTime.text = databaseUpdateTime


        alarmSettingsButton.setOnClickListener {
            val date = DateTime()
            TimePickerDialog(requireContext(),
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    cancelBirthdayNotification()
                    Var.setNotificationAlarm(requireContext(), hourOfDay, minute)
                    Snackbar.make(root, "Уведомления настроены, время - $hourOfDay:$minute", Snackbar.LENGTH_LONG).show()
                },
                date.hourOfDay,
                date.minuteOfHour,
                true).show()
        }

    }

    private fun cancelBirthdayNotification() {
        val alarmManager = requireContext()
            .getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            Var.NOTIFICATION_SERVICE_ID,
            Intent(requireContext(), BirthdayAlarmReceiver::class.java),
            0
        )
        requireContext().stopService(
            Intent(requireContext(), BirthdayNotificationService::class.java)
        )
        alarmManager?.cancel(pendingIntent)
    }
}
