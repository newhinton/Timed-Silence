package de.felixnuesse.timedsilence.ui;

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.Constants.Companion.WIFI_TYPE_CONNECTED
import de.felixnuesse.timedsilence.Constants.Companion.WIFI_TYPE_SEARCHING
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterContactListBinding
import de.felixnuesse.timedsilence.databinding.AdapterWifiListBinding
import de.felixnuesse.timedsilence.model.contacts.Contact
import de.felixnuesse.timedsilence.model.data.WifiObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import kotlin.collections.ArrayList


/**
 * Copyright (C) 2024  Felix Nüsse
 * Created on 06.01.24 - 21:53
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
class ContactsListAdapter(private val myDataset: ArrayList<Contact>): RecyclerView.Adapter<ContactsListAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = AdapterContactListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val context = holder.contactView.root.context
        var contact = myDataset[position]
        holder.contactView.name.text = contact.name
        holder.contactView.profile.setImageDrawable(contact.photo)

    }

    override fun getItemCount() = myDataset.size

    class ContactViewHolder(val contactView: AdapterContactListBinding) :
        RecyclerView.ViewHolder(contactView.root)

}


