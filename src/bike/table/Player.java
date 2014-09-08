package bike.table;

import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

import bike.table.Card;

public class Player {
	public enum Type {
		HUMAN, COMPUTER
	}
	
	protected String name;
	protected Player.Type type;
	protected InetAddress ip;
	protected int port;
	public List<Card> cards;
	
	public Player(String name, Player.Type type, InetAddress ip, int port) {
		this(name, type, ip, port, new ArrayList<Card>());
	}
	
	public Player(String name, Player.Type type, InetAddress ip, int port, List<Card> cards) {
		this.name = name;
		this.type = type;
		this.ip = ip;
		this.port = port;
		this.cards = new ArrayList<Card>();
		this.cards.addAll(cards);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Player.Type getPlayerType() {
		return this.type;
	}
	
	public InetAddress getPlayerIP() {
		return this.ip;
	}
	
	public int getPlayerPort() {
		return this.port;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Player))
			return false;
		
		Player p = (Player)other;
		if (this.name.equals(p.getName()))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashcode = 13;
		hashcode = hashcode * 17 + this.name.hashCode();
		hashcode = hashcode * 31 + this.ip.hashCode();
		hashcode = hashcode * 47 + this.port;
		hashcode = hashcode * 13 + this.cards.hashCode();
		return hashcode;
	}
}
