package com.currencyapp.presentation.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.currencyapp.R
import com.currencyapp.domain.model.Currency
import com.currencyapp.presentation.util.afterTextChanged
import com.currencyapp.presentation.util.formatToCurrency
import kotlinx.android.synthetic.main.layout_currency_list_item.view.*

class CurrencyListAdapter(
    private val clickListener: (Currency) -> (Unit),
    private val textListener: (String) -> (Unit)
) : ListAdapter<Currency, CurrencyListAdapter.ViewHolder>(
    CurrencyDiffCallback
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_currency_list_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val isBaseCurrency: Boolean get() = adapterPosition == 0
        fun bindView(currency: Currency) {
            itemView.tvCurrency.text = currency.code
            itemView.tvCurrencyCountry.text = currency.country
            itemView.ivThumbnail.loadImageFromRate(currency.iconUrl)
            itemView.etRate.setText(currency.rate.toString().formatToCurrency())
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

    private object CurrencyDiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Currency, newItem: Currency): Any? {
            if (oldItem == newItem) {
                return null
            }
            return Unit
        }
    }
}
