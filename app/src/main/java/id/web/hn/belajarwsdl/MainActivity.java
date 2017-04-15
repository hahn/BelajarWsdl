package id.web.hn.belajarwsdl;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Proxy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String NAMESPACE = "https://www.w3schools.com/xml/";
    private final static String MAIN_REQUEST_URL = NAMESPACE + "tempconvert.asmx";
    private final static String METHOD_C_TO_F = "CelsiusToFahrenheit";
    private final static String METHOD_F_TO_C = "FahrenheitToCelsius";
    private final static String SOAP_ACTION = NAMESPACE;
    private TextView txt;
    private Button btnCtoF, btnFtoC;
    private EditText editText;
    private TaskWsdl task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.txt);
        btnCtoF = (Button) findViewById(R.id.btn_celsius_to_fahrenheit);
        btnFtoC = (Button) findViewById(R.id.btn_fahrenheit_to_celsius);
        editText = (EditText) findViewById(R.id.edt_value);

        btnCtoF.setOnClickListener(this);
        btnFtoC.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        task.cancel(true);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        String value;
        value = editText.getText().toString();
        if(view == btnCtoF){
            task = new TaskWsdl();
            task.execute(value, METHOD_C_TO_F);
        } else if(view == btnFtoC){
            task = new TaskWsdl();
            task.execute(value, METHOD_F_TO_C);
        }
    }

    private String ubahSatuan(String fValue, String methodName) throws IOException, XmlPullParserException, NetworkOnMainThreadException {

        String action = SOAP_ACTION + methodName;
        SoapObject request = new SoapObject(NAMESPACE, methodName);
        HttpTransportSE ht = getHttpTransportSE();

        if(methodName.equals(METHOD_C_TO_F)){
            request.addProperty("Celsius", fValue);
        } else {
            request.addProperty("Fahrenheit", fValue);
        }

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        ht.call(action, envelope);
        SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

        String data = resultString.toString();
//        Log.d("SOAP", "DATA: " + data);

        return data;

    }


    private HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,MAIN_REQUEST_URL,60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    private class TaskWsdl extends AsyncTask<String, Void, Void>{

        String data;
        String DEGREE  = "\u00b0";

        @Override
        protected Void doInBackground(String... strings) {
            String value = strings[0];
            String methodName = strings[1];
            try {
                data = ubahSatuan(value, methodName);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(null != data){
                txt.setText(String.format("%s%s", data, DEGREE));
            } else {
                txt.setText("uuuu error");
            }
        }
    }
}
