package bike.table;

import java.util.List;
import java.util.Arrays;

public class PlayerOrder {
	private Player[] players;
	private int currentTurn;
	
	public PlayerOrder(Player... players) {
		this.players = players;
		this.currentTurn = 0;
	}
	
	public Player getCurrentPlayer() {
		return this.players[this.currentTurn];
	}
	
	public void advance() {
		this.currentTurn = (this.currentTurn + 1) % this.players.length;
	}
	
	public List<Player> getPlayers() {
		return Arrays.asList(this.players);
	}
}
