package au.edu.tafesa.itstudies.bookcatalog.DAO;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import au.edu.tafesa.itstudies.bookcatalog.models.BookShoppingCartModel;

/**
 * Created by sruiz on 26/03/2018.
 * Updated by sruiz on 25/3/2018
 *      General tidy up.
 */

/**
 *  5JAM BOOK CATALOG Assignment PART 1
 *  Jinghua Zhong
 */

public class BookShoppingCartFileDAO {
    public static final String BOOKS_FILENAME = "books.bin";

    /**
     *
     * @param context The context(Activity) that owns the file
     * @return A new BookShoppingCartModel read from the file BOOKS_FILENAME
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public  static BookShoppingCartModel loadFromBinaryFile(Context context) throws IOException, ClassNotFoundException {

        BookShoppingCartModel modelRead=null;

        //TODO
        // Read in the Model from the binary file created by saveToBinaryFile
        try {
            File bookFile = new File(context.getFilesDir(), BOOKS_FILENAME);
            ObjectInputStream readCurrentFile = new ObjectInputStream(new FileInputStream(bookFile));
            modelRead = (BookShoppingCartModel) readCurrentFile.readObject();
            Toast.makeText(context, BOOKS_FILENAME + " loaded", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelRead;
    }

    /**
     *
     * @param context The context(Activity) that owns the file
     * @param bookShoppingCartModel The BookShoppingCartModel to be saved.
     * @throws IOException
     */
    public static void saveToBinaryFile(Context context, BookShoppingCartModel bookShoppingCartModel) throws IOException {
        //TODO
        // Write out the Model to a binary file
        try {
            File bookFile = new File(context.getFilesDir(), BOOKS_FILENAME);
            ObjectOutputStream saveWriteFile = new ObjectOutputStream(new FileOutputStream(bookFile));
            saveWriteFile.writeObject(bookShoppingCartModel);
            Toast.makeText(context, BOOKS_FILENAME + " saved to " + context.getFilesDir(), Toast.LENGTH_LONG).show();
            saveWriteFile.close();
        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}
