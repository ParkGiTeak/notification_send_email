package com.example.notificationsendemail.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notificationsendemail.R
import com.example.notificationsendemail.adapter.InstalledAppListAdapter
import com.example.notificationsendemail.databinding.FragmentApplicationListBottomSheetBinding
import com.example.notificationsendemail.model.AppInfoData
import com.example.notificationsendemail.util.ExcludeLastItemDividerDecoration
import com.example.notificationsendemail.util.PackageUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ApplicationListBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentApplicationListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var mInstalledAppListAdapter: InstalledAppListAdapter? = null

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                (bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isFitToContents = true
            behavior.isHideable = true
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicationListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PackageUtil.getPackageList(view.context)?.let { packageList ->
            packageList.forEach {
                Log.d("PGT", "packageName:: ${it.appPackageName}")
            }
            setApplicationRecyclerView(packageList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setApplicationRecyclerView(packageList: MutableList<AppInfoData>) {
        mInstalledAppListAdapter = InstalledAppListAdapter()
        binding.rvInstalledApplicationList.apply {
            this.adapter = mInstalledAppListAdapter
            this.layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            ContextCompat.getDrawable(
                this.context,
                R.drawable.divider_application_list_bottom_sheet
            )?.let {
                this.addItemDecoration(ExcludeLastItemDividerDecoration(it))
            }
        }
        mInstalledAppListAdapter?.setItemList(packageList)
    }
}