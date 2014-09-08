package bike.table;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import bike.table.Card;
import bike.table.Player;

public class Deck {
	public static final int MAX_CARDS = 52;
	private List<Card> cardsInDeck;
	
	public Deck() {
		this.cardsInDeck = new ArrayList<Card>(MAX_CARDS);
		for (Card.Rank r : Card.Rank.values()) {
			for (Card.Suit s : Card.Suit.values()) {
				cardsInDeck.add(new Card(r, s));
			}
		}
	}
	
	public int getSize() {
		return cardsInDeck.size();
	}
	
	public void shuffle(int n) {
		Random prng = new Random();
		
		for (int i = 0; i < n; i++) {
			int a = prng.nextInt(MAX_CARDS-1);
			int b = prng.nextInt(MAX_CARDS-1);
			
			Card temp = this.cardsInDeck.get(a);
			this.cardsInDeck.set(a, this.cardsInDeck.get(b));
			this.cardsInDeck.set(b, temp);
		}
		
		//java.util.Collections.shuffle(this.cardsInDeck, new Random());
	}
	
	public List<Card> deal(int n, boolean remove) {
		n = n > this.cardsInDeck.size() ? this.cardsInDeck.size() : n;
		List<Card> cards = new ArrayList<Card>(n);
		
		for (int i = 0; i < n; i++) {
			if (remove)
				cards.add(this.cardsInDeck.remove(0));
			else 
				cards.add(this.cardsInDeck.get(i));
		}
		
		return cards;
	}
}
