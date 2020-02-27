package net.gas.gascontact.business.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.contactbook.R
import com.example.contactbook.databinding.BranchItemBinding
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.utils.GlideApp

class UnitListAdapter(private val viewModel: BranchListViewModel) :
    DataBoundListAdapter<Units>(diffCallback = object: DiffUtil.ItemCallback<Units>() {

    override fun areItemsTheSame(oldItem: Units, newItem: Units): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Units, newItem: Units): Boolean {
        return oldItem == newItem
    }

}) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.branch_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, item: Units, position: Int) {
        when(binding) {
            is BranchItemBinding -> {
                viewModel.spinnerState.value = true
                if (position % 2 == 0)
                    binding.root.setBackgroundColor(Color.parseColor("#fffef7"))
                else binding.root.setBackgroundColor(Color.parseColor("#F6F4F4"))
                binding.viewModel = viewModel
                binding.id = item.id.toInt()
                binding.unit = item.name
                binding.listType = Units::class.java.name
                GlideApp.with(binding.root.context)
                    .asDrawable()
                    .load(binding.root.context.resources.getDrawable(R.drawable.ic_book_25))
                    .into(binding.imageView)
                viewModel.spinnerState.value = false
            }
        }
    }


}