package net.gas.gascontact.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gas.gascontact.R
import net.gas.gascontact.database.entities.Departments
import net.gas.gascontact.database.entities.Units
import net.gas.gascontact.databinding.UnitRecyclerItemBinding
import net.gas.gascontact.view.viewmodel.BranchListViewModel
import java.util.*

class UnitListAdapter(private val mViewModel: BranchListViewModel) :
    RecyclerView.Adapter<UnitListAdapter.ViewHolder>() {


    private lateinit var items: List<Any>

    inner class ViewHolder(val binding: UnitRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            binding.apply {
                when (item) {
                    is Units -> {
                        itemName = item.name
                        itemId = item.id
                        isDepartmentItem = false
                    }
                    is Departments -> {
                        itemName = item.name
                        itemId = item.id
                        isDepartmentItem = true
                        imageView.setImageResource(R.drawable.ic_group_25)
                    }
                }
                viewModel = mViewModel
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UnitRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    fun setupLists(units: List<Units>, departments: List<Departments>?) {
        val regExpressionForListRang = "администрация|аппарат управления|управление".toRegex()
        for ((index, unit) in units.withIndex()) {
            unit.name?.let { name ->
                if (regExpressionForListRang.matches(name.toLowerCase(Locale.ROOT))) {
                    if (units.size > 1) {
                        Collections.swap(units, index, 0)
                    }
                }
            }
        }
        this.items = units
        departments?.let {
            for ((index, department) in departments.withIndex()) {
                department.name?.let { name ->
                    if (regExpressionForListRang.containsMatchIn(name.toLowerCase(Locale.ROOT))) {
                        if (departments.size > 1) Collections.swap(departments, index, 0)
                    }
                }
            }
            this.items = departments + units
        }
        notifyDataSetChanged()
    }


    fun setupListRefactor(units: List<Units>, departments: List<Departments>?) {
        departments?.let {
            items = units + departments
            for ((index, item) in items.withIndex()) {
                checkForMainListItem(item, index, units, departments)
            }
        }
    }


    private fun checkForMainListItem(item: Any, itemIndex: Int, units: List<Units>, departments: List<Departments>) {
        val regExpressionForDepartments = "^администрация".toRegex()
        val regExpressionForUnits = "^аппарат управления|управления".toRegex()
        when (item) {
            is Units -> {
                item.name?.toLowerCase(Locale.ROOT).let {
                    it?.let { nameInLowerCase ->
                        if (regExpressionForUnits.matches(nameInLowerCase))
                            if (units.size > 1) Collections.swap(items, itemIndex, 0)
                    }
                }
            }
            is Departments -> {
                item.name?.toLowerCase(Locale.ROOT).also {
                    it?.let { nameInLowerCase ->
                        if (regExpressionForDepartments.matches(nameInLowerCase))
                            if (departments.size > 1) Collections.swap(items, itemIndex, 0)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}
