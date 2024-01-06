package de.felixnuesse.timedsilence.model.contacts

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import de.felixnuesse.timedsilence.util.PermissionManager


class ContactUtil(private var mContext: Context) {

    companion object {
        private const val TAG = "ContactUtil"
    }


    private val PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.STARRED
    )

    fun getContactList(): ArrayList<Contact> {
        val contactList = arrayListOf<Contact>()

        val cr: ContentResolver = mContext.contentResolver
        val cursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        cursor?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val starredIndex = cursor.getColumnIndex(ContactsContract.Contacts.STARRED)
            while (cursor.moveToNext()) {
                if(cursor.getInt(starredIndex) == 1) {
                    contactList.add(Contact(cursor.getString(nameIndex)))
                }
            }
        }
        return contactList
    }

}