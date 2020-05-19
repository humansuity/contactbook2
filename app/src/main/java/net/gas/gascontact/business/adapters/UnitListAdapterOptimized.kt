package net.gas.gascontact.business.adapters

import android.graphics.Color
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import net.gas.gascontact.R
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.UnitRecyclerItemBinding
import net.gas.gascontact.utils.GlideApp

class UnitListAdapterOptimized(private val mViewModel: BranchListViewModel)
    : RecyclerView.Adapter<UnitListAdapterOptimized.ViewHolder>() {

    private lateinit var items: List<Units>

    inner class ViewHolder(val binding: UnitRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Units) {
            binding.apply {
                unitItem = item
                viewModel = mViewModel
                loadIcon(binding)
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UnitRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    private fun loadIcon(binding: UnitRecyclerItemBinding) {
        GlideApp.with(binding.root.context)
            .asDrawable()
            .load(binding.root.context.resources.getDrawable(R.drawable.ic_book_25))
            .into(binding.imageView)
    }

    fun setupList(items: List<Units>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}
