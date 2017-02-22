package np.com.qpay.kycformapp.http;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qpay on 2/17/17.
 */

public class HttpUrlConnectionPost {

    private static final String TAG = "dinesh";

    public static String sendHTTPData(String urlpath, JSONObject json) {
        HttpURLConnection connection = null;
        try {

            URL url=new URL(urlpath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(40000);
            connection.setReadTimeout(40000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(json.toString());
            streamWriter.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response + "\n");
                }
                bufferedReader.close();

                Log.d(TAG, stringBuilder.toString());
                return stringBuilder.toString();
            } else {
                Log.e(TAG, connection.getResponseMessage());
                return null;
            }
        } catch (Exception exception){
            Log.e(TAG, exception.toString());
            return null;
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }
}
