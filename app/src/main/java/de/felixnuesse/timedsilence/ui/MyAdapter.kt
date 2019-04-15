package de.felixnuesse.timedsilence.ui;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import kotlinx.android.synthetic.main.adapter_schedules_list.view.*
import java.text.DateFormat
import java.util.*

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 14.04.19 - 17:18
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
class MyAdapter(private val myDataset: List<ScheduleObject>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder.
// Each data item is just a string in this case that is shown in a TextView.
class MyViewHolder(val scheduleView: View) : RecyclerView.ViewHolder(scheduleView)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_schedules_list, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
                val df = DateFormat.getTimeInstance()
                df.timeZone= TimeZone.getTimeZone("UTC")

                holder.scheduleView.textView_schedule_row_title.text = (myDataset.get(position) as ScheduleObject).name
                holder.scheduleView.textView_schedule_row_time_start.text = df.format(myDataset.get(position).time_start)
                holder.scheduleView.textView_schedule_row_time_end.text =  df.format(myDataset.get(position).time_end)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
        }
