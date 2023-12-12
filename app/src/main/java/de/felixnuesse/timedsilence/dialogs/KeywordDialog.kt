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
import de.felixnuesse.timedsilence.fragments.KeywordFragment
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.data.KeywordObject.Companion.ALL_CALENDAR

import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE

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

    companion object {
        private const val TAG = "KeywordDialog"
    }

    private var tfrag: KeywordFragment? = null

    private lateinit var binding: DialogKeywordBinding

    constructor(context: Context, tfragment: KeywordFragment) : this(context) {
        tfrag=tfragment
    }

    private var state: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogKeywordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        binding.keywordBack.visibility = View.INVISIBLE
        binding.keywordDialogTitle.text = context.getText(R.string.keyword_dialog_title_title)
        binding.keywordKeywordLayout.visibility = View.VISIBLE

        binding.keywordNext.setOnClickListener {
            Log.e(TAG, "KeywordDialog: next!")

            hideAll()
            state++
            decideState()
        }

        binding.keywordBack.setOnClickListener {
            Log.e(TAG, "KeywordDialog: back!")

            hideAll()
            state--
            decideState()
        }

        binding.keywordCancel.setOnClickListener {
            Log.e(TAG, "KeywordDialog: cancel!")
            this.cancel()
        }

        binding.keywordSave.setOnClickListener {
            Log.e(TAG, "KeywordDialog: save!")

            val volId = getValueForVolumeRadioGroup()
            val keyword = KeywordObject(
                0,
                ALL_CALENDAR,
                binding.keywordTextfield.text.toString(),
                volId
            )
            tfrag?.saveKeyword(context, keyword)
            this.cancel()
        }
    }

    private fun hideAll() {
        binding.keywordKeywordLayout.visibility = View.GONE
        binding.keywordDialogRbVolume.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (binding.keywordDialogRbVolume.checkedRadioButtonId) {
            R.id.keyword_dialog_rb_loud -> return TIME_SETTING_LOUD
            R.id.keyword_dialog_rb_silent -> return TIME_SETTING_SILENT
            R.id.keyword_dialog_rb_vibrate -> return TIME_SETTING_VIBRATE
        }
        return TIME_SETTING_VIBRATE;
    }

    private fun decideState() {

        if(state==0){
            binding.keywordBack.visibility = View.INVISIBLE
            binding.keywordSave.visibility = View.GONE
            binding.keywordNext.visibility = View.VISIBLE
        }else if (state == 1){
            binding.keywordSave.visibility = View.VISIBLE
            binding.keywordBack.visibility = View.VISIBLE
            binding.keywordNext.visibility = View.GONE
        }else {
            binding.keywordBack.visibility = View.VISIBLE
            binding.keywordNext.visibility = View.VISIBLE
            binding.keywordSave.visibility = View.GONE
        }

        when (state) {
            0 -> {
                binding.keywordDialogTitle.text = context.getText(R.string.keyword_dialog_title_title)
                binding.keywordKeywordLayout.visibility = View.VISIBLE
            }
            1 -> {
                binding.keywordDialogTitle.text = context.getText(R.string.schedule_dialog_title_volume)
                binding.keywordDialogRbVolume.visibility = View.VISIBLE

            }

        }

    }
}