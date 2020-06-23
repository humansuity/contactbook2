package net.gas.gascontact.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gas.gascontact.database.entities.Departments
import net.gas.gascontact.database.entities.Units
import net.gas.gascontact.databinding.UnitRecyclerItemBinding
import net.gas.gascontact.view.viewmodel.BranchListViewModel

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
        this.items = units
        departments?.let { this.items = units + departments }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}
