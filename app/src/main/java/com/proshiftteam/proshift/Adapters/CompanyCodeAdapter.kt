/*
Copyright 2020 ProShift Team

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

// Adapter to display a list of company codes generated
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

    // Creates a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_code_item, parent, false)
            as CardView

        return ViewHolder(cardView)
    }

    // Gets total number of items
    override fun getItemCount(): Int {
        return companyCodeList.size
    }

    // Binds the data to items in the view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val companyCode = companyCodeList.get(position)

        holder.tvEmail.text = companyCode.email
        holder.tvPosition.text = companyCode.position_name
        holder.tvCode.text = companyCode.code.toString()

        holder.itemView.setOnClickListener { onItemClick(holder, holder.itemView.context) }
    }

    // Performs action when item is clicked
    fun onItemClick(holder: ViewHolder, context: Context) {
        context.copyToClipboard(holder.tvCode.text.toString())
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }

    // Copies the code
    fun Context.copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("clipboard", text)
        clipboard.setPrimaryClip(clip)
    }
}