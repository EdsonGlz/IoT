package com.example.testubi2;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;

public class MainActivity extends Activity {
    private TextView temperatura;
    private TextView humedad;
    private ImageView tempImg;
    private Boolean active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatura = findViewById(R.id.temperatura);
        humedad = findViewById(R.id.humedad);
        tempImg = findViewById(R.id.tempImg);
        active = true;
        new ApiUbidots().execute();

    }



    @Override
    protected void onStop() {

        super.onStop();
        active = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        new ApiUbidots().execute();
    }


    public class ApiUbidots extends AsyncTask<Integer, ProgressUpdate, Void> {
        private final String API_KEY_UBIDOTS = "BBFF-fa11c4afe5b4eff24d42aeac44aeaa3476a";
        private final String TempVariable_ID = "63f56fb39234c4000d8e5b66";
        private final String HumVariable_ID = "63f56fc0bd2642000e8ab972";


        @Override
        protected Void doInBackground(Integer... params) {
            while(active){
                ApiClient apiClient = new ApiClient(API_KEY_UBIDOTS); //API_KEY de Ubidots
                Variable temperatura = apiClient.getVariable(TempVariable_ID); //Obtener referencia a la variable de temperatura con su ID
                Variable humedad = apiClient.getVariable(HumVariable_ID); //Obtener referencia a la variable de humedad con su ID
                Value[] valoresTemperatura = temperatura.getValues(); //Obtener valores cambiantes de temperatura
                Value[] valoresHumedad = humedad.getValues(); //Obtener valores cambiantes de humedad

                Log.wtf("UBIDOTS", String.valueOf(valoresTemperatura[0].getValue()));
                Log.wtf("UBIDOTS", String.valueOf(valoresHumedad[0].getValue()));


                String valorActualTemp = String.valueOf(valoresTemperatura[0].getValue()); //obtener valor actual de temperatura
                String valorActualHum = String.valueOf(valoresHumedad[0].getValue()); //obtener valor actual de humedad
                publishProgress(new ProgressUpdate(valorActualTemp, valorActualHum)); //actualizar UI con valores actuales
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ProgressUpdate... values) { //método para actualizar UI
            super.onProgressUpdate(values);
            temperatura.setText(values[0].temperatura);
            humedad.setText(values[0].humedad);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            new ApiUbidots().execute();
        }
    }

    class ProgressUpdate {
        public final String temperatura;
        public final String humedad;

        public ProgressUpdate(String temperatura, String humedad) {
            this.temperatura = temperatura;
            this.humedad = humedad;

            if(Double.parseDouble(temperatura) < 30 && temperatura!= null){
                tempImg.setColorFilter(Color.GREEN);
            }else{
                tempImg.setColorFilter(Color.RED);
            }
        }
    }
}