package net.iessanclemente.a14felipecm.ud_a_45_a_a14felipecm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Principal extends AppCompatActivity {

    private String XML_URL="http://manuais.iessanclemente.net/images/2/20/Platega_pdm_rutas.xml";
    String rutaAr = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RUTAS/";
    private File fArquivo;
    private Thread thread;
    boolean descargado;
    TextView tvRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        tvRutas = (TextView) findViewById(R.id.txt_rutas);
    }

    public void actualizar(View v){
        ConnectivityManager CM=null;
        NetworkInfo NI=null;
        NI = comprobarInternet(NI);
        descargado=false;
        if (NI != null && NI.isConnected()) {
            switch(NI.getType()){
                case ConnectivityManager.TYPE_MOBILE:Log.i("CONEXION","MOVIL");
                    thread = new Thread(){
                        @Override
                        public void run(){
                            descargado = downloadXML();
                        }
                    };
                    thread.start();
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    Log.i("CONEXION","ETHERNET");
                    thread = new Thread(){
                        @Override
                        public void run(){
                            descargado = downloadXML();
                        }
                    };
                    thread.start();
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    Log.i("CONEXION","WIFI");
                    thread = new Thread(){
                        @Override
                        public void run(){
                            descargado = downloadXML();
                        }
                    };
                    thread.start();
                    break;
            }
            while(thread.isAlive()){

            }

            if(descargado){
                Toast.makeText(Principal.this, "Archivo descargado con éxito", Toast.LENGTH_SHORT).show();
                XmlPullParser xpp = abrirXml();
                Log.i("INFO","Despues de abrirXml");
                ArrayList<Ruta> arrRutas = procesarXml(xpp);
                Log.i("INFO","Despues de aprocesar");
                llenarTv(arrRutas);
                Log.i("INFO", "Despues de llenarTv");
            }else{
                Toast.makeText(Principal.this, "No se ha podido descargar el archivo", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // NON TEMOS REDE
            Toast.makeText(Principal.this, "No hay internet", Toast.LENGTH_SHORT).show();
        }
    }

    public XmlPullParser abrirXml(){
        File arqXml = new File(rutaAr + File.separator + "Platega_pdm_rutas.xml");
        XmlPullParser parser=null;
        try {
            FileInputStream fis_sd = new FileInputStream(arqXml);
            InputStreamReader isreader_sd = new InputStreamReader(fis_sd);
            parser = Xml.newPullParser();
            parser.setInput(fis_sd, "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("PROCESAR",e.toString());
        } catch (XmlPullParserException e) {
            Log.e("PROCESAR",e.toString());
        }
        return parser;
    }

    public ArrayList<Ruta> procesarXml(XmlPullParser xmlp){
        ArrayList<Ruta> rutas = new ArrayList<Ruta>();
        int evento = 0;
        try {
            evento = xmlp.nextTag();

        Ruta route = null;

        while(evento != XmlPullParser.END_DOCUMENT) {
            if(evento == XmlPullParser.START_TAG) {
                if (xmlp.getName().equals("ruta")) {      // Una nueva ruta
                    route = new Ruta();
                    evento = xmlp.nextTag();      // Pasamos a <nome>
                    route.setNome(xmlp.nextText());
                    evento = xmlp.nextTag();      // Pasamos a <descricion>
                    route.setDescripcion(xmlp.nextText());
                }
            }
            if(evento == XmlPullParser.END_TAG) {
                if (xmlp.getName().equals("ruta")) {      // Una nueva ruta
                    rutas.add(route);
                }
            }
            evento = xmlp.next();
        }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rutas;
    }

    public void llenarTv(ArrayList<Ruta> alr){
        String srutas="";
        for(int i=0;i<alr.size();i++){
            srutas +="################################# \n Nome: "+alr.get(i).getNome()+"\n Descricion: \n"+alr.get(i).getDescripcion();
        }
        tvRutas.setText(srutas);
    }

    public NetworkInfo comprobarInternet(NetworkInfo networkInfo){
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return networkInfo = connMgr.getActiveNetworkInfo();
    }

    public boolean downloadXML(){
        Boolean downloaded = false;
        fArquivo= new File(rutaAr);
        if(!fArquivo.exists()) fArquivo.mkdirs();
        URL url=null;
        try {
            url = new URL(XML_URL);

        HttpURLConnection conn=null;
        String nomeArquivo = Uri.parse(XML_URL).getLastPathSegment();
        fArquivo = new File(rutaAr + File.separator + nomeArquivo);
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);     /* milliseconds */
            conn.setConnectTimeout(15000);  /* milliseconds */
            conn.setRequestMethod("POST");
            conn.setDoInput(true);                  /* Indicamos que a conexión vai recibir datos */
            conn.connect();
            int response = conn.getResponseCode();
            if (response != HttpURLConnection.HTTP_OK){
                downloaded=false;
                return false;
            }
            OutputStream os = new FileOutputStream(fArquivo);
            InputStream in = conn.getInputStream();
            byte data[] = new byte[1024];   // Buffer a utilizar
            int count;
            while ((count = in.read(data)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();
            os.close();
            in.close();
            conn.disconnect();
            Log.i("COMUNICACION", "ACABO");
            downloaded=true;
            return true;
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("COMUNICACION",e.getMessage());
            downloaded=false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("COMUNICACION",e.getMessage());
            downloaded=false;
        }
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            Log.e("COMUNICACION",e1.getMessage());
            e1.printStackTrace();
            downloaded=false;
        }
        return downloaded;
    }
}
