package com.beshoykamal.transelation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>, TextToSpeech.OnInitListener {
    TextView textView;
    EditText translate;
    Spinner spinner1,spinner2;
    ListView listt;
    TextToSpeech tts;
    ImageButton restext;
    ArrayList<String> ask=new ArrayList<>();
    ArrayList<String> ans=new ArrayList<>();
    private static final int REQUEST_CODE_SPEECH_INPUT =1000 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);
        translate=findViewById(R.id.Translate);
        spinner1=findViewById(R.id.spinner1);
        spinner2=findViewById(R.id.spinner2);
        restext=findViewById(R.id.rectext);

        listt=findViewById(R.id.listt);

        tts=new TextToSpeech(this,this);

        restext.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                speak();
            }

            private void speak() {
                Intent in = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                in.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                in.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
                in.putExtra(RecognizerIntent.EXTRA_PROMPT,"hi speak something");

                try {

                    startActivityForResult(in, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    Toast.makeText(MainActivity.this, "ERRoR", Toast.LENGTH_SHORT).show();
                }


            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && null!=data) {
                    ArrayList<String> res=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    translate.setText(res.get(0));
                }
            }
        }
    }

    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS){
            tts.setPitch(0.7f);
        }
        else
            Toast.makeText(this, "version not support", Toast.LENGTH_SHORT).show();
    }

    public void start(View view) {

//        if (ask.contains(translate.getText().toString())) {
//            int i= ask.indexOf(translate.getText().toString());
//
//            textView.setText(ans.get(i));
//            return;
//        }
        textView.setText("");
        RequestQueue queue= Volley.newRequestQueue(this);
//        https://api.mymemory.translated.net/get?q=Hello World!&langpair=en|it
        String url = "https://api.mymemory.translated.net/get?q="+translate.getText().toString()
                +"&langpair="+spinner1.getSelectedItem().toString()+"|"+spinner2.getSelectedItem().toString()
                +"&key=19506a7ec0769ba290ad";

//        String url = "http://api.mymemory.translated.net/get?q="+translate.getText().toString()+"&langpair="+spinner1.getSelectedItem().toString()+"|"+spinner2.getSelectedItem().toString();
//        String url = "http://api.mymemory.translated.net/get?q=&langpair=en|ar";

        JsonObjectRequest request=new JsonObjectRequest(url,null,this,this);

        queue.add(request);
//
//        tts.setLanguage(Locale.ENGLISH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(translate.getText(),TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else
            tts.speak(""+translate.getText(),TextToSpeech.QUEUE_FLUSH,null);


    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "no connection  "+error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
           String res= response.getJSONObject("responseData").getString("translatedText");
           textView.setText(res+"\n");

            ask.add(translate.getText().toString());
            ans.add(res);
            JSONArray matches = response.getJSONArray("matches");
            for (int i = 0; i <matches.length() ; i++) {
                String translation = matches.getJSONObject(i).getString("translation");

                textView.append("\n"+translation+"\n");


////                String[] mach = translation.split(",");
//                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, split);
//                listt.setAdapter(adapter);

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
