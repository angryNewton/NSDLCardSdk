package com.nexxo.nsdlsdk.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nexxo.nsdlsdk.R
import com.nexxo.nsdlsdk.databinding.CardLimitItemsBinding
import com.nexxo.nsdlsdk.model.CorporatecardLimitResponse
import com.nexxo.nsdlsdk.model.CorporatecardProductSettingDomesticTxnToAllow
import com.nexxo.nsdlsdk.model.Corporatecardlistresponse
import com.nexxo.nsdlsdk.utility.Utility


class LimitCardAdapter : RecyclerView.Adapter<LimitCardAdapter.LimitTransactionViewHolder>() {
    private var transactionItems = mutableListOf<Corporatecardlistresponse>()
    private var crd: CorporatecardLimitResponse? = CorporatecardLimitResponse()
    private var context: Context? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(
        context: Context,
        transactionList: List<Corporatecardlistresponse>,
        responseobj: CorporatecardLimitResponse
    ) {
        this.context = context
        this.transactionItems = transactionList.toMutableList()
        this.crd = responseobj
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitTransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLimitItemsBinding.inflate(inflater, parent, false)
        return LimitTransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LimitTransactionViewHolder, position: Int) {
        var currentLimt: Double? = 0.0
        val maxLimt: Double = Utility.ParseDouble(crd?.maxLimit)
        //    val maxLimt: Double = 1100.00
        val minLimit: Double = Utility.ParseDouble(crd?.minLimit)
        var STEP = "0"
        if (TextUtils.isEmpty(crd?.sliderSteps)) {
            STEP = "0"
        } else {

            STEP = crd?.sliderSteps.toString()
        }
        val items = transactionItems[position]
        /* holder.binding.tvCurrentLimitPrice.text = crd?.dailyCnt.toString()
         holder.binding.tvCurrentMaxLimitPrice.text = crd?.dailyLim.toString()*/
        val dailyCnt: Double = Utility.ParseDouble(crd?.productSettings?.domestic?.dailyCnt)
        holder.binding.tvCurrentLimitPrice.text =
            context?.getString(R.string.rs) + " " + dailyCnt.toString()

        val dailyLimitAmount: Double = Utility.ParseDouble(crd?.productSettings?.domestic?.dailyLim)
        Utility.logData("Big Decimal :${dailyLimitAmount}")

        holder.binding.tvCurrentMaxLimitPrice.text =
            context?.getString(R.string.rs) + " " + maxLimt.toString()
        //  var maxLimt: Double = crd?.maxLimit!!.toDouble()
        // var minLimit:Double=crd?.minLimit!!.toDouble()

        if (items.id == 1) {
            currentLimt = Utility.ParseDouble(crd?.atmLim!!)
            Utility.logData("Big Decimal" + currentLimt.toString())
            holder.binding.tvCurrentLimitPrice.text =
                context?.getString(R.string.rs) + " " + currentLimt.toString()
            holder.binding.tvTitleNew.text = items.name
            val atmLimit: Double = Utility.ParseDouble(crd?.atmLim!!)
            holder.binding.tvAmount.text =
                context?.getString(R.string.rs) + " " + atmLimit.toString()
            holder.binding.tvMaxLimitSet.text = maxLimt.toString()
            /*holder.binding.limitSeekbar.progress = (Math.round(atmLimit).toInt() / 100)
            holder.binding.limitSeekbar.incrementProgressBy(STEP!!.toInt() / 100)
            holder.binding.limitSeekbar.max =
                (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt())*/
            if (STEP != null) {
                setSeekbarFirstTime(holder.binding, currentLimt, maxLimt, STEP)
            }

            if (crd?.productSettings?.domestic?.txnToAllow?.atm.equals("Y", true)) {
                limitCardShow(holder.binding.rlCardMain)
//                showVisibleLayout(holder.binding)
            } else {
                limitCardHide(holder.binding.rlCardMain)
//                hideVisibleLayout(holder.binding)
            }
            if (crd?.atm.equals("Y", true)) {
                showToogleVisible(maxLimt, holder.binding, items, minLimit, maxLimt, STEP)
            } else {
                hideToogleVisible(maxLimt, holder.binding, items, STEP)
            }

            Log.d("cred", crd!!.contactLessLim.toString())
        } else if (items.id == 2) {
            currentLimt = crd?.eCommLim!!.toDouble()
            holder.binding.tvCurrentLimitPrice.text =
                context?.getString(R.string.rs) + " " + currentLimt.toString()
            holder.binding.tvTitleNew.text = items.name
            val eCommLimit: Double = Utility.ParseDouble(crd?.eCommLim!!)
            holder.binding.tvAmount.text =
                context?.getString(R.string.rs) + " " + eCommLimit.toString()
            holder.binding.tvMaxLimitSet.text = maxLimt.toString()
            /*holder.binding.limitSeekbar.progress = (Math.round(eCommLimit).toInt() / 100)
            holder.binding.limitSeekbar.incrementProgressBy(STEP!!.toInt() / 100)
            holder.binding.limitSeekbar.max =
                (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt())*/
            if (STEP != null) {
                setSeekbarFirstTime(holder.binding, currentLimt, maxLimt, STEP)
            }
            if (crd?.productSettings?.domestic?.txnToAllow?.eComm.equals("Y", true)) {
                limitCardShow(holder.binding.rlCardMain)
//                showVisibleLayout(holder.binding)
            } else {
                limitCardHide(holder.binding.rlCardMain)
//                hideVisibleLayout(holder.binding)
            }
            if (crd?.eComm.equals("Y", true)) {
                showToogleVisible(maxLimt, holder.binding, items, minLimit, maxLimt, STEP)
            } else {
                hideToogleVisible(maxLimt, holder.binding, items, STEP)
            }
        } else if (items.id == 3) {
            currentLimt = crd?.posLim!!.toDouble()
            holder.binding.tvCurrentLimitPrice.text =
                context?.getString(R.string.rs) + " " + currentLimt.toString()
            holder.binding.tvTitleNew.text = items.name
            val posLimit = crd?.posLim!!.toDouble()
            holder.binding.tvAmount.text =
                context?.getString(R.string.rs) + " " + posLimit.toString()
            holder.binding.tvMaxLimitSet.text = maxLimt.toString()
            /*holder.binding.limitSeekbar.progress = (Math.round(posLimit).toInt() / 100)
            holder.binding.limitSeekbar.incrementProgressBy(STEP!!.toInt() / 100)
            holder.binding.limitSeekbar.max =
                (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt())*/
            if (STEP != null) {
                setSeekbarFirstTime(holder.binding, currentLimt, maxLimt, STEP)
            }


            if (crd?.productSettings?.domestic?.txnToAllow?.pos.equals("Y", true)) {
                limitCardShow(holder.binding.rlCardMain)
//                showVisibleLayout(holder.binding)
            } else {
                limitCardHide(holder.binding.rlCardMain)
//                hideVisibleLayout(holder.binding)
            }
            if (crd?.pos.equals("Y", true)) {
                showToogleVisible(maxLimt, holder.binding, items, minLimit, maxLimt, STEP)
            } else {
                hideToogleVisible(maxLimt, holder.binding, items, STEP)
            }

        } else if (items.id == 4) {
            currentLimt = crd?.contactLessLim!!.toDouble()
            holder.binding.tvCurrentLimitPrice.text =
                context?.getString(R.string.rs) + " " + currentLimt.toString()
            holder.binding.tvTitleNew.text = items.name
            val cashAtPos: Double = Utility.ParseDouble(crd?.contactLessLim!!)
            holder.binding.tvAmount.text =
                context?.getString(R.string.rs) + " " + cashAtPos.toString()
            holder.binding.tvMaxLimitSet.text = maxLimt.toString()
            /* holder.binding.limitSeekbar.progress = (Math.round(cashAtPos).toInt() / 100)
             holder.binding.limitSeekbar.incrementProgressBy(STEP!!.toInt() / 100)
             holder.binding.limitSeekbar.max =
                 (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt())
             holder.binding.limitSeekbar.progress = cashAtPos.toInt()*/
            if (STEP != null) {
                setSeekbarFirstTime(holder.binding, currentLimt, maxLimt, STEP)
            }
            if (crd?.productSettings?.domestic?.txnToAllow?.contactLess.equals("Y", true)) {
                limitCardShow(holder.binding.rlCardMain)
//                showVisibleLayout(holder.binding)
            } else {
                limitCardHide(holder.binding.rlCardMain)
//                hideVisibleLayout(holder.binding)
            }
            if (crd?.contactLess.equals("Y", true)) {
                showToogleVisible(maxLimt, holder.binding, items, minLimit, maxLimt, STEP)
            } else {
                hideToogleVisible(maxLimt, holder.binding, items, STEP)
            }

        } else if (items.id == 5) {
            currentLimt = crd?.cashAtPosLim!!.toDouble()
            holder.binding.tvCurrentLimitPrice.text =
                context?.getString(R.string.rs) + " " + currentLimt.toString()
            holder.binding.tvTitleNew.text = items.name
            val contactLessLimit = crd?.cashAtPosLim!!.toDouble()
            holder.binding.tvAmount.text =
                context?.getString(R.string.rs) + " " + contactLessLimit.toString()
            holder.binding.tvMaxLimitSet.text = maxLimt.toString()
            /*holder.binding.limitSeekbar.progress = (Math.round(contactLessLimit).toInt() / 100)
            holder.binding.limitSeekbar.incrementProgressBy(STEP!!.toInt() / 100)
            holder.binding.limitSeekbar.max =
                (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt())*/
            if (STEP != null) {
                setSeekbarFirstTime(holder.binding, currentLimt, maxLimt, STEP)
            }

            if (crd?.productSettings?.domestic?.txnToAllow?.cashAtPos.equals("Y", true)) {
                limitCardShow(holder.binding.rlCardMain)
//                showVisibleLayout(holder.binding)
            } else {
                limitCardHide(holder.binding.rlCardMain)
//                hideVisibleLayout(holder.binding)
            }

            if (crd?.cashAtPos.equals("Y", true)) {
                showToogleVisible(maxLimt, holder.binding, items, minLimit, maxLimt, STEP)
            } else {
                hideToogleVisible(maxLimt, holder.binding, items, STEP)
            }

        }
        holder.binding.switchChang.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                holder.binding.lvLimitseekbar.visibility = View.VISIBLE
                items.isValueChanged = true
                items.isEditable = true
                showToogleVisible(maxLimt, holder.binding, items, minLimit, maxLimt, STEP)

            } else {
                items.isEditable = true
                holder.binding.switchChang.isChecked = false
                items.isValueChanged = false
                holder.binding.lvLimitseekbar.visibility = View.GONE
            }
        }
    }

    private fun showToogleVisible(
        maxLimt1: Double,
        binding: CardLimitItemsBinding,
        items: Corporatecardlistresponse,
        minLimit: Double,
        maxLimt: Double,
        STEP: String?
    ) {
        binding.limitSeekbar.isClickable = true
        binding.switchChang.isChecked = true
        binding.lvLimitseekbar.visibility = View.VISIBLE
//        binding.switchChang.isClickable = true
        items.isValueChanged = true
        if (maxLimt > 0) {
            binding.limitSeekbar.isEnabled = true
            setSeekbar(binding, items, minLimit, maxLimt, true, STEP)
        } else {
//            binding.limitSeekbar.isEnabled = false
            binding.limitSeekbar.isEnabled = true
            setSeekbar(binding, items, minLimit, maxLimt, false, STEP)
//            Utility.showMessageInToast(context as Activity, "Max Limit value is Zero")
        }

    }

    private fun hideToogleVisible(
        maxLimt: Double,
        binding: CardLimitItemsBinding,
        items: Corporatecardlistresponse,
        STEP: String?
    ) {
        binding.limitSeekbar.isClickable = false
        binding.switchChang.isChecked = false
        binding.lvLimitseekbar.visibility = View.GONE
//        binding.switchChang.isClickable = false
        items.isValueChanged = false
        if (maxLimt > 0) {
            binding.limitSeekbar.isEnabled = true
            setSeekbar(binding, items, maxLimt, maxLimt, true, STEP)
        } else {
//            binding.limitSeekbar.isEnabled = false
            binding.limitSeekbar.isEnabled = true
            setSeekbar(binding, items, 0.00, 0.00, false, STEP)
//            Utility.showMessageInToast(context as Activity, "Max Limit value is Zero")
        }

    }

    fun limitCardShow(rl_card_main: RelativeLayout) {
        rl_card_main.visibility = View.VISIBLE
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 40, 20, 20)
        rl_card_main.setLayoutParams(params)
    }

    fun limitCardHide(rl_card_main: RelativeLayout) {
        rl_card_main.visibility = View.GONE
        rl_card_main.layoutParams = RecyclerView.LayoutParams(0, 0)
    }

    private fun showVisibleLayout(
        binding: CardLimitItemsBinding,
    ) {
        showLimiationCard(
            true,
            binding.cvLimitMain,
            crd?.productSettings?.domestic?.txnToAllow
        )
    }

    private fun hideVisibleLayout(binding: CardLimitItemsBinding) {
        binding.limitSeekbar.isClickable = false
        binding.switchChang.isChecked = false
        binding.lvLimitseekbar.visibility = View.GONE
        binding.switchChang.isClickable = false
    }

    private fun showLimiationCard(
        boolean: Boolean,
        view: LinearLayout,
        txnToAllow: CorporatecardProductSettingDomesticTxnToAllow?
    ) {
        if (boolean) {
            view.visibility = View.VISIBLE
            view.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
            )
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(10, 10, 10, 10)

        } else {
            view.visibility = View.GONE
            view.layoutParams.height = 0

        }
    }

    private fun setSeekbar(
        binding: CardLimitItemsBinding,
        items: Corporatecardlistresponse,
        minLimit: Double,
        maxLimt: Double,
        isLimitZero: Boolean,
        STEP: String?
    ) {


        var total: Double? = 0.00
        var isProgressChanged: Boolean? = false
        isProgressChanged = isLimitZero
        /*binding.tvMinLimitSet.text = "(Min) Rs " + this.crd?.dailyCnt
        binding.tvMaxLimitSet.text = "(Max) Rs " + this.crd?.dailyLim*/
        try {
            binding.tvMinLimitSet.text = "(Min) " + context?.getString(R.string.rs) + " " + minLimit
            binding.tvMaxLimitSet.text = "(Max) " + context?.getString(R.string.rs) + " " + maxLimt
            var limitMax: Int = 0
            try {
                limitMax = ((maxLimt.toInt()!! - minLimit.toInt()!!) / STEP!!.toInt())
            } catch (e: Exception) {
                limitMax = 0
            }
            if (limitMax > 0) {
                binding.limitSeekbar?.max = limitMax
            } else {
                binding.limitSeekbar?.max = limitMax
            }
            binding.limitSeekbar.setOnSeekBarChangeListener(
                object : OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        if (isProgressChanged == false) {
                            Utility.showMessageInToast(
                                context as Activity,
                                "Max limit value is zero"
                            )
                        }
                    }

                    override fun onProgressChanged(
                        seekBar: SeekBar, progress: Int,
                        fromUser: Boolean
                    ) {
                        if (isProgressChanged == false) {
//                            Utility.showMessageInToast(context as Activity, "Max Limit value is Zero")
                        } else {
                            items.isEditable = true
                            items.isCardLimtShow = true
                            total = (progress * 100).toDouble()
                            items.isSeekValueChanged = total.toString()
                            Utility.logData("Seekbar=====${items.isSeekValueChanged}")
                            binding.tvAmount.text =
                                context?.getString(R.string.rs) + " " + total.toString()
                            //  binding.tvCurrentLimitPrice.text = total.toString()
//                    binding.tvCurrentLimitPrice.text = currentLimt.toString()
                        }
                    }
                }
            )
        } catch (e: Exception) {
            e.let { it.message }
        }

    }

    private fun setSeekbarFirstTime(
        binding: CardLimitItemsBinding,
        currentLimt: Double,
        maxLimt: Double,
        STEP: String
    ) {
        try {
            binding.limitSeekbar.progress = (Math.round(currentLimt).toInt() / 100)
            Utility.logData((Math.round(currentLimt).toInt() / 100).toString())
//            binding.limitSeekbar.incrementProgressBy(STEP!!.toInt() / 100)
            Utility.logData((STEP!!.toInt() / 100).toString())
            binding.limitSeekbar.max =
                (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt())
            Utility.logData(
                (Math.round(maxLimt).toInt() * 100 / Math.round(maxLimt).toInt()).toString()
            )
        } catch (e: Exception) {
            e.message?.let { Utility.logData(it) }
            e.let { it.message }
        }

    }

    override fun getItemCount(): Int {
        return transactionItems.size
    }

    class LimitTransactionViewHolder(val binding: CardLimitItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}