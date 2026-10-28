package com.ink.suivichantier

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var db: Database
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private var selectedCategory = "Personnel"
    private val categories = listOf("Chantiers", "Personnel", "Transport du personnel", "Matériel interne", "Transport des matériaux", "Transferts", "Loyer personnel", "Véhicules utilitaires", "Matériel de location", "Matière première", "Matière consommable")
    private val green = 0xFF17483F.toInt()
    private val pale = 0xFFE5EEE9.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Database(this)
        buildShell()
        showDashboard()
    }

    private fun buildShell() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(0xFFF3F6F4.toInt()) }
        val header = TextView(this).apply { text = "🛣  SUIVI DE CHANTIER ROUTIER\nGestion opérationnelle · MAD · Hors ligne"; setTextColor(-1); textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setPadding(18, 22, 18, 20); setBackgroundColor(green) }
        root.addView(header)
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(14, 14, 14, 24) }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(5, 5, 5, 5); setBackgroundColor(green) }
        listOf("Accueil", "Nouvelle saisie", "Historique").forEachIndexed { i, title ->
            val b = Button(this).apply { text = title; textSize = 10f; isAllCaps = false; setOnClickListener { when(i) { 0 -> showDashboard(); 1 -> showForm(); else -> showHistory() } } }
            nav.addView(b, LinearLayout.LayoutParams(0, -2, 1f))
        }
        root.addView(nav)
        setContentView(root)
    }

    private fun title(text: String, sub: String? = null) {
        content.removeAllViews()
        content.addView(TextView(this).apply { this.text = text; textSize = 23f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF172521.toInt()); setPadding(2, 6, 2, 3) })
        if (sub != null) content.addView(TextView(this).apply { text = sub; textSize = 12f; setTextColor(0xFF64736C.toInt()); setPadding(2, 0, 2, 14) })
    }

    private fun button(text: String, action: () -> Unit) {
        content.addView(Button(this).apply { this.text = text; isAllCaps = false; setOnClickListener { action() } }, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 5, 0, 5) })
    }
    private fun metric(label: String, value: String) {
        content.addView(TextView(this).apply { text = "$label\n$value"; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(green); setBackgroundColor(-1); setPadding(15, 14, 15, 14) }, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 5, 0, 5) })
    }
    private fun showDashboard() {
        title("Tableau de bord", "Synthèse des données enregistrées sur cet appareil")
        metric("Coûts estimés enregistrés", money(db.totalCost()))
        metric("Nombre total de saisies", db.allRecords().size.toString())
        categories.forEach { metric(it, db.countCategory(it).toString() + " enregistrement(s)") }
        button("＋ Nouvelle saisie") { showForm() }
        button("Consulter l’historique") { showHistory() }
    }

    private fun addField(label: String, hint: String = "", default: String = ""): EditText {
        content.addView(TextView(this).apply { text = label; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF43574D.toInt()); setPadding(2, 9, 2, 3) })
        val e = EditText(this).apply { this.hint = hint; setText(default); textSize = 14f; setSingleLine(true); setPadding(12, 8, 12, 8); setBackgroundColor(-1) }
        content.addView(e, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 2, 0, 3) })
        return e
    }

    private fun showForm() {
        title("Nouvelle saisie", "Les données sont enregistrées localement dans SQLite")
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        val lab = TextView(this).apply { text = "Catégorie"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setPadding(2, 6, 2, 4) }
        content.addView(lab); content.addView(spinner)
        val date = addField("Date (AAAA-MM-JJ)", default = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
        val chantier = addField("Chantier", "Code ou nom du chantier")
        val designation = addField("Désignation / élément *", "Ex. ouvrier, gravier, camion, logement")
        val fournisseur = addField("Fournisseur / société", "Facultatif")
        val quantite = addField("Quantité / jours / voyages", default = "0")
        val tarif = addField("Tarif unitaire (MAD)", default = "0")
        val montant = addField("Montant total (MAD)", "0 si calcul quantité × tarif", "0")
        val notes = addField("Observations", "Facultatif")
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) { selectedCategory = categories[position] }
        }
        button("Enregistrer dans SQLite") {
            if (designation.text.toString().trim().isEmpty()) { designation.error = "Champ obligatoire"; return@button }
            val values = ContentValues().apply {
                put("category", selectedCategory); put("date", date.text.toString().ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) })
                put("chantier", chantier.text.toString()); put("designation", designation.text.toString()); put("fournisseur", fournisseur.text.toString())
                put("quantite", quantite.text.toString().toDoubleOrNull() ?: 0.0); put("tarif", tarif.text.toString().toDoubleOrNull() ?: 0.0)
                put("montant", montant.text.toString().toDoubleOrNull() ?: 0.0); put("notes", notes.text.toString())
            }
            val result = db.addRecord(values)
            if (result > 0) { Toast.makeText(this, "Saisie enregistrée", Toast.LENGTH_SHORT).show(); showDashboard() }
            else Toast.makeText(this, "Erreur d’enregistrement", Toast.LENGTH_LONG).show()
        }
        button("Annuler") { showDashboard() }
    }

    private fun showHistory() {
        title("Historique des saisies", "Touchez une ligne pour consulter ou supprimer un enregistrement")
        val records = db.allRecords()
        if (records.isEmpty()) content.addView(TextView(this).apply { text = "Aucune donnée pour le moment. Créez votre première saisie."; textSize = 14f; setPadding(8, 24, 8, 24) })
        records.forEach { r ->
            val row = TextView(this).apply {
                text = "${r.date} · ${r.category}\n${r.designation} · ${r.chantier}\n${money(if (r.montant > 0) r.montant else r.quantite * r.tarif)}"
                textSize = 13f; setTextColor(0xFF172521.toInt()); setBackgroundColor(-1); setPadding(13, 13, 13, 13)
            }
            content.addView(row, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 4, 0, 4) })
            row.setOnClickListener { AlertDialog.Builder(this).setTitle(r.designation).setMessage("Catégorie : ${r.category}\nDate : ${r.date}\nChantier : ${r.chantier}\nFournisseur : ${r.fournisseur}\nQuantité : ${r.quantite}\nTarif : ${money(r.tarif)}\nMontant : ${money(r.montant)}\nNotes : ${r.notes}").setPositiveButton("Fermer", null).setNegativeButton("Supprimer") { _, _ -> db.deleteRecord(r.id); showHistory() }.show() }
        }
        button("Retour au tableau de bord") { showDashboard() }
    }
    private fun money(value: Double): String = NumberFormat.getNumberInstance(Locale.FRANCE).format(value) + " MAD"
}
