package com.nexxo.nsdlsdk.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.databinding.CardPermanentBlockBinding
import com.nexxo.nsdlsdk.interfaces.iBlockUnblock
import com.nexxo.nsdlsdk.model.ControlItemsDTO
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.viewmodel.DashboardViewModel


class CardPermanentBlock(context: Context) : BottomSheetDialogFragment() {
    private lateinit var binding: CardPermanentBlockBinding
    var cardResonse: ControlItemsDTO = ControlItemsDTO()
    private lateinit var viewModel: DashboardViewModel
    lateinit var isUpdate: iBlockUnblock

    //API Call
    private val retrofitService = RetrofitService.getInstance()
    private val mainRepository = MainRepository(retrofitService)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CardPermanentBlockBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(
            DashboardViewModel::class.java
        )

        inItViews()
        setStyle(STYLE_NO_FRAME, 0)

        viewModel.cardBlockResponse.observe(activity as FragmentActivity) {
            Utility.logData("Card unblock res code ${it.responsecode}")
            binding.progressDialog.visibility = View.GONE
            Constants.iscardBlocked = false
            activity?.let {
                dialog?.dismiss()
            }
            // (activity as ActivityDashboardCard).changeTogel(false)
            isUpdate.changeTogelButton(false)
        }

        return binding.root
    }
    private fun inItViews() {
        binding.cancelBtn.setOnClickListener(View.OnClickListener {
            activity?.let {
                dialog?.dismiss()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:0224202 2100")
                startActivity(intent)


            }
        })

        binding.email.setOnClickListener {
            activity?.let {
                dialog?.dismiss()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "care@nsdlbank.co.in"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Card Block")
                startActivity(intent)
            }
        }
        binding.crossIv.setOnClickListener(View.OnClickListener {
            activity?.let {
                dialog?.dismiss()
            }
        })

    }
}