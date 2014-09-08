package bike.games;

import java.util.List;
import java.util.ArrayList;

import bike.table.Deck;
import bike.table.Card;
import bike.table.Player;
import bike.table.PlayerOrder;
import bike.rulesets.DeucesRuleSet;

public class Deuces {
	private DeucesRuleSet ruleset;
	private Deck deck;
	private List<List<Card>> pile;
	private Player lastValidPlay;
	private PlayerOrder order;
	
	public Deuces() {
		this.ruleset = new DeucesRuleSet();
	}
	
	public void startGame(Player a, Player b, Player c, Player d) {
		this.deck = new Deck();
		this.deck.shuffle(10000);
		this.pile = new ArrayList<List<Card>>();
		
		a.cards = this.ruleset.sort(this.deck.deal(13, true));
		b.cards = this.ruleset.sort(this.deck.deal(13, true));
		c.cards = this.ruleset.sort(this.deck.deal(13, true));
		d.cards = this.ruleset.sort(this.deck.deal(13, true));
		this.order = new PlayerOrder(a, b, c, d);
		this.lastValidPlay = a;
	}
	
	public Player checkForWinner() {
		for (Player p : this.order.getPlayers()) {
			if (p.cards.size() == 0) {
				return p;
			}
		}
		
		return null;
	}
	
	public List<Card> examinePile() {
		if (this.pile.isEmpty())
			return new ArrayList<Card>();
		return this.pile.get(this.pile.size() - 1);
	}
	
	public Player getCurrentPlayer() {
		return this.order.getCurrentPlayer();
	}
	
	public Player getPlayer(int k) {
		return this.order.getPlayers().get(k);
	}
	
	public boolean isValidCombination(List<Integer> indices) {
		List<Card> cards = this.getCardsFromIndices(indices);
		if (cards.isEmpty() || this.ruleset.getCardCombination(cards) == null)
			return false;
		
		return true;
	}
	
	public boolean isValidPlay(List<Integer> indices) {
		if (!this.isValidCombination(indices))
			return false;
		
		if (this.pile.isEmpty())
			return true;
		
		List<Card> play = this.getCardsFromIndices(indices);
		List<Card> topOfPile = this.examinePile();		
		try {
			if (this.ruleset.compareCombinations(play, topOfPile) < 0)
				return false;
		} catch (IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}
	
	public boolean playCards(List<Integer> indices) {
		if (!this.isValidPlay(indices))
			return false;
		
		List<Card> playedCards = this.getCardsFromIndices(indices);
		this.order.getCurrentPlayer().cards.removeAll(playedCards);
		this.pile.add(playedCards);
		
		this.lastValidPlay = this.order.getCurrentPlayer();
		this.order.advance();
		return true;
	}
	
	public void passTurn() {
		this.order.advance();
		
		if (this.order.getCurrentPlayer().equals(this.lastValidPlay))
			this.pile.clear();
	}
	
	private List<Card> getCardsFromIndices(List<Integer> indices) {
		List<Card> hand = new ArrayList<Card>();
		for (int i : indices) {
			Card c = this.order.getCurrentPlayer().cards.get(i);
			if (c == null)
				return new ArrayList<Card>();
			
			hand.add(c);
		}
		
		return hand;
	}
}
