package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.DialogKeywordBinding
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.fragments.KeywordFragment
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.data.KeywordObject.Companion.ALL_CALENDAR

import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.util.VibrationUtil
import de.felixnuesse.timedsilence.util.WindowUtils

/**
 * Copyright (C) 2021  Felix Nüsse
 * Created on  15.07.2021
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
class KeywordDialog(context: Context) : Dialog(context, R.style.AlertDialogCustom) {

    private var tfrag: KeywordFragment? = null
    private var keyword: KeywordObject? = null

    private lateinit var binding: DialogKeywordBinding

    constructor(context: Context, tfragment: KeywordFragment) : this(context) {
        tfrag=tfragment
    }

    fun setKeyword(keyword: KeywordObject) {
        this.keyword = keyword
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.let { WindowUtils.applyDialogPaddingFixForDarkmode(context, it) }

        binding = DialogKeywordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(!VibrationUtil.canVibrate(context)) {
            binding.keywordDialogRbVibrate.visibility = View.GONE
        }

        if(keyword != null) {
            binding.keywordTextfield.setText(keyword!!.keyword)
            when(keyword!!.volume) {
                TIME_SETTING_LOUD -> binding.keywordDialogRbLoud.isChecked = true
                TIME_SETTING_SILENT -> binding.keywordDialogRbSilent.isChecked = true
                else ->  binding.keywordDialogRbVibrate.isChecked = true
            }
        }

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)

        binding.keywordDialogTitle.text = context.getText(R.string.keyword_add_new)

        binding.keywordCancel.setOnClickListener {
            Log.e(TAG(), "KeywordDialog: cancel!")
            this.cancel()
        }

        binding.keywordSave.setOnClickListener {
            Log.e(TAG(), "KeywordDialog: save!")

            val volId = getValueForVolumeRadioGroup()
            val keyword = KeywordObject(
                keyword?.id ?: 0,
                ALL_CALENDAR,
                binding.keywordTextfield.text.toString(),
                volId
            )
            tfrag?.saveKeyword(context, keyword)
            this.cancel()
        }
    }

    private fun getValueForVolumeRadioGroup(): Int{
        return when (binding.keywordDialogRbVolume.checkedRadioButtonId) {
            R.id.keyword_dialog_rb_loud -> TIME_SETTING_LOUD
            R.id.keyword_dialog_rb_silent -> TIME_SETTING_SILENT
            else ->  TIME_SETTING_VIBRATE
        }
    }
}