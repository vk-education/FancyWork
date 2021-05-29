package ru.mail.fancywork.ui.primary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mail.fancywork.databinding.WorkItemInRvBinding

class WorksListAdapter(
    private var worksList: List<Int>//dataclasses
) : RecyclerView.Adapter<WorksListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: WorkItemInRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WorkItemInRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(worksList[position]) {
                binding.name.text = "ВЫЩЩЩИВКА"
                binding.difficulty.rating = 4F
                var str = "размер: " + worksList[position] + "x" + worksList[position] + "\nцветов: 5"

                binding.info.text = str

//                binding.cardLayout.setOnClickListener {
//                    //todo открываем активити просмотра
//                    notifyDataSetChanged()
//                }
            }
        }
    }

    override fun getItemCount(): Int {
        return worksList.size
    }
}