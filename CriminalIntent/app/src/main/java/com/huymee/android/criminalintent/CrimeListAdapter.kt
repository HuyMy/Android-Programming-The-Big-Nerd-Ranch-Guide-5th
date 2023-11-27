package com.huymee.android.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.huymee.android.criminalintent.databinding.ListItemCrimeBinding
import com.huymee.android.criminalintent.databinding.ListItemSeriousCrimeBinding

private const val VIEW_TYPE_NORMAL = 0
private const val VIEW_TYPE_SERIOUS = 1

class CrimeListAdapter(private val crimes: List<Crime>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_NORMAL) {
            val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
            CrimeHolder(binding)
        } else {
            val binding = ListItemSeriousCrimeBinding.inflate(inflater, parent, false)
            SeriousCrimeHolder(binding)
        }

    }

    override fun getItemCount() = crimes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val crime = crimes[position]
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_NORMAL) {
            (holder as CrimeHolder).bind(crime)
        } else {
            (holder as SeriousCrimeHolder).bind(crime)
        }
    }

    override fun getItemViewType(position: Int) = when {
        crimes[position].requiresPolice -> VIEW_TYPE_SERIOUS
        else -> VIEW_TYPE_NORMAL
    }
}

class CrimeHolder(
    private val binding: ListItemCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            crimeTitle.text = crime.title
            crimeDate.text = crime.date.toString()

            root.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "${crime.title} clicked!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class SeriousCrimeHolder(
    private val binding: ListItemSeriousCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            crimeTitle.text = crime.title
            crimeDate.text = crime.date.toString()

            root.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "${crime.title} clicked!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            callPoliceButton.setOnClickListener {
                Toast.makeText(
                    root.context, "Calling Police...", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}