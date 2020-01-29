package net.gas.contactbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.UnitsListFragmentBinding
import kotlinx.android.synthetic.main.units_list_fragment.*
import net.gas.contactbook.business.adapters.UnitListAdapter
import net.gas.contactbook.business.viewmodel.UnitsListViewModel
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var

class UnitListFragment : Fragment() {

    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: UnitsListViewModel
    private var isTapped = false
    var onUnitClicked: (() -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.units_list_fragment,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(
            this,
            Var.viewModelFactory {
                UnitsListViewModel(
                    context!!,
                    FragmentManagerHelper(context as AppCompatActivity)
                )
            }
        ).get(UnitsListViewModel::class.java)

        viewModel.firstFragmentCreated.observe(viewLifecycleOwner, Observer<Int> {
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            isTapped = true
            onUnitClicked?.invoke()
        })
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        when (binding) {
            is UnitsListFragmentBinding -> {
                val adapter = UnitListAdapter(viewModel)
                binding.apply {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = adapter
                    recyclerView.addItemDecoration(
                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    )
                }
                viewModel.unitList.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }
        }
    }
}