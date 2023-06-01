package jp.ac.jec.cm0135.rssreader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonHelper {
    public static ArrayList<RssItem> parseJson(String strJson){
        ArrayList<RssItem> ary = new ArrayList<>();
        try{
            JSONObject json = new JSONObject(strJson);
            JSONObject channel = json.getJSONObject("channel");
            JSONArray item = channel.getJSONArray("item");
            for(int i = 0; i < item.length(); i++){
                JSONObject entry = item.getJSONObject(i);
                ary.add(parseToItem(entry));
            }
        }catch (Exception e){
            Log.e("JsonHelper",e.getMessage());
        }
        return ary;
    }
    public static RssItem parseToItem(JSONObject json) throws JSONException {
        RssItem tmp = new RssItem();
        tmp.setTitle(json.getString("title"));
        tmp.setLink(json.getString("link"));
        tmp.setPubDate(json.getString("pubDate"));
        return tmp;
    }
}
