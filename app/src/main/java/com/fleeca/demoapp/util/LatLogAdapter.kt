package com.fleeca.demoapp.util

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fleeca.demoapp.R

class LatLogAdapter (private val context: Context, private val faqvo: ArrayList<MarkerDataDto>): RecyclerView.Adapter<LatLogAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatLogAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latlog_item_layout, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return faqvo.size

    }

    override fun onBindViewHolder(holder: LatLogAdapter.ViewHolder, position: Int) {
        var faqVo = faqvo[position]
        holder.txtSno.text= (position+1).toString()
        holder.txtLog.text= faqVo.lon.toString()
        holder.txtLat.text=faqVo.lat.toString()


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSno: TextView
        var txtLat: TextView
        var txtLog: TextView


        var view: View? = null

        init {
            this.view = view
            txtSno = view.findViewById(R.id.txt_s_no)
            txtLat = view.findViewById(R.id.txt_lat)
            txtLog = view.findViewById(R.id.txt_log)

        }

    }
}



