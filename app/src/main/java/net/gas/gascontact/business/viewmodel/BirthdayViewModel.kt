package net.gas.gascontact.business.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.model.DataModel

class BirthdayViewModel(application: Application) : AndroidViewModel(application) {

    val context: Context = application.applicationContext
    private val dataModel = DataModel(context)

    var personEntity: LiveData<Persons> = MutableLiveData<Persons>()

    private var isBirthViewPagerActive = false
    var callIntentCallback: ((Intent) -> Unit)? = null
    var sendEmailIntentCallback: ((Intent) -> Unit)? = null
    var personFragmentCallBack: (() -> Unit)? = null



    fun makePhoneCall(intent: Intent) {
        callIntentCallback?.invoke(intent)
    }

    fun sendEmail(intent: Intent) {
        sendEmailIntentCallback?.invoke(intent)
    }


    fun onPersonItemClick(id: Int) {
        personEntity = liveData { emitSource(dataModel.getPersonEntityById(id)) }
        personFragmentCallBack?.invoke()
    }

}