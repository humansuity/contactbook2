package net.gas.gascontact.view.adapters

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gas.gascontact.R
import net.gas.gascontact.database.entities.Persons
import net.gas.gascontact.databinding.PersonRecyclerItemBinding
import net.gas.gascontact.utils.GlideApp
import net.gas.gascontact.view.viewmodel.BranchListViewModel

class PersonListAdapterOptimized(
    private val mViewModel: BranchListViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PersonListAdapterOptimized.ViewHolder>() {

    private var list: List<Persons> = emptyList()

    inner class ViewHolder(val binding: PersonRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @ExperimentalStdlibApi
        fun bind(item: Persons) {
            binding.apply {
                personItem = item
                viewModel = mViewModel
                executePendingBindings()
            }
            if (item.photoID != null) {
                mViewModel.getPhotoEntity(item.photoID).observe(viewLifecycleOwner, Observer {
                    GlobalScope.launch(Dispatchers.Default) {
                        val decodedString = withContext(Dispatchers.Default) {
                            it.photo?.decodeToString()
                        }
                        try {
                            val byteArray = withContext(Dispatchers.Default) {
                                Base64.decode(decodedString, Base64.DEFAULT)
                            }
                            launch(Dispatchers.Main) {
                                GlideApp.with(binding.root.context)
                                    .asBitmap()
                                    .placeholder(R.drawable.person_undefined)
                                    .load(byteArray)
                                    .apply(RequestOptions().transform(RoundedCorners(30)))
                                    .into(binding.image)
                            }
                        } catch (e: Exception) {
                            launch(Dispatchers.Main) {
                                GlideApp.with(binding.root.context)
                                    .asDrawable()
                                    .load(binding.root.context.resources.getDrawable(R.drawable.person_undefined))
                                    .into(binding.image)
                            }
                        }
                    }
                })
            } else
                GlideApp.with(binding.root.context)
                    .asDrawable()
                    .load(binding.root.context.resources.getDrawable(R.drawable.person_undefined))
                    .into(binding.image)

            mViewModel.getPostByPersonId(item.postID!!.toInt())
                .observe(viewLifecycleOwner, Observer {
                    if (it != null)
                        binding.postItem = it.name
                    else
                        binding.postItem = "Не определено"
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PersonRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun setupList(items: List<Persons>) {
        list = items
        notifyDataSetChanged()
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size


}
