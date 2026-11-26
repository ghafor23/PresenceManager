package com.fixly.presencemanager // <-- GARDER VOTRE LIGNE package EXISTANTE

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StagiaireAdapter(
    private val items: MutableList<Stagiaire>,
    private val onChange: () -> Unit   // appelé à chaque changement de présence
) : RecyclerView.Adapter<StagiaireAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val nom = view.findViewById<TextView>(R.id.txtNom)
        val present = view.findViewById<TextView>(R.id.txtPresent)
        val absent = view.findViewById<TextView>(R.id.txtAbsent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stagiaire, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val stagiaire = items[position]
        holder.nom.text = stagiaire.nom

        // On renseigne TOUTES les propriétés dans chaque branche (recyclage des vues)
        if (stagiaire.present) {
            holder.present.setBackgroundColor(Color.parseColor("#E6F4EA"))
            holder.present.setTextColor(Color.parseColor("#19834A"))
            holder.absent.setBackgroundColor(Color.WHITE)
            holder.absent.setTextColor(Color.parseColor("#6B7280"))
        } else {
            holder.present.setBackgroundColor(Color.WHITE)
            holder.present.setTextColor(Color.parseColor("#6B7280"))
            holder.absent.setBackgroundColor(Color.parseColor("#FDE8E8"))
            holder.absent.setTextColor(Color.parseColor("#CB1111"))
        }

        // bindingAdapterPosition : position valide au moment du clic
        holder.present.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) modifierPresence(pos, true)
        }
        holder.absent.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) modifierPresence(pos, false)
        }
    }

    override fun getItemCount(): Int = items.size

    fun modifierPresence(position: Int, present: Boolean) {
        if (position !in items.indices) return
        if (items[position].present == present) return
        items[position] = items[position].copy(present = present)
        notifyItemChanged(position)
        onChange()
    }

    fun toutPresent() {
        for (i in items.indices) {
            items[i] = items[i].copy(present = true)
        }
        notifyDataSetChanged()
        onChange()
    }

    fun nombrePresents(): Int = items.count { it.present }
    fun nombreAbsents(): Int = items.count { !it.present }
}