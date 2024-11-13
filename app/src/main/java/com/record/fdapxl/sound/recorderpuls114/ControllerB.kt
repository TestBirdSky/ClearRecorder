package com.record.fdapxl.sound.recorderpuls114

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.record.fdapxl.sound.recorderpuls114.bean.Info
import com.record.fdapxl.sound.recorderpuls114.databinding.ActivityHomeBinding
import com.record.fdapxl.sound.recorderpuls114.databinding.ItemListBinding
import com.record.fdapxl.sound.recorderpuls114.dialog.InpDialog
import com.record.fdapxl.sound.recorderpuls114.dialog.ListDialog
import com.record.fdapxl.sound.recorderpuls114.dialog.SpDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ControllerB(val mActivity: AppCompatActivity, val binding: ActivityHomeBinding) {


    fun initRv(){
        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(mActivity)
        binding.rv.adapter = adapter
    }

    fun initData(){
        val list:List<Info>? = App.app.getRecordList()
        itemList.clear()
        if (list.isNullOrEmpty()){

        }else{
            itemList.addAll(list)
            itemList.sortByDescending { it.time }
        }
        adapter.notifyDataSetChanged()
    }
    val itemList = mutableListOf<Info>()
    inner class VH public constructor(val bin:ItemListBinding) : RecyclerView.ViewHolder(bin.root)
    val adapter = object :RecyclerView.Adapter<VH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemListBinding.inflate(mActivity.layoutInflater,parent,false))
        }
        override fun getItemCount(): Int {
            return itemList.size
        }
        override fun onBindViewHolder(vh: VH, position: Int) {
            val data = itemList.get(position)
            vh.bin.name.text = data.fileName
            vh.bin.time.text = SimpleDateFormat("HH:mm MM-dd-yyyy", Locale.getDefault()).format(Date(data.time))

            vh.itemView.setOnClickListener {
                PlayerActivity.start(mActivity,data)
            }
            vh.itemView.setOnLongClickListener {
                ListDialog.show(mActivity,{
                    App.app.shareFile(mActivity,data)
                },{
                    SpDialog.show(mActivity,"Deletion is irreversible. Are you sure you want to proceed with the deletion?") {
                        itemList.removeAt(position)
                        notifyDataSetChanged()
                        App.app.rmRecord(data)
                    }
                },{
                    var sss2 =  data.fileName.substringBefore(".");
                    InpDialog.show(mActivity,"Rename","",{ text ->
//                    InpDialog.show(mActivity,"Rename",sss2,{ text ->
                        val oleFile: File = data.file
                        val newFile: File = File(data.file.parent, "$text.aac")
                        oleFile.renameTo(newFile)//执行重命名

                        App.app.updateRecord(data,"${System.currentTimeMillis()}###${newFile}")

                        initData()
                    },{txt-> })
                })
                return@setOnLongClickListener true
            }
        }
    }

    init {

        initRv();

        initData();
    }
}