package net.gas.gascontact.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gas.gascontact.database.entities.Departments
import net.gas.gascontact.databinding.DepartmentRecyclerItemBinding
import net.gas.gascontact.view.viewmodel.BranchListViewModel
import java.util.*

class DepartmentListAdapter(private val mViewModel: BranchListViewModel) :
    RecyclerView.Adapter<DepartmentListAdapter.ViewHolder>() {

    private lateinit var items: List<Departments>

    inner class ViewHolder(val binding: DepartmentRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Departments) {
            binding.apply {
                departmentItem = item
                viewModel = mViewModel
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DepartmentRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun setupList(departments: List<Departments>) {
        for ((index, department) in departments.withIndex()) {
            val regExpressionForListRang = "администрация|управление|управления".toRegex()
            department.name?.let { name ->
                if (regExpressionForListRang.containsMatchIn(name.toLowerCase(Locale.ROOT))) {
                    if (departments.size > 1) Collections.swap(departments, index, 0)
                }
            }
        }
        this.items = departments
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
