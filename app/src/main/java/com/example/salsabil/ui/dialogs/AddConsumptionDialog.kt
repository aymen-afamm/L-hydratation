package com.example.salsabil.ui.dialogs


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.salsabil.R
import com.example.salsabil.utils.Constants

class AddConsumptionDialog(
    context: Context,
    private val onConsumptionAdded: (Int) -> Unit
) : Dialog(context) {

    private lateinit var chipGroup: ChipGroup
    private lateinit var etCustomAmount: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    private var selectedAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_consumption)

        chipGroup = findViewById(R.id.chipGroupAmounts)
        etCustomAmount = findViewById(R.id.etCustomAmount)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)

        setupChips()
        setupListeners()
    }

    private fun setupChips() {
        val amounts = mapOf(
            R.id.chipSmallGlass to Constants.SMALL_GLASS_ML,
            R.id.chipMediumGlass to Constants.MEDIUM_GLASS_ML,
            R.id.chipLargeGlass to Constants.LARGE_GLASS_ML,
            R.id.chipBottle to Constants.BOTTLE_ML
        )

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedAmount = amounts[checkedId] ?: 0
            if (selectedAmount > 0) {
                etCustomAmount.setText("")
            }
        }
    }

    private fun setupListeners() {
        btnAdd.setOnClickListener {
            val amount = if (etCustomAmount.text.isNotEmpty()) {
                etCustomAmount.text.toString().toIntOrNull() ?: 0
            } else {
                selectedAmount
            }

            if (amount > 0) {
                onConsumptionAdded(amount)
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}