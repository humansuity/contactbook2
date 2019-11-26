package net.gas.contactbook.business.repository

import android.app.Application
import androidx.lifecycle.LiveData
import net.gas.contactbook.business.database.cores.ContactbookDatabase
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.model.UnitListModel

class UnitRepo(val database: ContactbookDatabase?) {

    val unitList: LiveData<List<Units>> = database?.unitsDao()!!.getEntities()

}