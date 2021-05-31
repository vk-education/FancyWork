package ru.mail.fancywork.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mail.fancywork.databinding.ViewFancyworkBinding
import ru.mail.fancywork.model.datatype.Fancywork

class FancyworkAdapter(
    private var worksList: List<Fancywork>
) : RecyclerView.Adapter<FancyworkAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ViewFancyworkBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewFancyworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(worksList[position]) {
                binding.name.text = title
                binding.difficulty.rating = 4F
                binding.colorText.text = "Цветов: $colors"
                binding.sizeText.text = "Размер: ${width} x $height"

                if (bitmap != null) {
                    binding.image.setImageBitmap(bitmap)
                }

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
