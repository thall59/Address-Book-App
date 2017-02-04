// MainActivity.java
// Hosts Address Book app's fragments
package com.deitel.addressbook;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity 
   implements ContactListFragment.ContactListFragmentListener,
      DetailsFragment.DetailsFragmentListener, 
      AddEditFragment.AddEditFragmentListener
{
   // keys for storing row ID in Bundle passed to a fragment
   public static final String ROW_ID = "row_id"; 
   
   ContactListFragment contactListFragment; // displays contact list
   
   // display ContactListFragment when MainActivity first loads
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // return if Activity is being restored, no need to recreate GUI
      if (savedInstanceState != null) 
         return;

      // check whether layout contains fragmentContainer (phone layout);
      // ContactListFragment is always displayed
      if (findViewById(R.id.fragment_container) != null)
      {
         // create ContactListFragment
         contactListFragment = new ContactListFragment();
         
         // add the fragment to the FrameLayout
         FragmentTransaction transaction = 
            getFragmentManager().beginTransaction();
         transaction.add(R.id.fragment_container, contactListFragment);
         transaction.commit(); // causes ContactListFragment to display
      }
   }
   
   // called when MainActivity resumes
   @Override
   protected void onResume()
   {
      super.onResume();
      
      // if contactListFragment is null, activity running on tablet, 
      // so get reference from FragmentManager
      if (contactListFragment == null)
      {
         contactListFragment = 
            (ContactListFragment) getFragmentManager().findFragmentById(
               R.id.contact_list_fragment);
      }
   }
   
   // display DetailsFragment for selected contact
   @Override
   public void onContactSelected(long rowID)
   {
      if (findViewById(R.id.fragment_container) != null) // phone
         displayContact(rowID, R.id.fragment_container);
      else // tablet
      {
         getFragmentManager().popBackStack(); // removes top of back stack
         displayContact(rowID, R.id.right_pane_container);
      }
   }

   // display a contact
   private void displayContact(long rowID, int viewID)
   {
      DetailsFragment detailsFragment = new DetailsFragment();
      
      // specify rowID as an argument to the DetailsFragment
      Bundle arguments = new Bundle();
      arguments.putLong(ROW_ID, rowID);
      detailsFragment.setArguments(arguments);
      
      // use a FragmentTransaction to display the DetailsFragment
      FragmentTransaction transaction = 
         getFragmentManager().beginTransaction();
      transaction.replace(viewID, detailsFragment);
      transaction.addToBackStack(null);
      transaction.commit(); // causes DetailsFragment to display
   }
   
   // display the AddEditFragment to add a new contact
   @Override
   public void onAddContact()
   {
      if (findViewById(R.id.fragment_container) != null)
         displayAddEditFragment(R.id.fragment_container, null);
      else
         displayAddEditFragment(R.id.right_pane_container, null);
   }
   
   // display fragment for adding a new or editing an existing contact
   private void displayAddEditFragment(int viewID, Bundle arguments)
   {
      AddEditFragment addEditFragment = new AddEditFragment();
      
      if (arguments != null) // editing existing contact
         addEditFragment.setArguments(arguments);
      
      // use a FragmentTransaction to display the AddEditFragment
      FragmentTransaction transaction = 
         getFragmentManager().beginTransaction();
      transaction.replace(viewID, addEditFragment);
      transaction.addToBackStack(null);
      transaction.commit(); // causes AddEditFragment to display
   }
   
   // return to contact list when displayed contact deleted
   @Override
   public void onContactDeleted()
   {
      getFragmentManager().popBackStack(); // removes top of back stack
      
      if (findViewById(R.id.fragment_container) == null) // tablet
         contactListFragment.updateContactList();
   }

   // display the AddEditFragment to edit an existing contact
   @Override
   public void onEditContact(Bundle arguments)
   {
      if (findViewById(R.id.fragment_container) != null) // phone
         displayAddEditFragment(R.id.fragment_container, arguments);
      else // tablet
         displayAddEditFragment(R.id.right_pane_container, arguments);
   }

   // update GUI after new contact or updated contact saved
   @Override
   public void onAddEditCompleted(long rowID)
   {
      getFragmentManager().popBackStack(); // removes top of back stack

      if (findViewById(R.id.fragment_container) == null) // tablet
      {
         getFragmentManager().popBackStack(); // removes top of back stack
         contactListFragment.updateContactList(); // refresh contacts

         // on tablet, display contact that was just added or edited
         displayContact(rowID, R.id.right_pane_container);
      }
   }   
}


