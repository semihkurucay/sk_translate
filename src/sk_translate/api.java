/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sk_translate;

import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author semih
 */
public class api {

    public ArrayList<language> getLanguage() {
        ArrayList<language> languages = new ArrayList<>();

        try {
            HttpResponse<String> response = Unirest.get("https://client.camb.ai/apis/source-languages")
                    .header("x-api-key", api_key.key())
                    .asString();

            if (response.getStatus() == 200) {
                String body = response.getBody();

                JSONArray arrays = new JSONArray(body);

                for (int i = 0; i < arrays.length(); i++) {
                    JSONObject object = arrays.getJSONObject(i);
                    languages.add(new language(object.getInt("id"), object.optString("language")));
                }

            } else {
                JOptionPane.showMessageDialog(null, "Hata ile karşılaşıldı.\napi.getLanguage()\nHTTP Code:".concat(String.valueOf(response.getStatus())), "HATA - api.getLanguage()", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "HATA - api.getLanguage()", JOptionPane.ERROR_MESSAGE);
        }

        return languages;
    }

    public String translate(int source, int target, String message) {
        return getText(getRun_ID(getTask_ID(source, target, message)));
    }

    private String getTask_ID(int source, int target, String message) {
        String task_id = "";

        try {
            HttpResponse<String> response = Unirest.post("https://client.camb.ai/apis/translate")
                    .header("x-api-key", api_key.key())
                    .header("Content-Type", "application/json")
                    .body("{\n  \"chosen_dictionaries\": [],\n  \"source_language\": \"" + source + "\",\n  \"target_language\": \"" + target + "\",\n  \"texts\": [\n    \"" + message + "\"\n  ]\n}")
                    .asString();

            if (response.getStatus() == 200) {
                JSONObject object = new JSONObject(response.getBody());
                task_id = object.getString("task_id");
            } else {
                JOptionPane.showMessageDialog(null, "Hata ile karşılaşıldı.\napi.getTask_ID()\nHTTP Code:".concat(String.valueOf(response.getStatus())), "HATA - api.getTask_ID()", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "HATA - api.getTask_ID()", JOptionPane.ERROR_MESSAGE);
        }

        return task_id;
    }

    private int getRun_ID(String task_id) {
        int run_id = -1;
        boolean turn = true;
        
        try {
            HttpResponse<String> response = null;
            JSONObject object = null;
            
            while (turn) {
                response = Unirest.get("https://client.camb.ai/apis/translated-story/".concat(task_id))
                        .header("x-api-key", api_key.key())
                        .asString();

                if (response.getStatus() == 200) {
                    object = new JSONObject(response.getBody());
                    
                    
                    if(!object.getString("status").equals("PENDING")){
                        turn = false;
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(null, "Hata ile karşılaşıldı.\napi.getRun_ID()\nHTTP Code:".concat(String.valueOf(response.getStatus())), "HATA - api.getRun_ID()", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
            
            run_id = object.getInt("run_id");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "HATA - api.getRun_ID()", JOptionPane.ERROR_MESSAGE);
        }

        return run_id;
    }

    private String getText(int run_id) {
        String text = "";

        try {
            HttpResponse<String> response = Unirest.get("https://client.camb.ai/apis/translation-result/".concat(String.valueOf(run_id)))
                    .header("x-api-key", api_key.key())
                    .asString();

            if (response.getStatus() == 200) {
                JSONObject object = new JSONObject(response.getBody());
                JSONArray array = object.getJSONArray("texts");
                text = array.getString(0);
                
            } else {
                JOptionPane.showMessageDialog(null, "Hata ile karşılaşıldı.\napi.getText()\nHTTP Code:".concat(String.valueOf(response.getStatus())), "HATA - api.getText()", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "HATA - api.getText()", JOptionPane.ERROR_MESSAGE);
        }

        return text;
    }
}
