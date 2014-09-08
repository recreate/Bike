package bike.table;

import java.util.List;
import java.util.ArrayList;

public class Card {
	public enum Rank {
		ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, 
		TEN, JACK, QUEEN, KING;
	}
	
	public enum Suit {
		DIAMOND, CLUB, HEART, SPADE;
	}
	
	private Card.Rank rank;
	private Card.Suit suit;
	
	public Card(Card.Rank rank, Card.Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
	public Card.Rank getRank() {
		return this.rank;
	}
	
	public Card.Suit getSuit() {
		return this.suit;
	}
	
	public String compress() {
		return String.format("%s,%s", this.suit.toString(), this.rank.toString());
	}
	
	public static Card uncompress(String f) {
		return null;
	}
	
	public String prettyPrint() {
		return String.format("%s of %sS", this.rank.toString(), this.suit.toString());
	}
	
	public String prettyPrint2() {
		return String.format("%s\t%s", this.suit.toString(), this.rank.toString());
	}
	
	public String prettyPrint3() {
		String suit = "";
		if (this.suit == Card.Suit.DIAMOND)
			suit = "\u2666";
		else if (this.suit == Card.Suit.CLUB)
			suit = "\u2663";
		else if (this.suit == Card.Suit.HEART)
			suit = "\u2665";
		else if (this.suit == Card.Suit.SPADE)
			suit = "\u2660";
		
		String rank = "";
		if (this.rank == Card.Rank.JACK) {
			rank = "J";
		} else if (this.rank == Card.Rank.QUEEN) {
			rank = "Q";
		} else if (this.rank == Card.Rank.KING) {
			rank = "K";
		} else if (this.rank == Card.Rank.ACE) {
			rank = "A";
		} else {
			rank = "" + (this.rank.ordinal() + 1);
		}
		
		return String.format("%s%s", rank, suit);
	}
	
	public static String compressList(List<Card> cards) {
		String result = "";
		for (Card c : cards)
			result += c.compress() + ";";
		
		return result.substring(0, result.length() - 1);
	}
	
	public static List<Card> uncompressList(String input) {
		List<Card> cards = new ArrayList<Card>();
		for (String s : input.split(";")) {
			int delimiter = s.indexOf(",");
			Card c = new Card(
				Card.Rank.valueOf(s.substring(0, delimiter)), 
				Card.Suit.valueOf(s.substring(delimiter+1))
			);
			cards.add(c);
		}
		
		return cards;
	}
	
	public static String prettyPrintCards(List<Card> cards) {
		String result = "";
		for (Card c : cards)
			result += c.prettyPrint() + "\n";
		return result;
	}
	
	public static String prettyPrintCards2(List<Card> cards) {
		String result = "";
		
		for (int i = 0; i < cards.size(); i++)
			result += String.format("[%02d] %s\n", i, cards.get(i).prettyPrint2());
		return result;
	}
	
	public static String prettyPrintCards3(List<Card> cards) {
		String result = "";
		
		for (int i = 0; i < cards.size(); i++)
			result += cards.get(i).prettyPrint3() + ", ";
		
		return result.substring(0, result.length() - 2);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Card))
			return false;
		
		Card other = (Card)obj;
		if (this.suit == other.getSuit() && this.rank == other.getRank())
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashcode = 13;
		hashcode = hashcode * 17 + this.rank.hashCode();
		hashcode = hashcode * 31 + this.suit.hashCode();
		return hashcode;
	}
}
