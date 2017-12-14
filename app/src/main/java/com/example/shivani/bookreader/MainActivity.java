package com.example.shivani.bookreader;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title,title2,authors,authors2;
    private String bookName1 = "books/test.epub", bookName2 = "books/TheSilverChair.epub";
    private ImageView cover,cover2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout book1 = (LinearLayout) findViewById(R.id.bookOne);
        LinearLayout book2 = (LinearLayout) findViewById(R.id.bookTwo);
        book1.setOnClickListener(this);
        book2.setOnClickListener(this);

        cover = (ImageView) findViewById(R.id.image_book);
        title = (TextView) findViewById(R.id.title_book);
        authors = (TextView) findViewById(R.id.author_book);
        cover2 = (ImageView) findViewById(R.id.image_book2);
        title2 = (TextView) findViewById(R.id.title_book2);
        authors2 = (TextView) findViewById(R.id.author_book2);
        
        loadBook(bookName1,title,authors,cover);
        loadBook(bookName2,title2,authors2,cover2);
    }

    public void loadBook(String bookName, TextView textTitle,TextView textAuthor, ImageView image) {

        AssetManager assetManager = getAssets();

        try {
            InputStream epubInputStream = assetManager.open(bookName);
            Book book = (new EpubReader()).readEpub(epubInputStream);

            String authorBook = String.valueOf(book.getMetadata().getAuthors());
            String titleBook = book.getTitle();
            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());

            textAuthor.setText(authorBook);
            textTitle.setText(titleBook);
            image.setImageBitmap(coverImage);

            Log.i("epublib", "author(s): " + authorBook);
            Log.i("epublib", "title: " + titleBook);
            Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by " + coverImage.getHeight() + " pixels");

            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
            
        } catch (IOException e) {
            Log.e("epublib", e.getMessage());
        }
    }

    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("epublib", tocString.toString());
            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookOne:
                Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
                intent.putExtra("Book", "One");
                startActivity(intent);
                break;
            case R.id.bookTwo:
                Intent intent2 = new Intent(MainActivity.this, ReaderActivity.class);
                intent2.putExtra("Book", "Two");
                startActivity(intent2);
                break;
        }
    }
}
