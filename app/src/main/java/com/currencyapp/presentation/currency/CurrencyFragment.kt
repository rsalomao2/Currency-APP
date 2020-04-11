package com.currencyapp.presentation.currency

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.currencyapp.R
import com.currencyapp.data.di.injectCurrencyModules
import com.currencyapp.data.extension.observeEventNotHandled
import com.currencyapp.data.extension.observeNotNull
import kotlinx.android.synthetic.main.fragment_currency.*
import kotlinx.android.synthetic.main.fragment_currency.view.rviList
import org.koin.android.viewmodel.ext.android.viewModel

class CurrencyFragment : Fragment() {

    private val viewModel by viewModel<CurrencyViewModel>()

    private val mAdapter by lazy {
        CurrencyListAdapter(
            clickListener = viewModel.onItemClickListener,
            textListener = viewModel.onTextListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        injectCurrencyModules()
        val view = inflater.inflate(R.layout.fragment_currency, container, false)

        setRecycleView(view)
        setObservers(view)
        return view
    }

    private fun setObservers(view: View) {
        viewModel.currencyList.observeNotNull(viewLifecycleOwner) { currencyList ->
            mAdapter.updateItems(currencyList)
            rviList.scrollToPosition(0)
        }
        viewModel.errorMessage.observeEventNotHandled(viewLifecycleOwner) {
            showAlertDialog(it, getString(R.string.e_generic_button_label_try_again)) {
                resumeSearch()
            }
        }
        viewModel.errorNetwork.observeNotNull(viewLifecycleOwner) {
            showAlertDialog(
                getString(R.string.e_generic_network_text),
                getString(R.string.e_generic_button_label_try_again)
            ) {
                resumeSearch()
            }
        }

        viewModel.hideKeyBoard.observeEventNotHandled(viewLifecycleOwner) {
            hideKeyboard(view)
        }
    }

    private fun resumeSearch() {
        //TODO: Testar falha em conexao pra ver se nao abre varios dialogs
        viewModel.loadLatestCurrency()
    }

    private fun setRecycleView(view: View) {
        with(view.rviList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setOnTouchListener { v, _ ->
                hideKeyboard(v)
                false
            }
            setHasFixedSize(true)
        }
    }

    private fun hideKeyboard(v: View) {
        val imm: InputMethodManager? =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private var dialog: AlertDialog? = null
    private fun showAlertDialog(
        message: String,
        buttonLabel: String?,
        listener: () -> (Unit)?
    ) {
        dialog = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.e_generic_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                buttonLabel
            ) { _, _ -> listener.invoke() }.show()
        dialog?.dismiss()
        dialog?.show()
    }
}
