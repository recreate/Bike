package bike.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import bike.table.Player;

public class DeucesClient extends GameClient {
	public DeucesClient(InetAddress address, int port) {
		super(address, port);
	}
	
	public void sendJoinRequest(String playerName, String playerIP, String playerPort) {
		Map<String,String> data = new LinkedHashMap<String,String>();
		
		data.put(DeucesServer.KEY_ACTION, DeucesServer.ACTION_JOIN);
		data.put(DeucesServer.KEY_NAME, playerName);
		data.put(DeucesServer.KEY_IP, playerIP);
		data.put(DeucesServer.KEY_PORT, playerPort);
		
		this.sendMappedData(data);
	}
	
	public Player[] getPlayerInformation() {
		List<Player> players = new ArrayList<Player>(4);
		
		while (players.size() < 4) {
			Map<String,String> data = this.getMappedData();
			String action = data.get(DeucesServer.KEY_ACTION);
			String position = data.get(DeucesServer.KEY_POSITION);
			String name = data.get(DeucesServer.KEY_NAME);
			String type = data.get(DeucesServer.KEY_TYPE);
			String ip = data.get(DeucesServer.KEY_IP);
			String port = data.get(DeucesServer.KEY_PORT);
			String cards = data.get(DeucesServer.KEY_CARDS);
			
			if (action == null || !action.equals(DeucesServer.ACTION_PLAYER_DATA) || position == null || name == null || ip == null || port == null || cards == null)
				return null;
			
			Player p = null;
			try {
				p = new Player(name, Player.Type.valueOf(type), InetAddress.getByName(ip), Integer.parseInt(port), Card.uncompressList(cards));
				players.add(Integer.parseInt(position), p);
			} catch (UnknownHostException|NumberFormatException e) {
				continue;
			}
		}
		
		return players.toArray(new Player[players.size()]);
	}
	
	public void sendPassRequest(String playerName) {
		Map<String,String> data = new LinkedHashMap<String,String>();
		data.put(DeucesServer.KEY_ACTION, DeucesServer.ACTION_PASS);
		data.put(DeucesServer.KEY_NAME, playerName);
		
		this.sendMappedData(data);
	}
	
	public void sendPlayRequest(List<Integer> indices) {
		
	}
}
