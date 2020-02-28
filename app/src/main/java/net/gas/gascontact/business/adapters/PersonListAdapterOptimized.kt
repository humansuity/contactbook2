package net.gas.gascontact.business.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.databinding.PersonRecyclerItemBinding
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class PersonListAdapterOptimized(private val mViewModel: BranchListViewModel)
    : RecyclerView.Adapter<PersonListAdapterOptimized.ViewHolder>() {

    private lateinit var lists: MutableMap<String, List<Any>>

    inner class ViewHolder(val binding: PersonRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Persons) {
            var post: String = ""
            for (it in (lists["posts"] as List<Posts>)) {
                if (it.id == item.postID) {
                    post = if (it.name.isNullOrBlank()) "Не указана" else it.name
                    break
                }
            }
            binding.apply {
                personItem = item
                postItem = post
                viewModel = mViewModel
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PersonRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun setupList(items: MutableMap<String, List<Any>>) {
        lists = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind((lists["persons"] as List<Persons>)[position])
    }

    override fun getItemCount(): Int = (lists["persons"] as List<Persons>).size
}