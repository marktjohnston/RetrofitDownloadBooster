package downloadbooster.mj.com.retrofitdownloadbooster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements DownloadBooster.DownloadCallBack {

    Button btnDownloadButton;
    EditText editFullURL, editNumberOfParts, editSizeOfParts;
    TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDownloadButton = (Button) findViewById(R.id.btnDownload);
        editFullURL = (EditText)findViewById(R.id.TextURL);
        editNumberOfParts = (EditText)findViewById(R.id.TextNumberOfParts);
        editSizeOfParts = (EditText)findViewById(R.id.TextSizeOfParts);
        textStatus = (TextView)findViewById(R.id.TextStatus);

        btnDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String URL = editFullURL.getText().toString().isEmpty() ? "http://code.jquery.com/jquery-1.11.3.js" : editFullURL.getText().toString();
                int numberOfParts = editNumberOfParts.getText().toString().isEmpty() ? 1 : Integer.parseInt(editNumberOfParts.getText().toString());
                int sizeOfParts = editSizeOfParts.getText().toString().isEmpty() ? 1 : Integer.parseInt(editSizeOfParts.getText().toString());
                DownloadBooster db = new DownloadBooster(URL, numberOfParts, sizeOfParts);

                db.startDownload(MainActivity.this);
            }
        });

    }


    @Override
    public void DownloadComplete(byte[] bytes) {

        String outputFileName = this.getCacheDir() + "testfile";

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFileName));
            bos.write(bytes);
            bos.flush();
            bos.close();

        }
        catch(Exception e) {
            Log.e("MainActivity", "Unable to write to file");
        }
    }

    @Override
    public void DownloadError(String Error) {

        textStatus.setText(Error);
    }


}
