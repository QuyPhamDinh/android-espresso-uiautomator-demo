package com.wdiodemoapp.ui.forms

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wdiodemoapp.R
import com.wdiodemoapp.databinding.FragmentFormsBinding
import com.wdiodemoapp.ui.applyTopThirdOffset

class FormsFragment : Fragment() {

    private var _binding: FragmentFormsBinding? = null
    private val binding get() = _binding!!

    private val dropdownItems = arrayOf(
        "This app is awesome",
        "webdriver.io is awesome",
        "Appium is awesome",
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFormsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyTopThirdOffset(binding.formsScreen, binding.formsContent)

        // Ensure process death / activity recreation resets temporary form draft state.
        if (savedInstanceState != null) {
            binding.textInput.setText("")
            binding.formSwitch.isChecked = false
            binding.dropdownValue.setText("")
        }

        binding.textInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding.inputTextResult.text = s?.toString().orEmpty()
            }
        })

        updateSwitchLabel(binding.formSwitch.isChecked)
        binding.formSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchLabel(isChecked)
        }

        val openPicker = View.OnClickListener { showDropdownDialog() }
        binding.dropdownContainer.setOnClickListener(openPicker)
        binding.dropdownValue.setOnClickListener(openPicker)
        binding.dropdownChevron.setOnClickListener(openPicker)

        binding.buttonActive.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.forms_alert_title)
                .setMessage(R.string.forms_alert_message)
                .setNeutralButton(R.string.forms_alert_ask_later, null)
                .setNegativeButton(R.string.forms_alert_cancel, null)
                .setPositiveButton(R.string.alert_ok, null)
                .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isAllCaps = true
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.isAllCaps = true
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.isAllCaps = true
            }
            dialog.show()
        }
    }

    private fun updateSwitchLabel(isOn: Boolean) {
        binding.switchText.text = getString(
            if (isOn) R.string.forms_switch_on else R.string.forms_switch_off,
        )
    }

    private fun showDropdownDialog() {
        AlertDialog.Builder(requireContext())
            .setItems(dropdownItems) { dialog, index ->
                binding.dropdownValue.setText(dropdownItems[index])
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
