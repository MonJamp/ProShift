package com.proshiftteam.proshift.Adapters

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.CompanyCodeObject
import com.proshiftteam.proshift.R


class CompanyCodeAdapter(
    val tokenCode: String,
    val accessCode: Int,
    private val companyCodeList: List<CompanyCodeObject>
)
    : RecyclerView.Adapter<CompanyCodeAdapter.ViewHolder>()
{
    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val tvEmail: TextView = cardView.findViewById(R.id.cardCodeEmail)
        val tvPosition: TextView = cardView.findViewById(R.id.cardCodePosition)
        val tvCode: TextView = cardView.findViewById(R.id.cardCodeCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_code_item, parent, false)
            as CardView

        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return companyCodeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val companyCode = companyCodeList.get(position)

        holder.tvEmail.text = companyCode.email
        holder.tvPosition.text = companyCode.position_name
        holder.tvCode.text = companyCode.code.toString()

        holder.itemView.setOnClickListener { onItemClick(holder, holder.itemView.context) }
    }

    fun onItemClick(holder: ViewHolder, context: Context) {
        context.copyToClipboard(holder.tvCode.text.toString())
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }

    fun Context.copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("clipboard", text)
        clipboard.setPrimaryClip(clip)
    }
}