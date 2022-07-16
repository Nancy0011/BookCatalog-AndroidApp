package au.edu.tafesa.itstudies.bookcatalog.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the data for a Book Catalog (catalog) and for the selection of books made (cart)
 * @author sruiz
 *
 */
public class BookShoppingCartModel implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String INTENT_IDENTIFIER = "SHOPPING_CART_MODEL";
	private List<Book> catalog;


	public BookShoppingCartModel() {
		this(new ArrayList<Book>());
	}

	public BookShoppingCartModel(List<Book> catalog) {
		this.catalog = catalog;
	}

	public List<Book> getCatalog() {
		return catalog;
	}

	public void setCatalog(List<Book> catalog) {
		this.catalog = catalog;
	}

	/**
	 *  Sets the selected flag on book i to indicate it has been slelected for the shopping cart
	 * @param i Position in list of book to be marked as selected or not
	 * @param selectedChoice Either true for selected or false for not selected
	 */
	public void setSelectedForCart(int i, boolean selectedChoice){
		if (i<this.catalog.size()){
			catalog.get(i).setSelected(selectedChoice);

		}
	}

	public List<Book> getListOfSelectedBooks(){
		List<Book> selectedBooks = new ArrayList<Book>();
		for(Book b:this.catalog){
			if (b.isSelected()){
				selectedBooks.add(b);

			}
		}
		return selectedBooks;
	}

	public int getNumBooksSelected(){
		int  numBooksSelecetd;
		numBooksSelecetd=0;
		for(Book b:this.catalog){
			if (b.isSelected()){
				numBooksSelecetd++;
			}
		}
		return numBooksSelecetd;
	}

	/**
	 * A collection of dummy Book data to use for testing
	 */
	public void addDummyCatalogData(){
		this.getCatalog().add(new Book("The Cage", 9.99));
		this.getCatalog().add(new Book("Weight Loss", 19.99));
		this.getCatalog().add(new Book("Live and Let Die", 69.99));
		this.getCatalog().add(new Book("Life According to Me", 140.0));
		this.getCatalog().add(new Book("Let it be",10.0));
	}
}
