package net.gas.gascontact.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        val regExpressionForListRang = "администрация|управление|управления".toRegex()
        for ((index, unit) in units.withIndex()) {
            unit.name?.let { name ->
                if (regExpressionForListRang.containsMatchIn(name.toLowerCase(Locale.ROOT))) {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}
