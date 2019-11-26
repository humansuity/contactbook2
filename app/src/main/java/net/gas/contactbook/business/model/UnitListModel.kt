package net.gas.contactbook.business.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import net.gas.contactbook.business.database.cores.ContactbookDatabase
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.repository.UnitRepo

class UnitListModel(application: Application) {

    private val unitList: LiveData<List<Units>>


    init {
        val database: ContactbookDatabase? = ContactbookDatabase.getInstance(application.applicationContext)
        unitList = database?.unitsDao()!!.getEntities()
    }





}