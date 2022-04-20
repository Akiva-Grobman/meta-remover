package com.akivagrobman.metaremover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent receivedIntent = getIntent();
        String receivedType = receivedIntent.getType();
        String receivedAction = receivedIntent.getAction();

        if (receivedAction.equals(Intent.ACTION_SEND)) {
            if (receivedType.startsWith("image/")) {
                Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (receivedUri != null) {
                    try {
                        // This action doesn't copy the image metadata, so essentially this is the
                        // metadata removal
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                receivedUri);
                        shareImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String receivedAction = getIntent().getAction();
        if (receivedAction.equals(Intent.ACTION_MAIN)) {
            Toast.makeText(this,
                    "This app shouldn't be opened this way",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Uri getImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, getString(R.string.authority),
                    file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }
}
