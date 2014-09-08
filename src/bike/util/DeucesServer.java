package bike.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import bike.table.Card;
import bike.table.Player;

public class DeucesServer extends GameServer {
	public static final String KEY_ACTION = "action";
	public static final String KEY_POSITION = "player-position";
	public static final String KEY_NAME = "name";
	public static final String KEY_TYPE = "type";
	public static final String KEY_IP = "ip";
	public static final String KEY_PORT = "port";
	public static final String KEY_CARDS = "cards";
	
	public static final String ACTION_PLAYER_DATA = "player-data";
	public static final String ACTION_JOIN = "join";
	public static final String ACTION_PLAY = "play";
	
	public DeucesServer(InetAddress address, int port, int maxConnections) {
		super(address, port, maxConnections);
	}
	
	public Player processJoinRequest() {
		Map<String,String> data = this.getMappedData();
		String action = data.get(KEY_ACTION);
		String name = data.get(KEY_NAME);
		String ip = data.get(KEY_IP);
		String port = data.get(KEY_PORT);
		
		if (action == null || !action.equals(ACTION_JOIN) || name == null || ip == null || port == null)
			return null;
		
		Player p = null;
		try {
			p = new Player(name, Player.Type.HUMAN, InetAddress.getByName(ip), Integer.parseInt(port));
		} catch (UnknownHostException|NumberFormatException e) {
			p = null;
		}
		
		return p;
	}
	
	public List<Integer> processPlayRequest() {
		Map<String,String> data = this.getMappedData();
		String action = data.get(KEY_ACTION);
		String name = data.get(KEY_NAME);
		
		if (action == null || name == null)
			return new ArrayList<Integer>();
		
		if (action.equals(ACTION_PLAY)) {
			// TODO: if cards is empty, it means the player passed
		}
		
		return new ArrayList<Integer>();
	}
	
	public void broadcastPlayerInformation(Player a, Player b, Player c, Player d) {
		Map<String,String> data = new LinkedHashMap<String,String>();
		
		data.put(KEY_ACTION, ACTION_PLAYER_DATA);
		data.put(KEY_POSITION, "0");
		data.put(KEY_NAME, a.getName());
		data.put(KEY_TYPE, a.getPlayerType().toString());
		data.put(KEY_IP, a.getPlayerIP().toString().substring(1));
		data.put(KEY_PORT, "" + a.getPlayerPort());
		data.put(KEY_CARDS, Card.compressList(a.cards));
		this.broadcastMappedData(data);
		
		data.clear();
		data.put(KEY_ACTION, ACTION_PLAYER_DATA);
		data.put(KEY_POSITION, "1");
		data.put(KEY_NAME, b.getName());
		data.put(KEY_TYPE, b.getPlayerType().toString());
		data.put(KEY_IP, b.getPlayerIP().toString().substring(1));
		data.put(KEY_PORT, "" + b.getPlayerPort());
		data.put(KEY_CARDS, Card.compressList(b.cards));
		this.broadcastMappedData(data);
		
		data.clear();
		data.put(KEY_ACTION, ACTION_PLAYER_DATA);
		data.put(KEY_POSITION, "2");
		data.put(KEY_NAME, c.getName());
		data.put(KEY_TYPE, c.getPlayerType().toString());
		data.put(KEY_IP, c.getPlayerIP().toString().substring(1));
		data.put(KEY_PORT, "" + c.getPlayerPort());
		data.put(KEY_CARDS, Card.compressList(c.cards));
		this.broadcastMappedData(data);
		
		data.clear();
		data.put(KEY_ACTION, ACTION_PLAYER_DATA);
		data.put(KEY_POSITION, "3");
		data.put(KEY_NAME, d.getName());
		data.put(KEY_TYPE, d.getPlayerType().toString());
		data.put(KEY_IP, d.getPlayerIP().toString().substring(1));
		data.put(KEY_PORT, "" + d.getPlayerPort());
		data.put(KEY_CARDS, Card.compressList(d.cards));
		this.broadcastMappedData(data);
	}
	
	public void broadcastPlayerMove(String name, List<Card> cards) {
		Map<String,String> data = new LinkedHashMap<String,String>();
		data.put(KEY_ACTION, ACTION_PLAY);
		data.put(KEY_NAME, name);
		data.put(KEY_CARDS, Card.compressList(cards));
		
		this.sendMappedData(data);
	}
}
