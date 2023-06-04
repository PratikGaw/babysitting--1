package project.babysitting.babysiting.FirebaseMessaging;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAsyncTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        // Perform network operation here
        String token = params[0];
        String title = params[1];
        String message = params[2];

        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject body = new JSONObject();
            body.put("to", token);

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);

            JSONObject data = new JSONObject();
            data.put("message", message);
            data.put("title", title);

            body.put("notification", notification);
            body.put("data", data);


            RequestBody requestBody = RequestBody.create(JSON, body.toString());

            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .addHeader("Authorization", "key=AAAAKIdQYTU:APA91bGbkwUBQDQZKnatKIZ4WQy0O1gDqN8atYAQSBfnMFED1oT5QO3JjnG31p0k7Y3-qbG3Sv3crqpLLOSH3S2Is51t7X-mFXG3aUaqQ_RgoqsQ_BWSTGrff0V_GF_6q4eipFlER6q2")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                Log.d("msg1", "Notification sent successfully");
            } else {
                Log.e("msg1", "Error sending notification: " + response.message());
            }

        } catch (Exception e) {
            Log.e("msg1", "Error sending notification", e);
        }

        return null;
    }
}
