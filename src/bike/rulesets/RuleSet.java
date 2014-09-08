package bike.rulesets;

import java.util.List;
import java.util.ArrayList;

import bike.table.Card;

public abstract class RuleSet {
	protected abstract int getRankValue(Card c);
	protected abstract int getSuitValue(Card c);
	
	protected int compareCards(Card a, Card b) {
		if (this.getRankValue(a) == this.getRankValue(b))
			return this.getSuitValue(a) - this.getSuitValue(b);
		return this.getRankValue(a) - this.getRankValue(b);
	}
	
	public List<Card> sort(List<Card> cards) {
		return mergeSort(cards);
	}
	
	private List<Card> mergeSort(List<Card> c) {
		if (c.size() <= 1)
			return c;
		
		List<Card> left = new ArrayList<Card>(c.size()/2);
		List<Card> right = new ArrayList<Card>(c.size()/2 + 1);
		for (int i = 0; i < c.size(); i++) {
			if (i < c.size()/2)
				left.add(c.get(i));
			else
				right.add(c.get(i));
		}
		
		left = mergeSort(left);
		right = mergeSort(right);
		
		List<Card> sortedCards = new ArrayList<Card>(c.size());
		int i = 0; int j = 0;
		while (true) {
			if (i == left.size()) {
				for (int k = j; k < right.size(); k++)
					sortedCards.add(right.get(k));
				break;
			} else if (j == right.size()) {
				for (int k = i; k < left.size(); k++)
					sortedCards.add(left.get(k));
				break;
			}
			
			if (this.compareCards(left.get(i), right.get(j)) <= 0) {
				sortedCards.add(left.get(i));
				i++;
			} else {
				sortedCards.add(right.get(j));
				j++;
			}
		}
		
		return sortedCards;
	}
}
