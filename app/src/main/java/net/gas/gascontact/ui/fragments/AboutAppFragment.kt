package net.gas.gascontact.ui.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_about_app.*
import net.gas.gascontact.R
import net.gas.gascontact.business.BirthdayAlarmReceiver
import net.gas.gascontact.business.BirthdayNotificationService
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.ui.AlarmHelper
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
        notification_switch.isChecked = AlarmHelper.getNotificationState(requireContext())

        notification_switch.setOnCheckedChangeListener { _, isChecked ->
            //alarmSettingsButton.isEnabled = isChecked
        }

        try {
            val pkgInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            //app_version_text.text = pkgInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            //app_version_text.text = "1.0.0"
        }

        val databaseUpdateTime = requireContext()
            .getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")
        //textDatabaseUpdateTime.text = databaseUpdateTime


//        alarmSettingsButton.setOnClickListener {
//            openNotificationDialog()
//        }

    }


    private fun openNotificationDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_setup_alarm, null)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        val background = ColorDrawable(Color.TRANSPARENT)
        val margins = InsetDrawable(background, 50)
        dialog.window?.setBackgroundDrawable(margins)

        view.findViewById<Button>(R.id.btnSetupForWeekDaysNotifs)
            .setOnClickListener { openTimePicker(AlarmHelper.HOLIDAYS) }

        view.findViewById<Button>(R.id.btnSetupForHolidaysNotifs)
            .setOnClickListener { openTimePicker(AlarmHelper.WEEKDAYS) }

        dialog.show()
    }


    private fun openTimePicker(flag: Int) {
        val date = DateTime()
        TimePickerDialog(requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                setupNewAlarmTime(hourOfDay, minute, flag)
            },
            date.hourOfDay,
            date.minuteOfHour,
            true).show()
    }


    private fun setupNewAlarmTime(hourOfDay: Int, minute: Int, flag: Int) {
        AlarmHelper.setupNewNotificationScheduleTime(
            requireContext(),
            "$hourOfDay:$minute",
            flag
        )
        Snackbar.make(root, "Уведомления настроены для " +
                "${if (flag == AlarmHelper.WEEKDAYS) "будних" else "выходных"} дней, " +
                "время - $hourOfDay:$minute", Snackbar.LENGTH_LONG
        ).show()
    }

}
