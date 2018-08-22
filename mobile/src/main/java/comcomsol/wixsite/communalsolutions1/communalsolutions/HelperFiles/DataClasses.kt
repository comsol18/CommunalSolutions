package comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.Contact

data class DBValues(
        val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
        val reference: DatabaseReference = database.reference,
        val private: DatabaseReference = reference.child("private"),
        val public: DatabaseReference = reference.child("public"),
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
        val uuid: String = user!!.uid.hashCode().toString()
)

data class DBReferences(
        val database: DBValues = DBValues(),
        val userReference: DatabaseReference = database.private.child("users/${database.uuid}"),
        val locReference: DatabaseReference = database.private.child("locations/${database.uuid}"),
        val contactsReference: DatabaseReference = database.private.child("contacts/${database.uuid}"),
        val debugReference: DatabaseReference = database.private.child("debug/${database.uuid}")
)

data class DBPublicReferences(
        val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
        val dbReference: DatabaseReference = database.reference.child("public")
)

data class ContactList(
        var contact1: Contact = Contact(),
        var contact2: Contact = Contact(),
        var contact3: Contact = Contact(),
        var contact4: Contact = Contact(),
        var contact5: Contact = Contact(),
        var contact6: Contact = Contact()
)
