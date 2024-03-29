package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.DialogGraphoverviewBinding
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.ui.GraphOverviewAdapter
import de.felixnuesse.timedsilence.util.WindowUtils

/**
 * Copyright (C) 2023  Felix Nüsse
 * Created on  11.12.2023
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */
class GraphOverviewDialog(context: Context, private var mStates: ArrayList<VolumeState>) : Dialog(context, R.style.AlertDialogCustom) {

    private lateinit var binding: DialogGraphoverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.let { WindowUtils.applyDialogPaddingFixForDarkmode(context, it) }

        binding = DialogGraphoverviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)
        setList()
        binding.ok.setOnClickListener{
            dismiss()
        }
    }

    private fun setList() {
        Log.e(TAG(), "States: ${mStates.size}")

        var viewManager = LinearLayoutManager(binding.root.context)
        var viewAdapter = GraphOverviewAdapter(mStates)

        binding.states.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}