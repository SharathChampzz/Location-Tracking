package com.example.covidwarriors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class EnteryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String FILE_NAME = "login.txt";

    String stored_location;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entery);

        if(fileExists()){
            startActivity(new Intent(EnteryActivity.this , MainActivity.class));
            finish();
        }

        /*
        load_offline();
        if(!stored_location.equals(NULL)){
            startActivity(new Intent(EnteryActivity.this , MainActivity.class));
        }  */

        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinner2 =  findViewById(R.id.spinner2);
        Spinner spinner3 =  findViewById(R.id.spinner3);
        final EditText et = findViewById(R.id.editText2);
        name  = findViewById(R.id.user_name);

        Button bt =  findViewById(R.id.button1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.state, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.district, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.Taluk, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ph = et.getText().toString();
                String details = concat + "/" + ph;
                //Toast.makeText(EnteryActivity.this,concat+"/"+ph, Toast.LENGTH_LONG).show();
                save_offline(details , FILE_NAME);
                save_offline(name.getText().toString() , "user.txt");
                startActivity(new Intent(EnteryActivity.this , MainActivity.class));

            }
        });
    }

    private boolean fileExists() {
            File file = getBaseContext().getFileStreamPath(EnteryActivity.FILE_NAME);
            return file.exists();
    }


    private void save_offline(String text, String filename) {
            //text = "karnataka/shimoga/bhadravathi";
            //String text = mEditText.getText().toString();
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(filename, MODE_PRIVATE);
                fos.write(text.getBytes());
                //mEditText.getText().clear();
                //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                        //Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    public void load_offline(){
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            stored_location = sb.toString();
            //mEditText.setText(sb.toString());
            /*
            File dir = getFilesDir();
            File file = new File(dir , FILE_NAME);
            boolean deleted = file.delete();
            if(deleted){
                Toast.makeText(this, "Old Values Erased!", Toast.LENGTH_SHORT).show();
            }  */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    String concat;
    String state;
    String dis;
    String se;
    String d;
    String ph;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String sSelected=parent.getItemAtPosition(position).toString();
        //Toast.makeText(this,sSelected, Toast.LENGTH_SHORT).show();

        if( sSelected.equals("Karnataka")){
            state = sSelected+"/";
        }
        else if( sSelected.equals("Hassan") || sSelected.equals("Shivamogga") ||  sSelected.equals("Mysuru")
                ||  sSelected.equals("Tumakuru")){
            dis = sSelected+"/";
        }
        else if( sSelected.equals("Alur") || sSelected.equals("Arkalgud") ||  sSelected.equals("Arsikere") ||  sSelected.equals("Belur") ||  sSelected.equals("Chennarayapatna")
                ||  sSelected.equals("Hassan") ||  sSelected.equals("Holenarsipur") ||  sSelected.equals("Sakleshpur")
                || sSelected.equals("Soraba") ||  sSelected.equals("Bhadravathi") ||  sSelected.equals("Thirthahalli") ||
                sSelected.equals("Sagara") ||  sSelected.equals("Shikaripura") ||  sSelected.equals("Shimoga")
                ||  sSelected.equals("Hosanagara")
                || sSelected.equals("Piriyapatna") ||  sSelected.equals("Hunsur") ||  sSelected.equals("K.R.nagara")
                ||  sSelected.equals("Mysore")
                ||  sSelected.equals("H.D.kote") ||  sSelected.equals("Nanjangud") ||  sSelected.equals("Saragur")||
                sSelected.equals("T.N pura") ){
            se = sSelected;
        }

        d = state + dis + se;
        concat = d;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
