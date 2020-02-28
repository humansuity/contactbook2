package net.gas.gascontact.business.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.databinding.UnitRecyclerItemBinding
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class UnitListAdapterOptimized(private val items: List<Units>, private val viewModel: BranchListViewModel)
    : RecyclerView.Adapter<UnitListAdapterOptimized.ViewHolder>() {

    inner class ViewHolder(val binding: UnitRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Units) {
            binding.unitItem = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
            Log.e("exp", "${item.name}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UnitRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}