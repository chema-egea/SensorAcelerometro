package canales.egea.chema.SensorAcelerometro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    //Variables para gestionar la clase sensor
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    //Variables para gestionar el shake (agitado)
    private long lastUpdate = 0; //tiempo que paso desde el ultimo update
    private float last_x, last_y, last_z; //ultimos valores de x,y,z
    private static final int SHAKE_THRESHOLD = 600; //constante para saber si agitar valores (si la pasamos, actualizamos)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos variables que gestionan los sensores
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //Recogemos del evento la instancia al sensor
        Sensor mySensor = event.sensor;

        //Si es el aacelerometro, hacemos nuestra magia
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {

            //Recogemos el valor del agitado en los ejes x, y, z
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            //Hacemos una ligera modificacion para no reasignar valores cada instante, dejemos un poco de margen para que no explote
            long curTime = System.currentTimeMillis();

            //Hacemos las tipicas comprobaciones de deltatime para actualizar valores
            if ((curTime - lastUpdate) > 100)
            {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                //Creamos un valor para gestionar cuando actualizar los valores
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                //Si pasamos ese valor para actualizar los ejes, pues actualizamos
                if (speed > SHAKE_THRESHOLD)
                {
                    //Escribimos valores
                    TextView ejeX = (TextView)findViewById(R.id.ejeX);
                    TextView ejeY = (TextView)findViewById(R.id.ejeY);
                    TextView ejeZ = (TextView)findViewById(R.id.ejeZ);

                    ejeX.setText(""+Integer.toString((int) last_x));
                    ejeY.setText(""+Integer.toString((int) last_y));
                    ejeZ.setText(""+Integer.toString((int) last_z));
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Importante implementar y gestionar los métodos onPause y onResume por si otras aplicaciones quieren usar los sensores que usamos
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
