package net.gas.gascontact.business.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.gas.gascontact.R
import net.gas.gascontact.business.database.entities.Departments
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.DepartmentRecyclerItemBinding
import net.gas.gascontact.utils.GlideApp

class DepartmentListAdapterOptimized(private val mViewModel: BranchListViewModel) :
    RecyclerView.Adapter<DepartmentListAdapterOptimized.ViewHolder>() {

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

    fun setupList(items: List<Departments>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
