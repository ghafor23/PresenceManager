package com.fixly.presencemanager // <-- GARDER VOTRE LIGNE package EXISTANTE

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
// === AJOUT CAB : imports ===
import android.text.InputType
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
// === AJOUT POPUP : imports ===
import android.view.View
import android.widget.PopupMenu

class GroupesActivity : AppCompatActivity() {

    private lateinit var lvGroupes: ListView

    // MODIFIÉ : les données sont sorties des méthodes (listes modifiables)
    // pour que l'ajout et la suppression (CAB / Pop-up) soient conservés.
    private val groupesTexte = arrayListOf(
        "DEVAM201", "DEVAM202", "DEVAM203",
        "DEVAM204", "DEVAM205", "DEVAM206",
        "DEVAM207", "DEVAM208", "DEVAM209", "DEVAM210"
    )
    private val groupesObjets = arrayListOf(
        Groupe("DEVAM201", 24),
        Groupe("DEVAM202", 22),
        Groupe("DEVAM203", 26),
        Groupe("DEVAM204", 23),
        Groupe("DEVAM205", 25),
        Groupe("DEVAM206", 21),
        Groupe("DEVAM207", 20),
        Groupe("DEVAM208", 22),
        Groupe("DEVAM209", 25),
        Groupe("DEVAM210", 23)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupes)

        lvGroupes = findViewById(R.id.lvGroupes)
        val btnArrayAdapter = findViewById<Button>(R.id.btnArrayAdapter)
        val btnBaseAdapter = findViewById<Button>(R.id.btnBaseAdapter)

        // === AJOUT CAB : activer la sélection multiple par appui long ===
        lvGroupes.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        lvGroupes.setMultiChoiceModeListener(cabListener)

        // Clic sur le bouton ArrayAdapter -> Appel de la méthode 1
        btnArrayAdapter.setOnClickListener {
            afficherAvecArrayAdapter()
        }

        // Clic sur le bouton BaseAdapter -> Appel de la méthode 2
        btnBaseAdapter.setOnClickListener {
            afficherAvecBaseAdapter()
        }

        // Affichage initial par défaut avec BaseAdapter
        afficherAvecBaseAdapter()
    }

    // MÉTHODE 1 : ARRAYADAPTER (Liste simple)
    fun afficherAvecArrayAdapter() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            groupesTexte
        )
        lvGroupes.adapter = adapter

        // === AJOUT POPUP : le clic ouvre le menu Pop-up sur l'élément cliqué ===
        // (l'action « Afficher » du menu reprend le Toast du TP05)
        lvGroupes.setOnItemClickListener { _, vue, position, _ ->
            afficherPopupItem(vue, position)
        }
    }

    // MÉTHODE 2 : BASEADAPTER (Liste personnalisée avec ViewHolder)
    fun afficherAvecBaseAdapter() {
        val adapter = GroupeAdapter(this, groupesObjets)
        lvGroupes.adapter = adapter

        // === AJOUT POPUP : le clic ouvre le menu Pop-up sur l'élément cliqué ===
        // (l'action « Afficher » du menu reprend le dialogue du TP05)
        lvGroupes.setOnItemClickListener { _, vue, position, _ ->
            afficherPopupItem(vue, position)
        }
    }

    // ==================================================================
    // === AJOUT POPUP : menu flottant (PopupMenu) sur un élément de la liste
    // ==================================================================
    private fun afficherPopupItem(vue: View, position: Int) {
        val popupMenu = PopupMenu(this, vue)
        popupMenu.menuInflater.inflate(R.menu.menu_popup_groupe, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_afficher_groupe -> {
                    // Comportement d'origine du TP05
                    if (estModeBase()) {
                        val groupe = (lvGroupes.adapter as GroupeAdapter).getItem(position)
                        AlertDialog.Builder(this)
                            .setTitle(groupe.nom)
                            .setMessage("Effectif : ${groupe.effectif} stagiaires")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            groupesTexte[position],
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }
                R.id.action_supprimer_groupe -> {
                    if (estModeBase()) groupesObjets.removeAt(position)
                    else groupesTexte.removeAt(position)
                    (lvGroupes.adapter as BaseAdapter).notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    // ==================================================================
    // === AJOUT CAB : barre d'actions contextuelle (Ajouter / Supprimer)
    // ==================================================================

    private val cabListener = object : AbsListView.MultiChoiceModeListener {

        override fun onItemCheckedStateChanged(
            mode: ActionMode, position: Int, id: Long, checked: Boolean
        ) {
            mode.title = "${lvGroupes.checkedItemCount} sélectionné(s)"
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_cab_groupes, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_supprimer -> {
                    supprimerSelection()
                    mode.finish()
                    true
                }
                R.id.action_ajouter -> {
                    mode.finish()
                    afficherDialogueAjout()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
    }

    // Le mode courant est déduit de l'adaptateur affiché
    private fun estModeBase(): Boolean = lvGroupes.adapter is GroupeAdapter

    private fun supprimerSelection() {
        val positions = lvGroupes.checkedItemPositions
        // Parcours à l'envers pour ne pas décaler les indices
        for (i in positions.size() - 1 downTo 0) {
            if (positions.valueAt(i)) {
                val pos = positions.keyAt(i)
                if (estModeBase()) groupesObjets.removeAt(pos) else groupesTexte.removeAt(pos)
            }
        }
        (lvGroupes.adapter as BaseAdapter).notifyDataSetChanged()
        Toast.makeText(this, "Élément(s) supprimé(s)", Toast.LENGTH_SHORT).show()
    }

    private fun afficherDialogueAjout() {
        val conteneur = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }
        val saisieNom = EditText(this).apply { hint = "Nom du groupe" }
        val saisieEffectif = EditText(this).apply {
            hint = "Effectif"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        conteneur.addView(saisieNom)
        if (estModeBase()) conteneur.addView(saisieEffectif)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un groupe")
            .setView(conteneur)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = saisieNom.text.toString().trim()
                if (nom.isNotEmpty()) {
                    if (estModeBase()) {
                        val effectif = saisieEffectif.text.toString().toIntOrNull() ?: 0
                        groupesObjets.add(Groupe(nom, effectif))
                    } else {
                        groupesTexte.add(nom)
                    }
                    (lvGroupes.adapter as BaseAdapter).notifyDataSetChanged()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}