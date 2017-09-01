package LeagueStatsClient;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class FetchStatsHelper {
	// Query specific column names
	private static final String TOT_KILLS = "totalKills";
	private static final String TOT_DEATHS = "totalDeaths";
	private static final String TOT_ASSISTS = "totalAssists";
	private static final String KDA = "KDA";
	private static final String NUM_WINS = "numWins";
	private static final String NUM_GAMES= "numGames";
	private static final String WIN_RATE = "winRate";
	private static final String AVG_CS_0_TO_10 = "avgCs0to10";
	private static final String AVG_CS_10_TO_20 = "avgCs10to20";
	private static final String AVG_GOLD_0_TO_10 = "avgGold0to10";
	private static final String AVG_GOLD_10_TO_20 = "avgGold10to20";
	private static final String AVG_XP_0_TO_10 = "avgXp0to10";
	private static final String AVG_XP_10_TO_20 = "avgXp10to20";
	
	private static final String SERVER_IP = "71.178.160.111";
	
	public static Object[][] getData() {
		String url = "http://" + SERVER_IP + "/leaguestats/getAllData";
		try {
	        String allDataJsonStr = IOUtils.toString(new URL(url), "utf-8");
	        JSONArray allDataRows = (JSONArray) JSONValue.parseWithException(allDataJsonStr);
	        
	        final int numRows = allDataRows.size();
	        final int numCols = ((JSONObject) allDataRows.get(0)).size();
	        Object[][] data = new Object[numRows][numCols];
	        for (int row = 0; row < numRows; ++row) {
        		JSONObject rowObj = (JSONObject) allDataRows.get(row);
        		
        		int col = 0;
        		data[row][col++] = rowObj.get(MySqlHelper.COL_SUMMONER_NAME);
        		data[row][col++] = rowObj.get(MySqlHelper.COL_CHAMPION_NAME);
        		data[row][col++] = myParseInt(rowObj, TOT_KILLS);
        		data[row][col++] = myParseInt(rowObj, TOT_DEATHS);
        		data[row][col++] = myParseInt(rowObj, TOT_ASSISTS);
        		data[row][col++] = myParseFloat(rowObj, KDA);
        		data[row][col++] = myParseInt(rowObj, NUM_WINS);
        		data[row][col++] = myParseInt(rowObj, NUM_GAMES);
        		data[row][col++] = myParseFloat(rowObj, WIN_RATE);
        		data[row][col++] = myParseFloat(rowObj, AVG_CS_0_TO_10);
        		data[row][col++] = myParseFloat(rowObj, AVG_CS_10_TO_20);
        		data[row][col++] = myParseFloat(rowObj, AVG_GOLD_0_TO_10);
        		data[row][col++] = myParseFloat(rowObj, AVG_GOLD_10_TO_20);
        		data[row][col++] = myParseFloat(rowObj, AVG_XP_0_TO_10);
        		data[row][col++] = myParseFloat(rowObj, AVG_XP_10_TO_20);
	        }
	        
	        return data;
		} catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	private static Integer myParseInt(JSONObject row, String key) {
    	Object o = row.get(key);
    	if (o == null) {
    		return null;
    	} else {
    		return Integer.parseInt((String) o); 
    	}
    }
    
    private static Float myParseFloat(JSONObject row, String key) {
    	Object o = row.get(key);
    	if (o == null) {
    		return null;
    	} else {
    		return Float.parseFloat((String) o);        		
    	}
    }
    
}
