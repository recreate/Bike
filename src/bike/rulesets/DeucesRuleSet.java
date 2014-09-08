package bike.rulesets;

import java.util.List;

import bike.table.Card;

public class DeucesRuleSet extends RuleSet {
	public enum Combination {
		SINGLE, PAIR, THREE_OF_A_KIND, 
		STRAIGHT, FLUSH, FULL_HOUSE, QUAD, STRAIGHT_FLUSH;
	}
	
	@Override
	protected int getRankValue(Card c) {
		int value = -1;
		switch (c.getRank()) {
			case THREE:
				value = 0;
				break;
			case FOUR:
				value = 1;
				break;
			case FIVE:
				value = 2;
				break;
			case SIX:
				value = 3;
				break;
			case SEVEN:
				value = 4;
				break;
			case EIGHT:
				value = 5;
				break;
			case NINE:
				value = 6;
				break;
			case TEN:
				value = 7;
				break;
			case JACK:
				value = 8;
				break;
			case QUEEN:
				value = 9;
				break;
			case KING:
				value = 10;
				break;
			case ACE:
				value = 11;
				break;
			case TWO:
				value = 12;
				break;
			default:
				break;
		}
		
		return value;
	}
	
	@Override
	protected int getSuitValue(Card c) {
		int value = -1;
		
		switch (c.getSuit()) {
			case DIAMOND:
				value = 0;
				break;
			case CLUB:
				value = 1;
				break;
			case HEART:
				value = 2;
				break;
			case SPADE:
				value = 3;
				break;
			default:
				break;
		}
		
		return value;
	}
	
	public DeucesRuleSet.Combination getCardCombination(List<Card> combo) {
		if (combo.size() == 1) {
			return DeucesRuleSet.Combination.SINGLE;
		} else if (combo.size()  == 2 && 
				combo.get(0).getRank() == combo.get(1).getRank()) {
			return DeucesRuleSet.Combination.PAIR;
		} else if (combo.size()  == 3 && 
				combo.get(0).getRank() == combo.get(1).getRank() && 
				combo.get(1).getRank() == combo.get(2).getRank()) {
			return DeucesRuleSet.Combination.THREE_OF_A_KIND;
		} else if (combo.size()  == 5) {
			boolean isStraight = false;
			boolean isFlush = false;
			combo = this.sort(combo);
			
			if ((combo.get(0).getRank() == combo.get(1).getRank() &&
					combo.get(1).getRank() == combo.get(2).getRank() &&
					combo.get(2).getRank() == combo.get(3).getRank()) ||
					(combo.get(1).getRank() == combo.get(2).getRank() &&
					combo.get(2).getRank() == combo.get(3).getRank() &&
					combo.get(3).getRank() == combo.get(4).getRank())
					) {
				return DeucesRuleSet.Combination.QUAD;
			}
			
			if ((combo.get(0).getRank() == combo.get(1).getRank() && combo.get(1).getRank() == combo.get(2).getRank() && combo.get(3).getRank() == combo.get(4).getRank()) || 
					(combo.get(0).getRank() == combo.get(1).getRank() && combo.get(2).getRank() == combo.get(3).getRank() && combo.get(3).getRank() == combo.get(4).getRank()) ) {
				return DeucesRuleSet.Combination.FULL_HOUSE;
			}
			
			if ((combo.get(0).getRank().ordinal() + 1 == combo.get(1).getRank().ordinal() &&
					combo.get(1).getRank().ordinal() + 1 == combo.get(2).getRank().ordinal() &&
					combo.get(2).getRank().ordinal() + 1 == combo.get(3).getRank().ordinal() &&
					combo.get(3).getRank().ordinal() + 1 == combo.get(4).getRank().ordinal()) ||
					(
						(combo.get(0).getRank() == Card.Rank.THREE &&
						combo.get(1).getRank() == Card.Rank.FOUR &&
						combo.get(2).getRank() == Card.Rank.FIVE &&
						combo.get(3).getRank() == Card.Rank.ACE &&
						combo.get(4).getRank() == Card.Rank.TWO) ||
						(combo.get(0).getRank() == Card.Rank.THREE &&
						combo.get(1).getRank() == Card.Rank.FOUR &&
						combo.get(2).getRank() == Card.Rank.FIVE &&
						combo.get(3).getRank() == Card.Rank.SIX &&
						combo.get(4).getRank() == Card.Rank.ACE)
					)
					) {
				isStraight = true;
			}
			
			if (combo.get(0).getSuit() == combo.get(1).getSuit() &&
					combo.get(0).getSuit() == combo.get(2).getSuit() &&
					combo.get(0).getSuit() == combo.get(3).getSuit() &&
					combo.get(0).getSuit() == combo.get(4).getSuit()) {
				isFlush = true;
			}
			
			if (isStraight && isFlush) {
				return DeucesRuleSet.Combination.STRAIGHT_FLUSH;
			} else if (isStraight) {
				return DeucesRuleSet.Combination.STRAIGHT;
			} else if (isFlush) {
				return DeucesRuleSet.Combination.FLUSH;
			}
		}
		
		return null;
	}
	
	public int compareCombinations(List<Card> comboA, List<Card> comboB) {
		if (comboA.size() != comboB.size())
			throw new IllegalArgumentException("Combination size mismatch.");
		
		DeucesRuleSet.Combination typeA = this.getCardCombination(comboA);
		DeucesRuleSet.Combination typeB = this.getCardCombination(comboB);
		
		if (typeA == null || typeB == null)
			throw new IllegalArgumentException("Invalid combinations.");
		
		if (comboA.size() >= 1 && comboA.size() <= 3 && comboB.size() >= 1 && comboB.size() <= 3) {
			return this.compareCards(comboA.get(0), comboB.get(0));
		} else if (comboA.size() == 5 && comboB.size() == 5) {
			if (typeA != typeB) {
				return typeA.ordinal() - typeB.ordinal();
			}
			
			int result = 0;
			comboA = this.sort(comboA);
			comboB = this.sort(comboB);
			Card cardA = comboA.get(0);
			Card cardB = comboB.get(0);
			
			switch(typeA) {
				case STRAIGHT:
				case FLUSH:
				case STRAIGHT_FLUSH:
					result = this.compareCards(comboA.get(comboA.size()-1), comboB.get(comboB.size()-1));
					break;
				case FULL_HOUSE:
					if (comboA.get(2).getRank() == comboA.get(3).getRank() && comboA.get(3).getRank() == comboA.get(4).getRank()) {
						cardA = comboA.get(4);
					}
					if (comboB.get(2).getRank() == comboB.get(3).getRank() && comboB.get(3).getRank() == comboB.get(4).getRank()) {
						cardB = comboB.get(4);
					}
					result = this.compareCards(cardA, cardB);
					break;
				case QUAD:
					if (comboA.get(3).getRank() == comboA.get(4).getRank()) {
						cardA = comboA.get(4);
					}
					if (comboB.get(3).getRank() == comboB.get(4).getRank()) {
						cardB = comboB.get(4);
					}
					result = this.compareCards(cardA, cardB);
					break;
				default:
					break;
			}
			
			return result;
		}
		
		return 0;
	}
}
