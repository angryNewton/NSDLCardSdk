package com.nexxo.nsdlsdk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nexxo.nsdlsdk.R
import com.nexxo.nsdlsdk.databinding.CardBlockListItemsBinding
import com.nexxo.nsdlsdk.model.CardBlockReasons

class CardBlockAdatper(private val cellClickListener: CallBlockListner) :
    RecyclerView.Adapter<CardBlockAdatper.ControlViewHolder>() {
    private var cardItems = mutableListOf<CardBlockReasons>()
    private var context: Context? = null
    private var selectedPosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(context: Context, transactionList: List<CardBlockReasons>) {
        this.context = context
        this.cardItems = transactionList.toMutableList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControlViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardBlockListItemsBinding.inflate(inflater, parent, false)
        return ControlViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ControlViewHolder, position: Int) {
        val items = cardItems[position]
        holder.binding.tvCardBlockName.text = items.reasonMessage
        holder.binding.llCheck.setOnClickListener(View.OnClickListener {
            selectedPosition = holder.adapterPosition
            cellClickListener.onClick(selectedPosition, items)
            notifyDataSetChanged()
        })
        if (selectedPosition == position) {
            holder.binding.rBtnCheck.isChecked = true
            holder.binding.rBtnCheck.setBackgroundResource(R.drawable.ic_corporate_card_checked_box)
        } else {
            holder.binding.rBtnCheck.isChecked = false
            holder.binding.rBtnCheck.setBackgroundResource(R.drawable.ic_unchecked_box)
        }

//        if (items.isChecked!!) {
//            holder.binding.rBtnCheck.setImageResource(R.drawable.ic_checkcheckbox)
//        } else {
//            holder.binding.rBtnCheck.setImageResource(R.drawable.ic_uncheck_checkbox)
//        }
        /* holder.binding.llCheck.setOnClickListener(View.OnClickListener {
             items.reasonId?.let { it1 -> setProduct(it1) }
             cellClickListener.onClick(cardItems[position], items)
         })*/


    }

    override fun getItemCount(): Int {
        return cardItems.size
    }

//    private fun setProduct(productId: Int) {
//        for (i in 0 until cardItems.size) {
//            var cardItemsLocal : CardBlockReasons = CardBlockReasons()
//            if (cardItems[i].reasonId == productId) {
//                if (cardItems.get(i).isChecked!!) {
//                    cardItems.get(i).isChecked == false
//                    cardItemsLocal = cardItems.get(i)
//                    cardItems.set(i,cardItemsLocal)
//                    //    binding.rBtnCheck.setImageResource((R.drawable.ic_checkcheckbox))
//
//                } else {
//                    cardItems.get(i).isChecked == true
//                    cardItemsLocal = cardItems.get(i)
//                    cardItems.set(i,cardItemsLocal)
//
//                    //   holder.binding.rBtnCheck.setImageResource((R.drawable.ic_checkcheckbox))
//                }
//            } else {
//                cardItems.get(i).isChecked == false
//                cardItemsLocal = cardItems.get(i)
//                cardItems.set(i,cardItemsLocal)
//
//                //  holder.binding.rBtnCheck.setImageResource((R.drawable.ic_checkcheckbox))
//            }
//
//        }
//
//        notifyDataSetChanged()
//    }

    private fun setProduct(productId: Int) {
        for (i in 0 until cardItems.size) {
            if (cardItems[i].reasonId == productId) {
                val cardItems: CardBlockReasons = cardItems[i]
                CardBlockReasons(
                    cardItems.reasonId,
                    cardItems.reasonMessage,
                    true
                ).also { it }
            } else {
                val cardItems: CardBlockReasons = cardItems[i]
                CardBlockReasons(
                    cardItems.reasonId,
                    cardItems.reasonMessage,
                    false
                ).also { it }
            }

        }

        notifyDataSetChanged()
    }

    class ControlViewHolder(val binding: CardBlockListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface CallBlockListner {
        fun onClick(position: Int, reason: CardBlockReasons)
    }
}