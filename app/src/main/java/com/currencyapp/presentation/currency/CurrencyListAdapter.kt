package com.currencyapp.presentation.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.currencyapp.R
import com.currencyapp.domain.model.Currency
import com.currencyapp.presentation.util.afterTextChanged
import com.currencyapp.presentation.util.formatCurrency
import kotlinx.android.synthetic.main.layout_currency_list_item.view.*

class CurrencyListAdapter(
    private val clickListener: (Currency) -> (Unit),
    private val textListener: (String) -> (Unit),
    private val items: MutableList<Currency> = mutableListOf()
) : RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_currency_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bindView(items[position])

    fun updateItems(newCurrencies: List<Currency>) {
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(CurrencyDiffCallback(items, newCurrencies))
        items.clear()
        items.addAll(newCurrencies)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val isBaseCurrency: Boolean get() = adapterPosition == 0
        fun bindView(currency: Currency) {
            itemView.tvCurrency.text = currency.code
            itemView.tvCurrencyCountry.text = currency.country
            itemView.ivThumbnail.loadImageFromRate(currency.iconUrl)
            itemView.etRate.setText(formatCurrency(currency.rate))
            itemView.setOnClickListener {
                clickListener.invoke(currency)
            }
            itemView.etRate.afterTextChanged {
                if (isBaseCurrency) {
                    textListener.invoke(it)
                }
            }
        }

        private fun ImageView.loadImageFromRate(url: String) {
            Glide.with(this).load(url).into(this)
        }
    }

    inner class CurrencyDiffCallback(
        private val oldCurrencies: List<Currency>,
        private val newCurrencies: List<Currency>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldCurrencies.size
        override fun getNewListSize() = newCurrencies.size
        override fun areItemsTheSame(oldPos: Int, newPos: Int) =
            oldCurrencies[oldPos].code == newCurrencies[newPos].code

        override fun areContentsTheSame(oldPos: Int, newPos: Int) =
            oldCurrencies[oldPos].rate == newCurrencies[newPos].rate
    }
}
