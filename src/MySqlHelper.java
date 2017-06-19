
public class MySqlHelper {

	protected static final String TABLE_PLAYERMATCHES = "playermatches";
	protected static final String COL_MATCH_ID = "matchId";
	protected static final String COL_QUEUE = "queue";
	protected static final String COL_TIMESTAMP = "timestamp";
	protected static final String COL_KILLS = "kills";
	protected static final String COL_DEATHS = "deaths";
	protected static final String COL_ASSISTS = "assists";
	protected static final String COL_WIN = "win";
	
	protected static final String TABLE_CHAMPIONS = "champions";
	protected static final String COL_CHAMPION_ID = "championId"; // shared with playermatches table
	protected static final String COL_CHAMPION_NAME = "championName";
	
	protected static final String TABLE_PLAYERS = "players";
	protected static final String COL_ACCOUNT_ID = "accountId"; // shared with playermatches table
	protected static final String COL_SUMMONER_NAME = "summonerName";
	
}
