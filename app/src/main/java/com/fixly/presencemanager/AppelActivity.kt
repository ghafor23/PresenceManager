package com.fixly.presencemanager

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppelActivity : AppCompatActivity() {

    private lateinit var adapter: StagiaireAdapter
    private lateinit var txtPresents: TextView
    private lateinit var txtAbsents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appel)

        val rv = findViewById<RecyclerView>(R.id.rvStagiaires)
        txtPresents = findViewById(R.id.txtPresents)
        txtAbsents = findViewById(R.id.txtAbsents)

        // Données en mémoire (noms fictifs)
        val stagiaires = mutableListOf(
            Stagiaire("ST001", "AMRANI YOUSSEF", false),
            Stagiaire("ST002", "BENALI SALMA", true),
            Stagiaire("ST003", "ALAOUI OMAR", true),
            Stagiaire("ST004", "EL IDRISSI AYA", false),
            Stagiaire("ST005", "BERRADA LINA", false),
            Stagiaire("ST006", "CHRAIBI KARIM", true),
            Stagiaire("ST007", "FASSI SARA", false),
            Stagiaire("ST008", "HAMDAOUI REDA", true),
            Stagiaire("ST009", "IDRISSI NADIA", false),
            Stagiaire("ST010", "KETTANI ADAM", false),
            Stagiaire("ST011", "LAHLOU IMANE", false),
            Stagiaire("ST012", "MANSOURI HAMZA", true),
            Stagiaire("ST013", "NACIRI MERYEM", true),
            Stagiaire("ST014", "OUAZZANI ANAS", true),
            Stagiaire("ST015", "RAHMANI HIBA", false),
            Stagiaire("ST016", "TAZI YASSINE", true)
        )

        adapter = StagiaireAdapter(stagiaires) { mettreAJourCompteurs() }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<TextView>(R.id.btnTousPresents).setOnClickListener {
            adapter.toutPresent()
        }

        findViewById<TextView>(R.id.btnAnnuler).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnValider).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Appel validé")
                .setMessage(
                    "Présents : ${adapter.nombrePresents()}\n" +
                            "Absents : ${adapter.nombreAbsents()}"
                )
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }

        mettreAJourCompteurs()
    }

    private fun mettreAJourCompteurs() {
        txtPresents.text = "Présents : ${adapter.nombrePresents()}"
        txtAbsents.text = "Absents : ${adapter.nombreAbsents()}"
    }
}