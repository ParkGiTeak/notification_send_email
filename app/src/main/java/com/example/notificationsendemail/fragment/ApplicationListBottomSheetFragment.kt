package com.example.notificationsendemail.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notificationsendemail.R
import com.example.notificationsendemail.adapter.InstalledAppListAdapter
import com.example.notificationsendemail.databinding.FragmentApplicationListBottomSheetBinding
import com.example.notificationsendemail.datastore.notiPacakgeDataStore
import com.example.notificationsendemail.model.AppInfoData
import com.example.notificationsendemail.util.ExcludeLastItemDividerDecoration
import com.example.notificationsendemail.util.PackageUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private fun setApplicationRecyclerView(installedAppInfoDatas: MutableList<AppInfoData>) {
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
        mInstalledAppListAdapter?.apply {
            setAppItemClickLister(object : InstalledAppListAdapter.AppItemClickListener {
                override fun onClickView(position: Int) {
                    val isChecked = !installedAppInfoDatas[position].appCheckState
                    installedAppInfoDatas[position].appCheckState = isChecked

                    lifecycleScope.launch {
                        binding.root.context.notiPacakgeDataStore.updateData { currentPreference ->
                            val builder = currentPreference.toBuilder()

                            if (isChecked) {
                                builder.addPackage(installedAppInfoDatas[position].appPackageName)
                            } else {
                                /*
                                * ProtoBuf에서 repeat(List)는 단일 객체를 삭제하는 remove같은 메서드를 제공하지 않아서
                                * 전체리스트에서 지울거 지우고 clear해주고 다시 더해줘야한다.
                                */
                                val currentPackageList = currentPreference.packageList.toMutableList()
                                currentPackageList.remove(installedAppInfoDatas[position].appPackageName)
                                builder.clearPackage()
                                builder.addAllPackage(currentPackageList)
                            }
                            builder.build()
                        }
                    }
                    notifyItemChanged(position)
                }
            })
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    binding.root.context.notiPacakgeDataStore.data.collect { savePackages ->
                        installedAppInfoDatas.map { appInfoData ->
                            appInfoData.appCheckState =
                                savePackages.packageList.contains(appInfoData.appPackageName)
                        }
                    }
                }
            }
            setItemList(installedAppInfoDatas)
        }
    }
}