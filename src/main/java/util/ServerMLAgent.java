package util;

import com.google.gson.Gson;
import engine.core.MarioWorld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ServerMLAgent implements MLAgent {

    private URL url;
    private float x;
    private int tick;


    public ServerMLAgent(String URI) {
        try {
            this.url = new URL(URI);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean[] getActions(int[][] frame, float reward) {

        String POST_PARAMS = "{\"frame\":[";
        boolean actions[] = new boolean[5];

        for (int i = 0; i < frame.length-1; i++) {
            POST_PARAMS += Arrays.toString(frame[i]);
            POST_PARAMS += ",";
        }
        POST_PARAMS += Arrays.toString(frame[frame.length-1]) + "],";
        POST_PARAMS += "\"reward\":" + reward + "}";
        //System.out.println(POST_PARAMS);
        URL obj = null;
        try {
            obj = new URL("http://127.0.0.1:5000/");
            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("userId", "a1bcdefgh");
            postConnection.setRequestProperty("Content-Type", "application/json");
            postConnection.setDoOutput(true);
            OutputStream os = postConnection.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            int responseCode = postConnection.getResponseCode();
            //System.out.println("POST Response Code :  " + responseCode);
            //System.out.println("POST Response Message : " + postConnection.getResponseMessage());

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        postConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in .readLine()) != null) {
                    response.append(inputLine);
                } in .close();

                int[] action_binary = new Gson().fromJson(response.toString(), int[].class);
                for (int i = 0; i < action_binary.length; i++) {
                    actions[i] = (action_binary[i] == 1);
                }
                //System.out.println(Arrays.toString(actions));
                //System.out.println(Arrays.toString(action_binary));

            } else {
                //System.out.println("POST NOT WORKED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return actions;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    @Override
    public float calculateReward(MarioWorld world) {
        //v: measure distance x before the step and after the step
        //c: difference in ticks between frames
        //d: agent dies and gets large penalty

        float v = world.mario.x - this.x;
        float c = this.tick - world.currentTick;
        float d = world.mario.alive == true ? 0.0f : -15.0f;

        float reward = v + c +d;
        reward = reward > 15 ? 15 : reward;
        reward = reward < -15 ? -15 : reward;
        return reward;
    }
}
