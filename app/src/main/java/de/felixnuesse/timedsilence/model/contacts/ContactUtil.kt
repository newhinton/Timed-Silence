package de.felixnuesse.timedsilence.model.contacts

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.extensions.e
import de.felixnuesse.timedsilence.util.PermissionManager
import java.io.IOException


class ContactUtil(private var mContext: Context) {

    companion object {
        private const val TAG = "ContactUtil"
    }


    private val PROJECTION = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.STARRED,
        ContactsContract.Contacts.PHOTO_URI
    )

    fun getContactList(): ArrayList<Contact> {
        val contactList = arrayListOf<Contact>()

        if(!PermissionManager(mContext).grantedContacts()) {
            e("We dont have access to the contacts!")
            return contactList
        }


        getQuery()?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)

            while (cursor.moveToNext()) {
                var c = Contact(cursor.getString(nameIndex))
                c.photo = getPhoto(cursor.getLong(idIndex))
                contactList.add(c)
            }

        }
        return contactList
    }

    private fun getQuery(): Cursor? {
        return mContext.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            PROJECTION,
            ContactsContract.Contacts.STARRED + " = ?",
            arrayOf("1"),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
    }

    private fun getPhoto(id: Long): Bitmap {
        var photo = BitmapFactory.decodeResource(mContext.resources, R.drawable.icon_person)

        val uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id)

        try {
            val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                mContext.contentResolver,
                uri
            )
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream)
            }
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }

}