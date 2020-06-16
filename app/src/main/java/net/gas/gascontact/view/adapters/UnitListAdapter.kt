package net.gas.gascontact.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gas.gascontact.database.entities.Units
import net.gas.gascontact.databinding.UnitRecyclerItemBinding
import net.gas.gascontact.view.viewmodel.BranchListViewModel

class UnitListAdapter(private val mViewModel: BranchListViewModel) :
    RecyclerView.Adapter<UnitListAdapter.ViewHolder>() {

    private lateinit var items: List<Units>

    inner class ViewHolder(val binding: UnitRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Units) {
            binding.apply {
                unitItem = item
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


    fun setupList(items: List<Units>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}
