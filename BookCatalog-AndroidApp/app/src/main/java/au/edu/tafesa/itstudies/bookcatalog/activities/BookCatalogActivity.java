package au.edu.tafesa.itstudies.bookcatalog.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import au.edu.tafesa.itstudies.bookcatalog.DAO.BookShoppingCartFileDAO;
import au.edu.tafesa.itstudies.bookcatalog.R;
import au.edu.tafesa.itstudies.bookcatalog.models.Book;
import au.edu.tafesa.itstudies.bookcatalog.models.BookShoppingCartModel;


/**
 * Displays a list of books that can be added/removed to/from a shopping cart
 *
 * @author sruiz
 */
public class BookCatalogActivity extends Activity {

    private static final int NEW_BOOK_REQUEST = 1;
    private BookShoppingCartModel bookShoppingCartModel;
    private ListView listViewCatalog;
    private Button btnViewCart;

    //LIFE-CYCLE EVENTS

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Create the model
        bookShoppingCartModel = new BookShoppingCartModel();

        //Following only really needed here until loadFromFile working
        bookShoppingCartModel.addDummyCatalogData();

        // Create the Catalog and display in the List using the model to drive teh list Adapter
        listViewCatalog = (ListView) findViewById(R.id.ListViewCatalog);
        listViewCatalog.setAdapter(new CatalogListViewAdapter(bookShoppingCartModel));

        //Extract interface objects
        btnViewCart = (findViewById(R.id.ButtonViewCart));

        //Register the Event Handlers
        btnViewCart.setOnClickListener((new ButtonViewCartOnClickHandler()));


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFromFile();
    }

    @Override
    protected void onPause() {
        saveToFile();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == NEW_BOOK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                bookShoppingCartModel = (BookShoppingCartModel) data.getSerializableExtra(BookShoppingCartModel.INTENT_IDENTIFIER);
                //As we now have a new Model instance we need to update the ListView Adapter
                ((CatalogListViewAdapter) listViewCatalog.getAdapter()).setTheData(bookShoppingCartModel);
            }
        }
    }

    //DO THE WORK METHODS
    private void saveToFile() {
        //Update the file with the latest model data
        try {
            BookShoppingCartFileDAO.saveToBinaryFile(this, bookShoppingCartModel);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to save data. Problem with the file.", Toast.LENGTH_LONG).show();
        }
    }


    private void loadFromFile() {
        try {
            bookShoppingCartModel = BookShoppingCartFileDAO.loadFromBinaryFile(this);
            if (bookShoppingCartModel.getCatalog().size() == 0) {
                //Add some dummy data for testing purposes if we have nothing
                bookShoppingCartModel.addDummyCatalogData();
            }
            //Read from file creates a new Model object so have to reset the model being used by the Adapter
            ((CatalogListViewAdapter) listViewCatalog.getAdapter()).setTheData(bookShoppingCartModel);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to load data. Problem with the file.", Toast.LENGTH_LONG).show();
            //Use some dummy data (hard-coded in the model class)
            bookShoppingCartModel.addDummyCatalogData();
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "Unable to load data. Problem with the app!.", Toast.LENGTH_LONG).show();
        }
    }

    //View Adapters

    /**
     * Provides the details of how to display each row of the listViewCatalog and makes use of a List of books
     * (which comes from the bookShoppingCartModel) for the data.
     *
     * @author sruiz
     */
    class CatalogListViewAdapter extends BaseAdapter {

        public BookShoppingCartModel getTheData() {
            return theData;
        }

        public void setTheData(BookShoppingCartModel theData) {
            this.theData = theData;
        }

        private BookShoppingCartModel theData;

        public CatalogListViewAdapter(BookShoppingCartModel theData) {
            this.theData = theData;
        }

        public int getCount() {
            return theData.getCatalog().size();
        }

        public Object getItem(int position) {
            return theData.getCatalog().get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewItem rowViewComponents;

            // First time the converttView will be null and we will create it using the "book_list_item" layout
            if (convertView == null) {
                convertView = BookCatalogActivity.this.getLayoutInflater().inflate(R.layout.book_list_item, null);
                rowViewComponents = new ViewItem();
                //Set the components of rowViewComponent from the "book_list_item" layout
                rowViewComponents.textViewTitle = (TextView) convertView.findViewById(R.id.textViewItem);
                rowViewComponents.textViewPrice = (TextView) convertView.findViewById(R.id.textViewPrice);
                rowViewComponents.btnAdd = (Button) convertView.findViewById(R.id.btnAdd);
                rowViewComponents.btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
                //Register the handlers for the buttons on the rowView
                rowViewComponents.btnAdd.setOnClickListener(new ButtonAddOnClickHandler());
                rowViewComponents.btnRemove.setOnClickListener(new ButtonRemoveOnClickHandler());
                // Ensure we save this in our convertView as a Tag
                convertView.setTag(rowViewComponents);
            } else {
                rowViewComponents = (ViewItem) convertView.getTag();
            }

            //Get the current book from the Model and set the rowView components appropriately
            // Remembering to adjust whether the Add/Remove buttons are enabled or disabled
            Book curBook = theData.getCatalog().get(position);
            rowViewComponents.textViewTitle.setText(curBook.getTitle());
            rowViewComponents.textViewPrice.setText("$" + curBook.getPrice());
            rowViewComponents.btnAdd.setEnabled(!curBook.isSelected());
            rowViewComponents.btnRemove.setEnabled(curBook.isSelected());

            return convertView;
        }

        private class ViewItem {
            TextView textViewTitle;
            TextView textViewPrice;
            Button btnAdd;
            Button btnRemove;
        }

    }

    //EVENT HANDLERS

    /**
     * Handles the Add book to cart button click by adding the book at the position in the models catalog to
     * the models cart.
     *
     * @author sruiz
     */
    class ButtonAddOnClickHandler implements OnClickListener {

        public void onClick(View v) {
            int position;
            View rowView;


            rowView = (View) v.getParent();
            position = listViewCatalog.getPositionForView(rowView);
            // Ask the model to set the book at this position to be selected=true
            // Remember to notify the listview if the data change so the view updates.
            bookShoppingCartModel.setSelectedForCart(position, true);
            ((CatalogListViewAdapter) listViewCatalog.getAdapter()).notifyDataSetChanged();
        }

    }

    /**
     * Handles the Remove book from cart button click by removing the book at the position in the models catalog from
     * the models cart.
     *
     * @author sruiz
     */
    class ButtonRemoveOnClickHandler implements OnClickListener {

        public void onClick(View v) {
            int position;
            View rowView;

            rowView = (View) v.getParent();
            position = listViewCatalog.getPositionForView(rowView);
            // Ask the model to set the book at this position to be selected=false
            // Remember to notify the listview if the data change so the view updates.
            bookShoppingCartModel.setSelectedForCart(position, false);
            ((CatalogListViewAdapter) listViewCatalog.getAdapter()).notifyDataSetChanged();
        }

    }

    /**
     * Handles the View Cart button click by using an explicit Intent to start the the book at the
     * BookShoppingCartActivity
     *
     * @author sruiz
     */
    class ButtonViewCartOnClickHandler implements OnClickListener {
        public void onClick(View v) {
            // Use an Explicit Intent to start an Activity called BookShoppingCartActivity that will
            // display the books in the cart. There is no return result from the Activity we are starting,
            // it is just a view of the books in the cart.
            Intent viewShoppingCartIntent = new Intent(getBaseContext(), BookShoppingCartActivity.class);
            viewShoppingCartIntent.putExtra(BookShoppingCartModel.INTENT_IDENTIFIER, bookShoppingCartModel);
            startActivity(viewShoppingCartIntent);

        }
    }

}
