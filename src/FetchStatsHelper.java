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
	
	// Table column headers
	public static final String COL_PLAYER_NAME = "Summoner";
	public static final String COL_CHAMP = "Champion";
	public static final String COL_KILLS = "Kills";
	public static final String COL_DEATHS= "Deaths";
	public static final String COL_ASSISTS= "Assists";
	public static final String COL_KDA = "KDA";
	public static final String COL_NUM_WINS= "# Wins";
	public static final String COL_NUM_GAMES= "# Games";
	public static final String COL_WIN_RATE = "Win %";
	public static final String COL_CS_0_TO_10 = "cs/min@0-10";
	public static final String COL_CS_10_TO_20 = "cs/min@10-20";
	public static final String COL_GOLD_0_TO_10 = "gold/min@0-10";
	public static final String COL_GOLD_10_TO_20 = "gold/min@10-20";
	public static final String COL_XP_0_TO_10 = "xp/min@0-10";
	public static final String COL_XP_10_TO_20 = "xp/min@10-20";
	
	public static final String[] COL_HEADERS = {
									COL_PLAYER_NAME,
	                                COL_CHAMP,
	                                COL_KILLS,
	                                COL_DEATHS,
	                                COL_ASSISTS,
	                                COL_KDA,
	                                COL_NUM_WINS,
	                                COL_NUM_GAMES,
	                                COL_WIN_RATE,
	                                COL_CS_0_TO_10,
	                                COL_CS_10_TO_20,
	                                COL_GOLD_0_TO_10,
	                                COL_GOLD_10_TO_20,
	                                COL_XP_0_TO_10,
	                                COL_XP_10_TO_20
	                          	};
	
	public static Object[][] getData() {
		String url = "http://71.178.243.38/leaguestats/getAllData";
		try {
	        String allDataJsonStr = IOUtils.toString(new URL(url), "utf-8");
	        JSONArray allDataRows = (JSONArray) JSONValue.parseWithException(allDataJsonStr);
	        
	        final int numRows = allDataRows.size();
	        final int numCols = COL_HEADERS.length;
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
