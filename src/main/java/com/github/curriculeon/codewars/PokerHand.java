package com.github.curriculeon.codewars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonhunter
 * @created 03/20/2020 - 2:10 PM
 * https://www.codewars.com/kata/5739174624fc28e188000465/train/java
 */
public class PokerHand {
    private final String handAsString;

    public enum Result {TIE, WIN, LOSS}

    public PokerHand(String hand) {
        this.handAsString = hand;
    }

    public String getHandAsString() {
        return handAsString;
    }

    public Result compareWith(PokerHand hand) {
        System.out.println(toString());
        String opponentHandAsString = hand.getHandAsString();
        String hands = handAsString + " " + opponentHandAsString;
        HandSplitter handSplitter = new HandSplitter(hands);
        String winnerAsString = Arrays.toString(handSplitter.getWinner())
                .toUpperCase()
                .replaceAll(",", "")
                .replaceAll("\\[", "")
                .replaceAll("]", "");
        System.out.println(new StringBuilder()
                .append("Opponent Hand = " + opponentHandAsString)
                .append("\nMy hand = " + handAsString)
                .append("\nhands = " + hands)
                .append("\nwinner = " + winnerAsString)
                .toString());
        if (!winnerAsString.equals(opponentHandAsString)) {
            return Result.LOSS;
        } else if (!winnerAsString.equals(handAsString)) {
            return Result.WIN;
        }
        return Result.TIE;
    }


    /**
     * Implementation of adapter on Hand object
     */
    static class HandSplitter {
        private final List<String> bothHands;
        private final Card[] hand1;
        private final Card[] hand2;

        public HandSplitter(String cards) {
            this.bothHands = Arrays.asList(cards.split(" "));
            this.hand1 = parseHand(bothHands.subList(0, bothHands.size() / 2));
            this.hand2 = parseHand(bothHands.subList(bothHands.size() / 2, bothHands.size()));
        }

        public Card[] getWinner() {
            if (Hand.evaluate(hand1) > Hand.evaluate(hand2)) {
                return hand1;
            }
            return hand2;
        }

        private Card[] parseHand(List<String> cardListAsString) {
            StringBuilder sb = new StringBuilder();
            cardListAsString.forEach(card -> {
                Character rank = card.charAt(0);
                Character suit = card.charAt(1);

                sb
                        .append(rank.toString().toUpperCase())
                        .append(suit.toString().toLowerCase())
                        .append(" ");
            });
            return Hand.fromString(sb.toString());
        }
    }


    // https://github.com/jmp/poker-hand-evaluator
    static class Hand {
        /**
         * Private constructor to disable instantiation of an abstract class.
         */
        private Hand() {

        }

        /**
         * Evaluates the given hand and returns its value as an integer.
         * Based on Kevin Suffecool's 5-card hand evaluator and with Paul Senzee's pre-computed hash.
         *
         * @param cards a hand of bothHands to evaluate
         * @return the value of the hand as an integer between 1 and 7462
         */
        public static int evaluate(Card[] cards) {
            // Only 5-card hands are supported
            if (cards == null || cards.length != 5) {
                throw new IllegalArgumentException("Exactly 5 bothHands are required.");
            }

            // Binary representations of each card
            final int c1 = cards[0].getValue();
            final int c2 = cards[1].getValue();
            final int c3 = cards[2].getValue();
            final int c4 = cards[3].getValue();
            final int c5 = cards[4].getValue();

            // No duplicate bothHands allowed
            if (hasDuplicates(new int[]{c1, c2, c3, c4, c5})) {
                throw new IllegalArgumentException("Illegal hand.");
            }

            // Calculate index in the flushes/unique table
            final int index = (c1 | c2 | c3 | c4 | c5) >> 16;

            // Flushes, including straight flushes
            if ((c1 & c2 & c3 & c4 & c5 & 0xF000) != 0) {
                return new Tables.Flushes().TABLE[index];
            }

            // Straight and high card hands
            final int value = new Tables.Unique().TABLE[index];
            if (value != 0) {
                return value;
            }

            // Remaining bothHands
            final int product = (c1 & 0xFF) * (c2 & 0xFF) * (c3 & 0xFF) * (c4 & 0xFF) * (c5 & 0xFF);
            return new Tables.Hash.Values().TABLE[hash(product)];
        }

        /**
         * Creates a new 5-card hand from the given string.
         *
         * @param string the string to create the hand from, such as "Kd 5s Jc Ah Qc"
         * @return a new hand as an array of bothHands
         * @see Card
         */
        public static Card[] fromString(String string) {
            final String[] parts = string.split(" ");
            final Card[] cards = new Card[parts.length];

            if (parts.length != 5)
                throw new IllegalArgumentException("Exactly 5 bothHands are required.");

            int index = 0;
            for (String part : parts)
                cards[index++] = Card.fromString(part);

            return cards;
        }

        /**
         * Converts the given hand into concatenation of their string representations
         *
         * @param cards a hand of bothHands
         * @return a concatenation of the string representations of the given bothHands
         */
        public static String toString(Card[] cards) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < cards.length; i++) {
                builder.append(cards[i]);
                if (i < cards.length - 1)
                    builder.append(" ");
            }

            return builder.toString();
        }

        /**
         * Checks if the given array of values has any duplicates.
         *
         * @param values the values to check
         * @return true if the values contain duplicates, false otherwise
         */
        private static boolean hasDuplicates(int[] values) {
            Arrays.sort(values);
            for (int i = 1; i < values.length; i++) {
                if (values[i] == values[i - 1])
                    return true;
            }
            return false;
        }

        private static int hash(int key) {
            key += 0xE91AAA35;
            key ^= key >>> 16;
            key += key << 8;
            key ^= key >>> 4;
            return ((key + (key << 2)) >>> 19) ^ new Tables.Hash.Adjust().TABLE[(key >>> 8) & 0x1FF];
        }
    }


    static class Card {
        private final int value;  // Format: xxxAKQJT 98765432 CDHSrrrr xxPPPPPP

        // Ranks
        public static final int DEUCE = 0;
        public static final int TREY = 1;
        public static final int FOUR = 2;
        public static final int FIVE = 3;
        public static final int SIX = 4;
        public static final int SEVEN = 5;
        public static final int EIGHT = 6;
        public static final int NINE = 7;
        public static final int TEN = 8;
        public static final int JACK = 9;
        public static final int QUEEN = 10;
        public static final int KING = 11;
        public static final int ACE = 12;

        // Suits
        public static final int CLUBS = 0x8000;
        public static final int DIAMONDS = 0x4000;
        public static final int HEARTS = 0x2000;
        public static final int SPADES = 0x1000;

        public static String getValidSuits() {
            Map<Integer, String> map = new HashMap<>();
            map.put(CLUBS, "CLUBS");
            map.put(DIAMONDS, "DIAMONDS");
            map.put(HEARTS, "HEARTS");
            map.put(SPADES, "SPADES");
            return map.toString();
        }

        // Rank symbols
        private static final String RANKS = "23456789TJQKA";
        private static final String SUITS = "shdc";

        /**
         * Creates a new card with the given rank and suit.
         *
         * @param rank the rank of the card, e.g. {@link Card#SIX}
         * @param suit the suit of the card, e.g. {@link Card#CLUBS}
         */
        public Card(int rank, int suit) {
            if (!isValidRank(rank)) {
                throw new IllegalArgumentException("Invalid rank.");
            }

            if (!isValidSuit(suit)) {
                String errorMessage = "[ %s ] is an invalid suit. List of valid suits:\n\t%s";
                throw new IllegalArgumentException(String.format(errorMessage, suit, getValidSuits()));
            }

            value = (1 << (rank + 16)) | suit | (rank << 8) | new Tables().PRIMES[rank];
        }

        /**
         * Create a new {@link Card} instance from the given string.
         * The string should be a two-character string where the first character
         * is the rank and the second character is the suit. For example, "Kc" means
         * the king of clubs, and "As" means the ace of spades.
         *
         * @param string Card to create as a string.
         * @return a new {@link Card} instance corresponding to the given string.
         */
        public static Card fromString(String string) {
            if (string == null || string.length() != 2) {
                throw new IllegalArgumentException("Card string must be non-null with length of exactly 2.");
            }

            final int rank = RANKS.indexOf(string.charAt(0));
            final int suit = SPADES << SUITS.indexOf(string.charAt(1));

            return new Card(rank, suit);
        }

        /**
         * Returns the rank of the card.
         *
         * @return rank of the card as an integer.
         * @see Card#ACE
         * @see Card#DEUCE
         * @see Card#TREY
         * @see Card#FOUR
         * @see Card#FIVE
         * @see Card#SIX
         * @see Card#SEVEN
         * @see Card#EIGHT
         * @see Card#NINE
         * @see Card#TEN
         * @see Card#JACK
         * @see Card#QUEEN
         * @see Card#KING
         */
        public int getRank() {
            return (value >> 8) & 0xF;
        }

        /**
         * Returns the suit of the card.
         *
         * @return Suit of the card as an integer.
         * @see Card#SPADES
         * @see Card#HEARTS
         * @see Card#DIAMONDS
         * @see Card#CLUBS
         */
        public int getSuit() {
            return value & 0xF000;
        }

        /**
         * Returns a string representation of the card.
         * For example, the king of spades is "Ks", and the jack of hearts is "Jh".
         *
         * @return a string representation of the card.
         */
        public String toString() {
            char rank = RANKS.charAt(getRank());
            char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
            return "" + rank + suit;
        }

        /**
         * Returns the value of the card as an integer.
         * The value is represented as the bits <code>xxxAKQJT 98765432 CDHSrrrr xxPPPPPP</code>,
         * where <code>x</code> means unused, <code>AKQJT 98765432</code> are bits turned on/off
         * depending on the rank of the card, <code>CDHS</code> are the bits corresponding to the
         * suit, and <code>PPPPPP</code> is the prime number of the card.
         *
         * @return the value of the card.
         */
        int getValue() {
            return value;
        }

        /**
         * Returns whether the given rank is valid or not.
         *
         * @param rank rank to check.
         * @return true if the rank is valid, false otherwise.
         */
        private static boolean isValidRank(int rank) {
            return rank >= DEUCE && rank <= ACE;
        }

        /**
         * Returns whether the given suit is valid or not.
         *
         * @param suit suit to check.
         * @return true if the suit is valid, false otherwise.
         */
        private static boolean isValidSuit(int suit) {
            return suit == CLUBS || suit == DIAMONDS || suit == HEARTS || suit == SPADES;
        }
    }


    static class Tables {
        private static final int CARDS_IN_DECK = 52;
        private static final int HAND_COMBINATIONS = 2598960;
        public final byte[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
        private final int[] DECK = {
                98306, 164099, 295429, 557831, 1082379, 2131213, 4228625, 8423187, 16812055, 33589533, 67144223, 134253349,
                268471337, 81922, 147715, 279045, 541447, 1065995, 2114829, 4212241, 8406803, 16795671, 33573149, 67127839,
                134236965, 268454953, 73730, 139523, 270853, 533255, 1057803, 2106637, 4204049, 8398611, 16787479, 33564957,
                67119647, 134228773, 268446761, 69634, 135427, 266757, 529159, 1053707, 2102541, 4199953, 8394515, 16783383,
                33560861, 67115551, 134224677, 268442665,
        };

        private final int[][] HANDS;

        {
            HANDS = new int[HAND_COMBINATIONS][];
            for (int i = 0; i < HANDS.length; i++) {
                HANDS[i] = new int[5];
            }

            // Generate every possible 5-card hand
            int totalHands = 0;
            int[] hand = new int[5];
            for (int a = 0; a < CARDS_IN_DECK - 4; a++) {
                hand[0] = DECK[a];
                for (int b = a + 1; b < CARDS_IN_DECK - 3; b++) {
                    hand[1] = DECK[b];
                    for (int c = b + 1; c < CARDS_IN_DECK - 2; c++) {
                        hand[2] = DECK[c];
                        for (int d = c + 1; d < CARDS_IN_DECK - 1; d++) {
                            hand[3] = DECK[d];
                            for (int e = d + 1; e < CARDS_IN_DECK; e++) {
                                hand[4] = DECK[e];
                                System.arraycopy(hand, 0, HANDS[totalHands++], 0, hand.length);
                            }
                        }
                    }
                }
            }

            assert totalHands == HANDS.length;
        }

        public static class Flushes {
            public final short[] TABLE = {
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 1599, 0, 0, 0, 0, 0, 0, 0, 1598, 0, 0, 0, 1597, 0, 1596,
                    8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1595, 0, 0, 0,
                    0, 0, 0, 0, 1594, 0, 0, 0, 1593, 0, 1592, 1591, 0, 0, 0, 0, 0, 0,
                    0, 0, 1590, 0, 0, 0, 1589, 0, 1588, 1587, 0, 0, 0, 0, 1586, 0,
                    1585, 1584, 0, 0, 1583, 1582, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 1581, 0, 0, 0, 0, 0, 0, 0, 1580, 0, 0, 0,
                    1579, 0, 1578, 1577, 0, 0, 0, 0, 0, 0, 0, 0, 1576, 0, 0, 0, 1575,
                    0, 1574, 1573, 0, 0, 0, 0, 1572, 0, 1571, 1570, 0, 0, 1569, 1568,
                    0, 1567, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1566, 0, 0, 0, 1565, 0,
                    1564, 1563, 0, 0, 0, 0, 1562, 0, 1561, 1560, 0, 0, 1559, 1558, 0,
                    1557, 0, 0, 0, 0, 0, 0, 1556, 0, 1555, 1554, 0, 0, 1553, 1552, 0,
                    1551, 0, 0, 0, 0, 1550, 1549, 0, 1548, 0, 0, 0, 6, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1547, 0, 0, 0,
                    0, 0, 0, 0, 1546, 0, 0, 0, 1545, 0, 1544, 1543, 0, 0, 0, 0, 0, 0,
                    0, 0, 1542, 0, 0, 0, 1541, 0, 1540, 1539, 0, 0, 0, 0, 1538, 0,
                    1537, 1536, 0, 0, 1535, 1534, 0, 1533, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 1532, 0, 0, 0, 1531, 0, 1530, 1529, 0, 0, 0, 0, 1528, 0, 1527,
                    1526, 0, 0, 1525, 1524, 0, 1523, 0, 0, 0, 0, 0, 0, 1522, 0, 1521,
                    1520, 0, 0, 1519, 1518, 0, 1517, 0, 0, 0, 0, 1516, 1515, 0, 1514,
                    0, 0, 0, 1513, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1512, 0,
                    0, 0, 1511, 0, 1510, 1509, 0, 0, 0, 0, 1508, 0, 1507, 1506, 0, 0,
                    1505, 1504, 0, 1503, 0, 0, 0, 0, 0, 0, 1502, 0, 1501, 1500, 0, 0,
                    1499, 1498, 0, 1497, 0, 0, 0, 0, 1496, 1495, 0, 1494, 0, 0, 0,
                    1493, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1492, 0, 1491, 1490, 0, 0,
                    1489, 1488, 0, 1487, 0, 0, 0, 0, 1486, 1485, 0, 1484, 0, 0, 0,
                    1483, 0, 0, 0, 0, 0, 0, 0, 0, 1482, 1481, 0, 1480, 0, 0, 0, 1479,
                    0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1478, 0, 0, 0, 0,
                    0, 0, 0, 1477, 0, 0, 0, 1476, 0, 1475, 1474, 0, 0, 0, 0, 0, 0, 0,
                    0, 1473, 0, 0, 0, 1472, 0, 1471, 1470, 0, 0, 0, 0, 1469, 0, 1468,
                    1467, 0, 0, 1466, 1465, 0, 1464, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1463, 0, 0, 0, 1462, 0, 1461, 1460, 0, 0, 0, 0, 1459, 0, 1458,
                    1457, 0, 0, 1456, 1455, 0, 1454, 0, 0, 0, 0, 0, 0, 1453, 0, 1452,
                    1451, 0, 0, 1450, 1449, 0, 1448, 0, 0, 0, 0, 1447, 1446, 0, 1445,
                    0, 0, 0, 1444, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1443, 0,
                    0, 0, 1442, 0, 1441, 1440, 0, 0, 0, 0, 1439, 0, 1438, 1437, 0, 0,
                    1436, 1435, 0, 1434, 0, 0, 0, 0, 0, 0, 1433, 0, 1432, 1431, 0, 0,
                    1430, 1429, 0, 1428, 0, 0, 0, 0, 1427, 1426, 0, 1425, 0, 0, 0,
                    1424, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1423, 0, 1422, 1421, 0, 0,
                    1420, 1419, 0, 1418, 0, 0, 0, 0, 1417, 1416, 0, 1415, 0, 0, 0,
                    1414, 0, 0, 0, 0, 0, 0, 0, 0, 1413, 1412, 0, 1411, 0, 0, 0, 1410,
                    0, 0, 0, 0, 0, 0, 0, 1409, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 1408, 0, 0, 0, 1407, 0, 1406, 1405, 0,
                    0, 0, 0, 1404, 0, 1403, 1402, 0, 0, 1401, 1400, 0, 1399, 0, 0, 0,
                    0, 0, 0, 1398, 0, 1397, 1396, 0, 0, 1395, 1394, 0, 1393, 0, 0, 0,
                    0, 1392, 1391, 0, 1390, 0, 0, 0, 1389, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 1388, 0, 1387, 1386, 0, 0, 1385, 1384, 0, 1383, 0, 0, 0, 0,
                    1382, 1381, 0, 1380, 0, 0, 0, 1379, 0, 0, 0, 0, 0, 0, 0, 0, 1378,
                    1377, 0, 1376, 0, 0, 0, 1375, 0, 0, 0, 0, 0, 0, 0, 1374, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1373, 0, 1372, 1371,
                    0, 0, 1370, 1369, 0, 1368, 0, 0, 0, 0, 1367, 1366, 0, 1365, 0, 0,
                    0, 1364, 0, 0, 0, 0, 0, 0, 0, 0, 1363, 1362, 0, 1361, 0, 0, 0,
                    1360, 0, 0, 0, 0, 0, 0, 0, 1359, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 1358, 1357, 0, 1356, 0, 0, 0, 1355, 0, 0, 0, 0, 0,
                    0, 0, 1354, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1353, 0, 0, 0, 0, 0, 0, 0, 1352, 0, 0, 0, 1351, 0, 1350, 1349, 0,
                    0, 0, 0, 0, 0, 0, 0, 1348, 0, 0, 0, 1347, 0, 1346, 1345, 0, 0, 0,
                    0, 1344, 0, 1343, 1342, 0, 0, 1341, 1340, 0, 1339, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 1338, 0, 0, 0, 1337, 0, 1336, 1335, 0, 0, 0, 0,
                    1334, 0, 1333, 1332, 0, 0, 1331, 1330, 0, 1329, 0, 0, 0, 0, 0, 0,
                    1328, 0, 1327, 1326, 0, 0, 1325, 1324, 0, 1323, 0, 0, 0, 0, 1322,
                    1321, 0, 1320, 0, 0, 0, 1319, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1318, 0, 0, 0, 1317, 0, 1316, 1315, 0, 0, 0, 0, 1314, 0,
                    1313, 1312, 0, 0, 1311, 1310, 0, 1309, 0, 0, 0, 0, 0, 0, 1308, 0,
                    1307, 1306, 0, 0, 1305, 1304, 0, 1303, 0, 0, 0, 0, 1302, 1301, 0,
                    1300, 0, 0, 0, 1299, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1298, 0, 1297,
                    1296, 0, 0, 1295, 1294, 0, 1293, 0, 0, 0, 0, 1292, 1291, 0, 1290,
                    0, 0, 0, 1289, 0, 0, 0, 0, 0, 0, 0, 0, 1288, 1287, 0, 1286, 0, 0,
                    0, 1285, 0, 0, 0, 0, 0, 0, 0, 1284, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1283, 0, 0, 0, 1282, 0, 1281,
                    1280, 0, 0, 0, 0, 1279, 0, 1278, 1277, 0, 0, 1276, 1275, 0, 1274,
                    0, 0, 0, 0, 0, 0, 1273, 0, 1272, 1271, 0, 0, 1270, 1269, 0, 1268,
                    0, 0, 0, 0, 1267, 1266, 0, 1265, 0, 0, 0, 1264, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 1263, 0, 1262, 1261, 0, 0, 1260, 1259, 0, 1258, 0, 0,
                    0, 0, 1257, 1256, 0, 1255, 0, 0, 0, 1254, 0, 0, 0, 0, 0, 0, 0, 0,
                    1253, 1252, 0, 1251, 0, 0, 0, 1250, 0, 0, 0, 0, 0, 0, 0, 1249, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1248, 0, 1247,
                    1246, 0, 0, 1245, 1244, 0, 1243, 0, 0, 0, 0, 1242, 1241, 0, 1240,
                    0, 0, 0, 1239, 0, 0, 0, 0, 0, 0, 0, 0, 1238, 1237, 0, 1236, 0, 0,
                    0, 1235, 0, 0, 0, 0, 0, 0, 0, 1234, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 1233, 1232, 0, 1231, 0, 0, 0, 1230, 0, 0, 0, 0,
                    0, 0, 0, 1229, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1228,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1227, 0, 0, 0,
                    1226, 0, 1225, 1224, 0, 0, 0, 0, 1223, 0, 1222, 1221, 0, 0, 1220,
                    1219, 0, 1218, 0, 0, 0, 0, 0, 0, 1217, 0, 1216, 1215, 0, 0, 1214,
                    1213, 0, 1212, 0, 0, 0, 0, 1211, 1210, 0, 1209, 0, 0, 0, 1208, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 1207, 0, 1206, 1205, 0, 0, 1204, 1203,
                    0, 1202, 0, 0, 0, 0, 1201, 1200, 0, 1199, 0, 0, 0, 1198, 0, 0, 0,
                    0, 0, 0, 0, 0, 1197, 1196, 0, 1195, 0, 0, 0, 1194, 0, 0, 0, 0, 0,
                    0, 0, 1193, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1192, 0, 1191, 1190, 0, 0, 1189, 1188, 0, 1187, 0, 0, 0, 0, 1186,
                    1185, 0, 1184, 0, 0, 0, 1183, 0, 0, 0, 0, 0, 0, 0, 0, 1182, 1181,
                    0, 1180, 0, 0, 0, 1179, 0, 0, 0, 0, 0, 0, 0, 1178, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1177, 1176, 0, 1175, 0, 0, 0,
                    1174, 0, 0, 0, 0, 0, 0, 0, 1173, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 1172, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1171, 0,
                    1170, 1169, 0, 0, 1168, 1167, 0, 1166, 0, 0, 0, 0, 1165, 1164, 0,
                    1163, 0, 0, 0, 1162, 0, 0, 0, 0, 0, 0, 0, 0, 1161, 1160, 0, 1159,
                    0, 0, 0, 1158, 0, 0, 0, 0, 0, 0, 0, 1157, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 1156, 1155, 0, 1154, 0, 0, 0, 1153, 0, 0,
                    0, 0, 0, 0, 0, 1152, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1151, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1150, 1149, 0, 1148, 0, 0, 0,
                    1147, 0, 0, 0, 0, 0, 0, 0, 1146, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 1145, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 1144, 0, 0, 0, 0, 0, 0, 0, 1143, 0, 0, 0, 1142,
                    0, 1141, 1140, 0, 0, 0, 0, 0, 0, 0, 0, 1139, 0, 0, 0, 1138, 0,
                    1137, 1136, 0, 0, 0, 0, 1135, 0, 1134, 1133, 0, 0, 1132, 1131, 0,
                    1130, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1129, 0, 0, 0, 1128, 0, 1127,
                    1126, 0, 0, 0, 0, 1125, 0, 1124, 1123, 0, 0, 1122, 1121, 0, 1120,
                    0, 0, 0, 0, 0, 0, 1119, 0, 1118, 1117, 0, 0, 1116, 1115, 0, 1114,
                    0, 0, 0, 0, 1113, 1112, 0, 1111, 0, 0, 0, 1110, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 1109, 0, 0, 0, 1108, 0, 1107, 1106, 0, 0,
                    0, 0, 1105, 0, 1104, 1103, 0, 0, 1102, 1101, 0, 1100, 0, 0, 0, 0,
                    0, 0, 1099, 0, 1098, 1097, 0, 0, 1096, 1095, 0, 1094, 0, 0, 0, 0,
                    1093, 1092, 0, 1091, 0, 0, 0, 1090, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1089, 0, 1088, 1087, 0, 0, 1086, 1085, 0, 1084, 0, 0, 0, 0, 1083,
                    1082, 0, 1081, 0, 0, 0, 1080, 0, 0, 0, 0, 0, 0, 0, 0, 1079, 1078,
                    0, 1077, 0, 0, 0, 1076, 0, 0, 0, 0, 0, 0, 0, 1075, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1074, 0, 0, 0,
                    1073, 0, 1072, 1071, 0, 0, 0, 0, 1070, 0, 1069, 1068, 0, 0, 1067,
                    1066, 0, 1065, 0, 0, 0, 0, 0, 0, 1064, 0, 1063, 1062, 0, 0, 1061,
                    1060, 0, 1059, 0, 0, 0, 0, 1058, 1057, 0, 1056, 0, 0, 0, 1055, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 1054, 0, 1053, 1052, 0, 0, 1051, 1050,
                    0, 1049, 0, 0, 0, 0, 1048, 1047, 0, 1046, 0, 0, 0, 1045, 0, 0, 0,
                    0, 0, 0, 0, 0, 1044, 1043, 0, 1042, 0, 0, 0, 1041, 0, 0, 0, 0, 0,
                    0, 0, 1040, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1039, 0, 1038, 1037, 0, 0, 1036, 1035, 0, 1034, 0, 0, 0, 0, 1033,
                    1032, 0, 1031, 0, 0, 0, 1030, 0, 0, 0, 0, 0, 0, 0, 0, 1029, 1028,
                    0, 1027, 0, 0, 0, 1026, 0, 0, 0, 0, 0, 0, 0, 1025, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1024, 1023, 0, 1022, 0, 0, 0,
                    1021, 0, 0, 0, 0, 0, 0, 0, 1020, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 1019, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    1018, 0, 0, 0, 1017, 0, 1016, 1015, 0, 0, 0, 0, 1014, 0, 1013,
                    1012, 0, 0, 1011, 1010, 0, 1009, 0, 0, 0, 0, 0, 0, 1008, 0, 1007,
                    1006, 0, 0, 1005, 1004, 0, 1003, 0, 0, 0, 0, 1002, 1001, 0, 1000,
                    0, 0, 0, 999, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 998, 0, 997, 996, 0,
                    0, 995, 994, 0, 993, 0, 0, 0, 0, 992, 991, 0, 990, 0, 0, 0, 989,
                    0, 0, 0, 0, 0, 0, 0, 0, 988, 987, 0, 986, 0, 0, 0, 985, 0, 0, 0,
                    0, 0, 0, 0, 984, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 983, 0, 982, 981, 0, 0, 980, 979, 0, 978, 0, 0, 0, 0, 977,
                    976, 0, 975, 0, 0, 0, 974, 0, 0, 0, 0, 0, 0, 0, 0, 973, 972, 0,
                    971, 0, 0, 0, 970, 0, 0, 0, 0, 0, 0, 0, 969, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 968, 967, 0, 966, 0, 0, 0, 965, 0, 0,
                    0, 0, 0, 0, 0, 964, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    963, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 962, 0, 961, 960, 0, 0,
                    959, 958, 0, 957, 0, 0, 0, 0, 956, 955, 0, 954, 0, 0, 0, 953, 0,
                    0, 0, 0, 0, 0, 0, 0, 952, 951, 0, 950, 0, 0, 0, 949, 0, 0, 0, 0,
                    0, 0, 0, 948, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    947, 946, 0, 945, 0, 0, 0, 944, 0, 0, 0, 0, 0, 0, 0, 943, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 942, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 941, 940, 0, 939, 0, 0, 0, 938, 0, 0, 0, 0, 0, 0, 0,
                    937, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 936, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 935, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 934, 0, 0, 0, 933, 0, 932,
                    931, 0, 0, 0, 0, 930, 0, 929, 928, 0, 0, 927, 926, 0, 925, 0, 0,
                    0, 0, 0, 0, 924, 0, 923, 922, 0, 0, 921, 920, 0, 919, 0, 0, 0, 0,
                    918, 917, 0, 916, 0, 0, 0, 915, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    914, 0, 913, 912, 0, 0, 911, 910, 0, 909, 0, 0, 0, 0, 908, 907,
                    0, 906, 0, 0, 0, 905, 0, 0, 0, 0, 0, 0, 0, 0, 904, 903, 0, 902,
                    0, 0, 0, 901, 0, 0, 0, 0, 0, 0, 0, 900, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 899, 0, 898, 897, 0, 0, 896, 895,
                    0, 894, 0, 0, 0, 0, 893, 892, 0, 891, 0, 0, 0, 890, 0, 0, 0, 0,
                    0, 0, 0, 0, 889, 888, 0, 887, 0, 0, 0, 886, 0, 0, 0, 0, 0, 0, 0,
                    885, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 884, 883, 0,
                    882, 0, 0, 0, 881, 0, 0, 0, 0, 0, 0, 0, 880, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 879, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    878, 0, 877, 876, 0, 0, 875, 874, 0, 873, 0, 0, 0, 0, 872, 871,
                    0, 870, 0, 0, 0, 869, 0, 0, 0, 0, 0, 0, 0, 0, 868, 867, 0, 866,
                    0, 0, 0, 865, 0, 0, 0, 0, 0, 0, 0, 864, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 863, 862, 0, 861, 0, 0, 0, 860, 0, 0, 0,
                    0, 0, 0, 0, 859, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    858, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 857, 856, 0, 855, 0, 0, 0,
                    854, 0, 0, 0, 0, 0, 0, 0, 853, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 852, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 851, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 850, 0, 849,
                    848, 0, 0, 847, 846, 0, 845, 0, 0, 0, 0, 844, 843, 0, 842, 0, 0,
                    0, 841, 0, 0, 0, 0, 0, 0, 0, 0, 840, 839, 0, 838, 0, 0, 0, 837,
                    0, 0, 0, 0, 0, 0, 0, 836, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 835, 834, 0, 833, 0, 0, 0, 832, 0, 0, 0, 0, 0, 0, 0,
                    831, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 830, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 829, 828, 0, 827, 0, 0, 0, 826, 0, 0, 0, 0,
                    0, 0, 0, 825, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 824,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 823, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 822, 821, 0, 820, 0, 0, 0, 819, 0, 0,
                    0, 0, 0, 0, 0, 818, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    817, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 816, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    10, 0, 0, 0, 0, 0, 0, 0, 815, 0, 0, 0, 814, 0, 813, 812, 0, 0, 0,
                    0, 0, 0, 0, 0, 811, 0, 0, 0, 810, 0, 809, 808, 0, 0, 0, 0, 807,
                    0, 806, 805, 0, 0, 804, 803, 0, 802, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 801, 0, 0, 0, 800, 0, 799, 798, 0, 0, 0, 0, 797, 0, 796, 795,
                    0, 0, 794, 793, 0, 792, 0, 0, 0, 0, 0, 0, 791, 0, 790, 789, 0, 0,
                    788, 787, 0, 786, 0, 0, 0, 0, 785, 784, 0, 783, 0, 0, 0, 782, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 781, 0, 0, 0, 780, 0, 779,
                    778, 0, 0, 0, 0, 777, 0, 776, 775, 0, 0, 774, 773, 0, 772, 0, 0,
                    0, 0, 0, 0, 771, 0, 770, 769, 0, 0, 768, 767, 0, 766, 0, 0, 0, 0,
                    765, 764, 0, 763, 0, 0, 0, 762, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    761, 0, 760, 759, 0, 0, 758, 757, 0, 756, 0, 0, 0, 0, 755, 754,
                    0, 753, 0, 0, 0, 752, 0, 0, 0, 0, 0, 0, 0, 0, 751, 750, 0, 749,
                    0, 0, 0, 748, 0, 0, 0, 0, 0, 0, 0, 747, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 746, 0, 0, 0, 745, 0,
                    744, 743, 0, 0, 0, 0, 742, 0, 741, 740, 0, 0, 739, 738, 0, 737,
                    0, 0, 0, 0, 0, 0, 736, 0, 735, 734, 0, 0, 733, 732, 0, 731, 0, 0,
                    0, 0, 730, 729, 0, 728, 0, 0, 0, 727, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 726, 0, 725, 724, 0, 0, 723, 722, 0, 721, 0, 0, 0, 0, 720,
                    719, 0, 718, 0, 0, 0, 717, 0, 0, 0, 0, 0, 0, 0, 0, 716, 715, 0,
                    714, 0, 0, 0, 713, 0, 0, 0, 0, 0, 0, 0, 712, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 711, 0, 710, 709, 0, 0, 708,
                    707, 0, 706, 0, 0, 0, 0, 705, 704, 0, 703, 0, 0, 0, 702, 0, 0, 0,
                    0, 0, 0, 0, 0, 701, 700, 0, 699, 0, 0, 0, 698, 0, 0, 0, 0, 0, 0,
                    0, 697, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 696, 695,
                    0, 694, 0, 0, 0, 693, 0, 0, 0, 0, 0, 0, 0, 692, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 691, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 690, 0, 0, 0, 689, 0, 688, 687, 0, 0, 0, 0, 686,
                    0, 685, 684, 0, 0, 683, 682, 0, 681, 0, 0, 0, 0, 0, 0, 680, 0,
                    679, 678, 0, 0, 677, 676, 0, 675, 0, 0, 0, 0, 674, 673, 0, 672,
                    0, 0, 0, 671, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 670, 0, 669, 668, 0,
                    0, 667, 666, 0, 665, 0, 0, 0, 0, 664, 663, 0, 662, 0, 0, 0, 661,
                    0, 0, 0, 0, 0, 0, 0, 0, 660, 659, 0, 658, 0, 0, 0, 657, 0, 0, 0,
                    0, 0, 0, 0, 656, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 655, 0, 654, 653, 0, 0, 652, 651, 0, 650, 0, 0, 0, 0, 649,
                    648, 0, 647, 0, 0, 0, 646, 0, 0, 0, 0, 0, 0, 0, 0, 645, 644, 0,
                    643, 0, 0, 0, 642, 0, 0, 0, 0, 0, 0, 0, 641, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 640, 639, 0, 638, 0, 0, 0, 637, 0, 0,
                    0, 0, 0, 0, 0, 636, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    635, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 634, 0, 633, 632, 0, 0,
                    631, 630, 0, 629, 0, 0, 0, 0, 628, 627, 0, 626, 0, 0, 0, 625, 0,
                    0, 0, 0, 0, 0, 0, 0, 624, 623, 0, 622, 0, 0, 0, 621, 0, 0, 0, 0,
                    0, 0, 0, 620, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    619, 618, 0, 617, 0, 0, 0, 616, 0, 0, 0, 0, 0, 0, 0, 615, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 614, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 613, 612, 0, 611, 0, 0, 0, 610, 0, 0, 0, 0, 0, 0, 0,
                    609, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 608, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 607, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 606, 0, 0, 0, 605, 0, 604,
                    603, 0, 0, 0, 0, 602, 0, 601, 600, 0, 0, 599, 598, 0, 597, 0, 0,
                    0, 0, 0, 0, 596, 0, 595, 594, 0, 0, 593, 592, 0, 591, 0, 0, 0, 0,
                    590, 589, 0, 588, 0, 0, 0, 587, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    586, 0, 585, 584, 0, 0, 583, 582, 0, 581, 0, 0, 0, 0, 580, 579,
                    0, 578, 0, 0, 0, 577, 0, 0, 0, 0, 0, 0, 0, 0, 576, 575, 0, 574,
                    0, 0, 0, 573, 0, 0, 0, 0, 0, 0, 0, 572, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 571, 0, 570, 569, 0, 0, 568, 567,
                    0, 566, 0, 0, 0, 0, 565, 564, 0, 563, 0, 0, 0, 562, 0, 0, 0, 0,
                    0, 0, 0, 0, 561, 560, 0, 559, 0, 0, 0, 558, 0, 0, 0, 0, 0, 0, 0,
                    557, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 556, 555, 0,
                    554, 0, 0, 0, 553, 0, 0, 0, 0, 0, 0, 0, 552, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 551, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    550, 0, 549, 548, 0, 0, 547, 546, 0, 545, 0, 0, 0, 0, 544, 543,
                    0, 542, 0, 0, 0, 541, 0, 0, 0, 0, 0, 0, 0, 0, 540, 539, 0, 538,
                    0, 0, 0, 537, 0, 0, 0, 0, 0, 0, 0, 536, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 535, 534, 0, 533, 0, 0, 0, 532, 0, 0, 0,
                    0, 0, 0, 0, 531, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    530, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 529, 528, 0, 527, 0, 0, 0,
                    526, 0, 0, 0, 0, 0, 0, 0, 525, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 524, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 523, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 522, 0, 521,
                    520, 0, 0, 519, 518, 0, 517, 0, 0, 0, 0, 516, 515, 0, 514, 0, 0,
                    0, 513, 0, 0, 0, 0, 0, 0, 0, 0, 512, 511, 0, 510, 0, 0, 0, 509,
                    0, 0, 0, 0, 0, 0, 0, 508, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 507, 506, 0, 505, 0, 0, 0, 504, 0, 0, 0, 0, 0, 0, 0,
                    503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 502, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 501, 500, 0, 499, 0, 0, 0, 498, 0, 0, 0, 0,
                    0, 0, 0, 497, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 496,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 495, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 494, 493, 0, 492, 0, 0, 0, 491, 0, 0,
                    0, 0, 0, 0, 0, 490, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    489, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 488, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 487, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 486, 0, 0, 0, 485, 0,
                    484, 483, 0, 0, 0, 0, 482, 0, 481, 480, 0, 0, 479, 478, 0, 477,
                    0, 0, 0, 0, 0, 0, 476, 0, 475, 474, 0, 0, 473, 472, 0, 471, 0, 0,
                    0, 0, 470, 469, 0, 468, 0, 0, 0, 467, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 466, 0, 465, 464, 0, 0, 463, 462, 0, 461, 0, 0, 0, 0, 460,
                    459, 0, 458, 0, 0, 0, 457, 0, 0, 0, 0, 0, 0, 0, 0, 456, 455, 0,
                    454, 0, 0, 0, 453, 0, 0, 0, 0, 0, 0, 0, 452, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 451, 0, 450, 449, 0, 0, 448,
                    447, 0, 446, 0, 0, 0, 0, 445, 444, 0, 443, 0, 0, 0, 442, 0, 0, 0,
                    0, 0, 0, 0, 0, 441, 440, 0, 439, 0, 0, 0, 438, 0, 0, 0, 0, 0, 0,
                    0, 437, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 436, 435,
                    0, 434, 0, 0, 0, 433, 0, 0, 0, 0, 0, 0, 0, 432, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 431, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 430, 0, 429, 428, 0, 0, 427, 426, 0, 425, 0, 0, 0, 0, 424,
                    423, 0, 422, 0, 0, 0, 421, 0, 0, 0, 0, 0, 0, 0, 0, 420, 419, 0,
                    418, 0, 0, 0, 417, 0, 0, 0, 0, 0, 0, 0, 416, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 415, 414, 0, 413, 0, 0, 0, 412, 0, 0,
                    0, 0, 0, 0, 0, 411, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    410, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 409, 408, 0, 407, 0, 0, 0,
                    406, 0, 0, 0, 0, 0, 0, 0, 405, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 404, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 403, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 402, 0, 401,
                    400, 0, 0, 399, 398, 0, 397, 0, 0, 0, 0, 396, 395, 0, 394, 0, 0,
                    0, 393, 0, 0, 0, 0, 0, 0, 0, 0, 392, 391, 0, 390, 0, 0, 0, 389,
                    0, 0, 0, 0, 0, 0, 0, 388, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 387, 386, 0, 385, 0, 0, 0, 384, 0, 0, 0, 0, 0, 0, 0,
                    383, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 382, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 381, 380, 0, 379, 0, 0, 0, 378, 0, 0, 0, 0,
                    0, 0, 0, 377, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 376,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 375, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 374, 373, 0, 372, 0, 0, 0, 371, 0, 0,
                    0, 0, 0, 0, 0, 370, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    369, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 368, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 367, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 366, 0, 365, 364, 0, 0, 363, 362,
                    0, 361, 0, 0, 0, 0, 360, 359, 0, 358, 0, 0, 0, 357, 0, 0, 0, 0,
                    0, 0, 0, 0, 356, 355, 0, 354, 0, 0, 0, 353, 0, 0, 0, 0, 0, 0, 0,
                    352, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 351, 350, 0,
                    349, 0, 0, 0, 348, 0, 0, 0, 0, 0, 0, 0, 347, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 346, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 345,
                    344, 0, 343, 0, 0, 0, 342, 0, 0, 0, 0, 0, 0, 0, 341, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 340, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    339, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    338, 337, 0, 336, 0, 0, 0, 335, 0, 0, 0, 0, 0, 0, 0, 334, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 333, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 332, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 331, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    330, 329, 0, 328, 0, 0, 0, 327, 0, 0, 0, 0, 0, 0, 0, 326, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 325, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 324, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 323, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            };
        }

        public static class Unique {
            public final short[] TABLE = {
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 1608, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 7462, 0, 0, 0, 0, 0, 0, 0, 7461, 0, 0, 0, 7460, 0,
                    7459, 1607, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7458,
                    0, 0, 0, 0, 0, 0, 0, 7457, 0, 0, 0, 7456, 0, 7455, 7454, 0, 0, 0,
                    0, 0, 0, 0, 0, 7453, 0, 0, 0, 7452, 0, 7451, 7450, 0, 0, 0, 0,
                    7449, 0, 7448, 7447, 0, 0, 7446, 7445, 0, 1606, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7444, 0, 0, 0, 0, 0, 0, 0,
                    7443, 0, 0, 0, 7442, 0, 7441, 7440, 0, 0, 0, 0, 0, 0, 0, 0, 7439,
                    0, 0, 0, 7438, 0, 7437, 7436, 0, 0, 0, 0, 7435, 0, 7434, 7433, 0,
                    0, 7432, 7431, 0, 7430, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7429, 0, 0,
                    0, 7428, 0, 7427, 7426, 0, 0, 0, 0, 7425, 0, 7424, 7423, 0, 0,
                    7422, 7421, 0, 7420, 0, 0, 0, 0, 0, 0, 7419, 0, 7418, 7417, 0, 0,
                    7416, 7415, 0, 7414, 0, 0, 0, 0, 7413, 7412, 0, 7411, 0, 0, 0,
                    1605, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 7410, 0, 0, 0, 0, 0, 0, 0, 7409, 0, 0, 0, 7408, 0, 7407,
                    7406, 0, 0, 0, 0, 0, 0, 0, 0, 7405, 0, 0, 0, 7404, 0, 7403, 7402,
                    0, 0, 0, 0, 7401, 0, 7400, 7399, 0, 0, 7398, 7397, 0, 7396, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 7395, 0, 0, 0, 7394, 0, 7393, 7392, 0, 0,
                    0, 0, 7391, 0, 7390, 7389, 0, 0, 7388, 7387, 0, 7386, 0, 0, 0, 0,
                    0, 0, 7385, 0, 7384, 7383, 0, 0, 7382, 7381, 0, 7380, 0, 0, 0, 0,
                    7379, 7378, 0, 7377, 0, 0, 0, 7376, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 7375, 0, 0, 0, 7374, 0, 7373, 7372, 0, 0, 0, 0, 7371,
                    0, 7370, 7369, 0, 0, 7368, 7367, 0, 7366, 0, 0, 0, 0, 0, 0, 7365,
                    0, 7364, 7363, 0, 0, 7362, 7361, 0, 7360, 0, 0, 0, 0, 7359, 7358,
                    0, 7357, 0, 0, 0, 7356, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7355, 0,
                    7354, 7353, 0, 0, 7352, 7351, 0, 7350, 0, 0, 0, 0, 7349, 7348, 0,
                    7347, 0, 0, 0, 7346, 0, 0, 0, 0, 0, 0, 0, 0, 7345, 7344, 0, 7343,
                    0, 0, 0, 7342, 0, 0, 0, 0, 0, 0, 0, 1604, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    7341, 0, 0, 0, 0, 0, 0, 0, 7340, 0, 0, 0, 7339, 0, 7338, 7337, 0,
                    0, 0, 0, 0, 0, 0, 0, 7336, 0, 0, 0, 7335, 0, 7334, 7333, 0, 0, 0,
                    0, 7332, 0, 7331, 7330, 0, 0, 7329, 7328, 0, 7327, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 7326, 0, 0, 0, 7325, 0, 7324, 7323, 0, 0, 0, 0,
                    7322, 0, 7321, 7320, 0, 0, 7319, 7318, 0, 7317, 0, 0, 0, 0, 0, 0,
                    7316, 0, 7315, 7314, 0, 0, 7313, 7312, 0, 7311, 0, 0, 0, 0, 7310,
                    7309, 0, 7308, 0, 0, 0, 7307, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 7306, 0, 0, 0, 7305, 0, 7304, 7303, 0, 0, 0, 0, 7302, 0,
                    7301, 7300, 0, 0, 7299, 7298, 0, 7297, 0, 0, 0, 0, 0, 0, 7296, 0,
                    7295, 7294, 0, 0, 7293, 7292, 0, 7291, 0, 0, 0, 0, 7290, 7289, 0,
                    7288, 0, 0, 0, 7287, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7286, 0, 7285,
                    7284, 0, 0, 7283, 7282, 0, 7281, 0, 0, 0, 0, 7280, 7279, 0, 7278,
                    0, 0, 0, 7277, 0, 0, 0, 0, 0, 0, 0, 0, 7276, 7275, 0, 7274, 0, 0,
                    0, 7273, 0, 0, 0, 0, 0, 0, 0, 7272, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7271, 0, 0, 0, 7270, 0, 7269,
                    7268, 0, 0, 0, 0, 7267, 0, 7266, 7265, 0, 0, 7264, 7263, 0, 7262,
                    0, 0, 0, 0, 0, 0, 7261, 0, 7260, 7259, 0, 0, 7258, 7257, 0, 7256,
                    0, 0, 0, 0, 7255, 7254, 0, 7253, 0, 0, 0, 7252, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 7251, 0, 7250, 7249, 0, 0, 7248, 7247, 0, 7246, 0, 0,
                    0, 0, 7245, 7244, 0, 7243, 0, 0, 0, 7242, 0, 0, 0, 0, 0, 0, 0, 0,
                    7241, 7240, 0, 7239, 0, 0, 0, 7238, 0, 0, 0, 0, 0, 0, 0, 7237, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7236, 0, 7235,
                    7234, 0, 0, 7233, 7232, 0, 7231, 0, 0, 0, 0, 7230, 7229, 0, 7228,
                    0, 0, 0, 7227, 0, 0, 0, 0, 0, 0, 0, 0, 7226, 7225, 0, 7224, 0, 0,
                    0, 7223, 0, 0, 0, 0, 0, 0, 0, 7222, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 7221, 7220, 0, 7219, 0, 0, 0, 7218, 0, 0, 0, 0,
                    0, 0, 0, 7217, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1603,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 7216, 0, 0, 0, 0, 0, 0, 0, 7215, 0, 0, 0, 7214, 0, 7213,
                    7212, 0, 0, 0, 0, 0, 0, 0, 0, 7211, 0, 0, 0, 7210, 0, 7209, 7208,
                    0, 0, 0, 0, 7207, 0, 7206, 7205, 0, 0, 7204, 7203, 0, 7202, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 7201, 0, 0, 0, 7200, 0, 7199, 7198, 0, 0,
                    0, 0, 7197, 0, 7196, 7195, 0, 0, 7194, 7193, 0, 7192, 0, 0, 0, 0,
                    0, 0, 7191, 0, 7190, 7189, 0, 0, 7188, 7187, 0, 7186, 0, 0, 0, 0,
                    7185, 7184, 0, 7183, 0, 0, 0, 7182, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 7181, 0, 0, 0, 7180, 0, 7179, 7178, 0, 0, 0, 0, 7177,
                    0, 7176, 7175, 0, 0, 7174, 7173, 0, 7172, 0, 0, 0, 0, 0, 0, 7171,
                    0, 7170, 7169, 0, 0, 7168, 7167, 0, 7166, 0, 0, 0, 0, 7165, 7164,
                    0, 7163, 0, 0, 0, 7162, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7161, 0,
                    7160, 7159, 0, 0, 7158, 7157, 0, 7156, 0, 0, 0, 0, 7155, 7154, 0,
                    7153, 0, 0, 0, 7152, 0, 0, 0, 0, 0, 0, 0, 0, 7151, 7150, 0, 7149,
                    0, 0, 0, 7148, 0, 0, 0, 0, 0, 0, 0, 7147, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7146, 0, 0, 0, 7145, 0,
                    7144, 7143, 0, 0, 0, 0, 7142, 0, 7141, 7140, 0, 0, 7139, 7138, 0,
                    7137, 0, 0, 0, 0, 0, 0, 7136, 0, 7135, 7134, 0, 0, 7133, 7132, 0,
                    7131, 0, 0, 0, 0, 7130, 7129, 0, 7128, 0, 0, 0, 7127, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 7126, 0, 7125, 7124, 0, 0, 7123, 7122, 0, 7121,
                    0, 0, 0, 0, 7120, 7119, 0, 7118, 0, 0, 0, 7117, 0, 0, 0, 0, 0, 0,
                    0, 0, 7116, 7115, 0, 7114, 0, 0, 0, 7113, 0, 0, 0, 0, 0, 0, 0,
                    7112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7111,
                    0, 7110, 7109, 0, 0, 7108, 7107, 0, 7106, 0, 0, 0, 0, 7105, 7104,
                    0, 7103, 0, 0, 0, 7102, 0, 0, 0, 0, 0, 0, 0, 0, 7101, 7100, 0,
                    7099, 0, 0, 0, 7098, 0, 0, 0, 0, 0, 0, 0, 7097, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7096, 7095, 0, 7094, 0, 0, 0, 7093,
                    0, 0, 0, 0, 0, 0, 0, 7092, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 7091, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7090,
                    0, 0, 0, 7089, 0, 7088, 7087, 0, 0, 0, 0, 7086, 0, 7085, 7084, 0,
                    0, 7083, 7082, 0, 7081, 0, 0, 0, 0, 0, 0, 7080, 0, 7079, 7078, 0,
                    0, 7077, 7076, 0, 7075, 0, 0, 0, 0, 7074, 7073, 0, 7072, 0, 0, 0,
                    7071, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7070, 0, 7069, 7068, 0, 0,
                    7067, 7066, 0, 7065, 0, 0, 0, 0, 7064, 7063, 0, 7062, 0, 0, 0,
                    7061, 0, 0, 0, 0, 0, 0, 0, 0, 7060, 7059, 0, 7058, 0, 0, 0, 7057,
                    0, 0, 0, 0, 0, 0, 0, 7056, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 7055, 0, 7054, 7053, 0, 0, 7052, 7051, 0, 7050, 0,
                    0, 0, 0, 7049, 7048, 0, 7047, 0, 0, 0, 7046, 0, 0, 0, 0, 0, 0, 0,
                    0, 7045, 7044, 0, 7043, 0, 0, 0, 7042, 0, 0, 0, 0, 0, 0, 0, 7041,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7040, 7039, 0,
                    7038, 0, 0, 0, 7037, 0, 0, 0, 0, 0, 0, 0, 7036, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 7035, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 7034, 0, 7033, 7032, 0, 0, 7031, 7030, 0, 7029, 0, 0, 0, 0,
                    7028, 7027, 0, 7026, 0, 0, 0, 7025, 0, 0, 0, 0, 0, 0, 0, 0, 7024,
                    7023, 0, 7022, 0, 0, 0, 7021, 0, 0, 0, 0, 0, 0, 0, 7020, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7019, 7018, 0, 7017, 0, 0,
                    0, 7016, 0, 0, 0, 0, 0, 0, 0, 7015, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 7014, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7013, 7012, 0,
                    7011, 0, 0, 0, 7010, 0, 0, 0, 0, 0, 0, 0, 7009, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 7008, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1602,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7007, 0, 0, 0, 0, 0, 0, 0,
                    7006, 0, 0, 0, 7005, 0, 7004, 7003, 0, 0, 0, 0, 0, 0, 0, 0, 7002,
                    0, 0, 0, 7001, 0, 7000, 6999, 0, 0, 0, 0, 6998, 0, 6997, 6996, 0,
                    0, 6995, 6994, 0, 6993, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6992, 0, 0,
                    0, 6991, 0, 6990, 6989, 0, 0, 0, 0, 6988, 0, 6987, 6986, 0, 0,
                    6985, 6984, 0, 6983, 0, 0, 0, 0, 0, 0, 6982, 0, 6981, 6980, 0, 0,
                    6979, 6978, 0, 6977, 0, 0, 0, 0, 6976, 6975, 0, 6974, 0, 0, 0,
                    6973, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6972, 0, 0, 0,
                    6971, 0, 6970, 6969, 0, 0, 0, 0, 6968, 0, 6967, 6966, 0, 0, 6965,
                    6964, 0, 6963, 0, 0, 0, 0, 0, 0, 6962, 0, 6961, 6960, 0, 0, 6959,
                    6958, 0, 6957, 0, 0, 0, 0, 6956, 6955, 0, 6954, 0, 0, 0, 6953, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 6952, 0, 6951, 6950, 0, 0, 6949, 6948,
                    0, 6947, 0, 0, 0, 0, 6946, 6945, 0, 6944, 0, 0, 0, 6943, 0, 0, 0,
                    0, 0, 0, 0, 0, 6942, 6941, 0, 6940, 0, 0, 0, 6939, 0, 0, 0, 0, 0,
                    0, 0, 6938, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6937, 0, 0, 0, 6936, 0, 6935, 6934, 0, 0, 0, 0, 6933,
                    0, 6932, 6931, 0, 0, 6930, 6929, 0, 6928, 0, 0, 0, 0, 0, 0, 6927,
                    0, 6926, 6925, 0, 0, 6924, 6923, 0, 6922, 0, 0, 0, 0, 6921, 6920,
                    0, 6919, 0, 0, 0, 6918, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6917, 0,
                    6916, 6915, 0, 0, 6914, 6913, 0, 6912, 0, 0, 0, 0, 6911, 6910, 0,
                    6909, 0, 0, 0, 6908, 0, 0, 0, 0, 0, 0, 0, 0, 6907, 6906, 0, 6905,
                    0, 0, 0, 6904, 0, 0, 0, 0, 0, 0, 0, 6903, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6902, 0, 6901, 6900, 0, 0, 6899,
                    6898, 0, 6897, 0, 0, 0, 0, 6896, 6895, 0, 6894, 0, 0, 0, 6893, 0,
                    0, 0, 0, 0, 0, 0, 0, 6892, 6891, 0, 6890, 0, 0, 0, 6889, 0, 0, 0,
                    0, 0, 0, 0, 6888, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6887, 6886, 0, 6885, 0, 0, 0, 6884, 0, 0, 0, 0, 0, 0, 0, 6883, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6882, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6881, 0, 0, 0, 6880, 0, 6879, 6878,
                    0, 0, 0, 0, 6877, 0, 6876, 6875, 0, 0, 6874, 6873, 0, 6872, 0, 0,
                    0, 0, 0, 0, 6871, 0, 6870, 6869, 0, 0, 6868, 6867, 0, 6866, 0, 0,
                    0, 0, 6865, 6864, 0, 6863, 0, 0, 0, 6862, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 6861, 0, 6860, 6859, 0, 0, 6858, 6857, 0, 6856, 0, 0, 0, 0,
                    6855, 6854, 0, 6853, 0, 0, 0, 6852, 0, 0, 0, 0, 0, 0, 0, 0, 6851,
                    6850, 0, 6849, 0, 0, 0, 6848, 0, 0, 0, 0, 0, 0, 0, 6847, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6846, 0, 6845, 6844,
                    0, 0, 6843, 6842, 0, 6841, 0, 0, 0, 0, 6840, 6839, 0, 6838, 0, 0,
                    0, 6837, 0, 0, 0, 0, 0, 0, 0, 0, 6836, 6835, 0, 6834, 0, 0, 0,
                    6833, 0, 0, 0, 0, 0, 0, 0, 6832, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 6831, 6830, 0, 6829, 0, 0, 0, 6828, 0, 0, 0, 0, 0,
                    0, 0, 6827, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6826, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6825, 0, 6824, 6823, 0, 0, 6822,
                    6821, 0, 6820, 0, 0, 0, 0, 6819, 6818, 0, 6817, 0, 0, 0, 6816, 0,
                    0, 0, 0, 0, 0, 0, 0, 6815, 6814, 0, 6813, 0, 0, 0, 6812, 0, 0, 0,
                    0, 0, 0, 0, 6811, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6810, 6809, 0, 6808, 0, 0, 0, 6807, 0, 0, 0, 0, 0, 0, 0, 6806, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6805, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6804, 6803, 0, 6802, 0, 0, 0, 6801, 0, 0, 0, 0, 0, 0,
                    0, 6800, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6799, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6798, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6797, 0, 0, 0, 6796, 0,
                    6795, 6794, 0, 0, 0, 0, 6793, 0, 6792, 6791, 0, 0, 6790, 6789, 0,
                    6788, 0, 0, 0, 0, 0, 0, 6787, 0, 6786, 6785, 0, 0, 6784, 6783, 0,
                    6782, 0, 0, 0, 0, 6781, 6780, 0, 6779, 0, 0, 0, 6778, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6777, 0, 6776, 6775, 0, 0, 6774, 6773, 0, 6772,
                    0, 0, 0, 0, 6771, 6770, 0, 6769, 0, 0, 0, 6768, 0, 0, 0, 0, 0, 0,
                    0, 0, 6767, 6766, 0, 6765, 0, 0, 0, 6764, 0, 0, 0, 0, 0, 0, 0,
                    6763, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6762,
                    0, 6761, 6760, 0, 0, 6759, 6758, 0, 6757, 0, 0, 0, 0, 6756, 6755,
                    0, 6754, 0, 0, 0, 6753, 0, 0, 0, 0, 0, 0, 0, 0, 6752, 6751, 0,
                    6750, 0, 0, 0, 6749, 0, 0, 0, 0, 0, 0, 0, 6748, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6747, 6746, 0, 6745, 0, 0, 0, 6744,
                    0, 0, 0, 0, 0, 0, 0, 6743, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 6742, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6741, 0, 6740,
                    6739, 0, 0, 6738, 6737, 0, 6736, 0, 0, 0, 0, 6735, 6734, 0, 6733,
                    0, 0, 0, 6732, 0, 0, 0, 0, 0, 0, 0, 0, 6731, 6730, 0, 6729, 0, 0,
                    0, 6728, 0, 0, 0, 0, 0, 0, 0, 6727, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6726, 6725, 0, 6724, 0, 0, 0, 6723, 0, 0, 0, 0,
                    0, 0, 0, 6722, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6721,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6720, 6719, 0, 6718, 0, 0, 0, 6717,
                    0, 0, 0, 0, 0, 0, 0, 6716, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 6715, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6714, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6713, 0, 6712, 6711,
                    0, 0, 6710, 6709, 0, 6708, 0, 0, 0, 0, 6707, 6706, 0, 6705, 0, 0,
                    0, 6704, 0, 0, 0, 0, 0, 0, 0, 0, 6703, 6702, 0, 6701, 0, 0, 0,
                    6700, 0, 0, 0, 0, 0, 0, 0, 6699, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 6698, 6697, 0, 6696, 0, 0, 0, 6695, 0, 0, 0, 0, 0,
                    0, 0, 6694, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6693, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 6692, 6691, 0, 6690, 0, 0, 0, 6689, 0,
                    0, 0, 0, 0, 0, 0, 6688, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 6687, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6686, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6685, 6684, 0, 6683, 0, 0, 0,
                    6682, 0, 0, 0, 0, 0, 0, 0, 6681, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6680, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6679, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1601, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 1609, 0, 0, 0, 0, 0, 0, 0, 6678, 0, 0, 0, 6677, 0,
                    6676, 6675, 0, 0, 0, 0, 0, 0, 0, 0, 6674, 0, 0, 0, 6673, 0, 6672,
                    6671, 0, 0, 0, 0, 6670, 0, 6669, 6668, 0, 0, 6667, 6666, 0, 6665,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6664, 0, 0, 0, 6663, 0, 6662, 6661,
                    0, 0, 0, 0, 6660, 0, 6659, 6658, 0, 0, 6657, 6656, 0, 6655, 0, 0,
                    0, 0, 0, 0, 6654, 0, 6653, 6652, 0, 0, 6651, 6650, 0, 6649, 0, 0,
                    0, 0, 6648, 6647, 0, 6646, 0, 0, 0, 6645, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6644, 0, 0, 0, 6643, 0, 6642, 6641, 0, 0, 0, 0,
                    6640, 0, 6639, 6638, 0, 0, 6637, 6636, 0, 6635, 0, 0, 0, 0, 0, 0,
                    6634, 0, 6633, 6632, 0, 0, 6631, 6630, 0, 6629, 0, 0, 0, 0, 6628,
                    6627, 0, 6626, 0, 0, 0, 6625, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6624,
                    0, 6623, 6622, 0, 0, 6621, 6620, 0, 6619, 0, 0, 0, 0, 6618, 6617,
                    0, 6616, 0, 0, 0, 6615, 0, 0, 0, 0, 0, 0, 0, 0, 6614, 6613, 0,
                    6612, 0, 0, 0, 6611, 0, 0, 0, 0, 0, 0, 0, 6610, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6609, 0, 0, 0,
                    6608, 0, 6607, 6606, 0, 0, 0, 0, 6605, 0, 6604, 6603, 0, 0, 6602,
                    6601, 0, 6600, 0, 0, 0, 0, 0, 0, 6599, 0, 6598, 6597, 0, 0, 6596,
                    6595, 0, 6594, 0, 0, 0, 0, 6593, 6592, 0, 6591, 0, 0, 0, 6590, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 6589, 0, 6588, 6587, 0, 0, 6586, 6585,
                    0, 6584, 0, 0, 0, 0, 6583, 6582, 0, 6581, 0, 0, 0, 6580, 0, 0, 0,
                    0, 0, 0, 0, 0, 6579, 6578, 0, 6577, 0, 0, 0, 6576, 0, 0, 0, 0, 0,
                    0, 0, 6575, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6574, 0, 6573, 6572, 0, 0, 6571, 6570, 0, 6569, 0, 0, 0, 0, 6568,
                    6567, 0, 6566, 0, 0, 0, 6565, 0, 0, 0, 0, 0, 0, 0, 0, 6564, 6563,
                    0, 6562, 0, 0, 0, 6561, 0, 0, 0, 0, 0, 0, 0, 6560, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6559, 6558, 0, 6557, 0, 0, 0,
                    6556, 0, 0, 0, 0, 0, 0, 0, 6555, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6554, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6553, 0, 0, 0, 6552, 0, 6551, 6550, 0, 0, 0, 0, 6549, 0, 6548,
                    6547, 0, 0, 6546, 6545, 0, 6544, 0, 0, 0, 0, 0, 0, 6543, 0, 6542,
                    6541, 0, 0, 6540, 6539, 0, 6538, 0, 0, 0, 0, 6537, 6536, 0, 6535,
                    0, 0, 0, 6534, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6533, 0, 6532, 6531,
                    0, 0, 6530, 6529, 0, 6528, 0, 0, 0, 0, 6527, 6526, 0, 6525, 0, 0,
                    0, 6524, 0, 0, 0, 0, 0, 0, 0, 0, 6523, 6522, 0, 6521, 0, 0, 0,
                    6520, 0, 0, 0, 0, 0, 0, 0, 6519, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6518, 0, 6517, 6516, 0, 0, 6515, 6514, 0,
                    6513, 0, 0, 0, 0, 6512, 6511, 0, 6510, 0, 0, 0, 6509, 0, 0, 0, 0,
                    0, 0, 0, 0, 6508, 6507, 0, 6506, 0, 0, 0, 6505, 0, 0, 0, 0, 0, 0,
                    0, 6504, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6503,
                    6502, 0, 6501, 0, 0, 0, 6500, 0, 0, 0, 0, 0, 0, 0, 6499, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6498, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6497, 0, 6496, 6495, 0, 0, 6494, 6493, 0, 6492, 0, 0,
                    0, 0, 6491, 6490, 0, 6489, 0, 0, 0, 6488, 0, 0, 0, 0, 0, 0, 0, 0,
                    6487, 6486, 0, 6485, 0, 0, 0, 6484, 0, 0, 0, 0, 0, 0, 0, 6483, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6482, 6481, 0, 6480,
                    0, 0, 0, 6479, 0, 0, 0, 0, 0, 0, 0, 6478, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6477, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6476,
                    6475, 0, 6474, 0, 0, 0, 6473, 0, 0, 0, 0, 0, 0, 0, 6472, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6471, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 6470, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6469, 0, 0, 0, 6468, 0, 6467, 6466, 0, 0, 0,
                    0, 6465, 0, 6464, 6463, 0, 0, 6462, 6461, 0, 6460, 0, 0, 0, 0, 0,
                    0, 6459, 0, 6458, 6457, 0, 0, 6456, 6455, 0, 6454, 0, 0, 0, 0,
                    6453, 6452, 0, 6451, 0, 0, 0, 6450, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6449, 0, 6448, 6447, 0, 0, 6446, 6445, 0, 6444, 0, 0, 0, 0, 6443,
                    6442, 0, 6441, 0, 0, 0, 6440, 0, 0, 0, 0, 0, 0, 0, 0, 6439, 6438,
                    0, 6437, 0, 0, 0, 6436, 0, 0, 0, 0, 0, 0, 0, 6435, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6434, 0, 6433, 6432, 0, 0,
                    6431, 6430, 0, 6429, 0, 0, 0, 0, 6428, 6427, 0, 6426, 0, 0, 0,
                    6425, 0, 0, 0, 0, 0, 0, 0, 0, 6424, 6423, 0, 6422, 0, 0, 0, 6421,
                    0, 0, 0, 0, 0, 0, 0, 6420, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 6419, 6418, 0, 6417, 0, 0, 0, 6416, 0, 0, 0, 0, 0, 0, 0,
                    6415, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6414, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 6413, 0, 6412, 6411, 0, 0, 6410, 6409,
                    0, 6408, 0, 0, 0, 0, 6407, 6406, 0, 6405, 0, 0, 0, 6404, 0, 0, 0,
                    0, 0, 0, 0, 0, 6403, 6402, 0, 6401, 0, 0, 0, 6400, 0, 0, 0, 0, 0,
                    0, 0, 6399, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6398,
                    6397, 0, 6396, 0, 0, 0, 6395, 0, 0, 0, 0, 0, 0, 0, 6394, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6393, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 6392, 6391, 0, 6390, 0, 0, 0, 6389, 0, 0, 0, 0, 0, 0, 0,
                    6388, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6387, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6386, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 6385, 0, 6384, 6383, 0, 0, 6382, 6381, 0,
                    6380, 0, 0, 0, 0, 6379, 6378, 0, 6377, 0, 0, 0, 6376, 0, 0, 0, 0,
                    0, 0, 0, 0, 6375, 6374, 0, 6373, 0, 0, 0, 6372, 0, 0, 0, 0, 0, 0,
                    0, 6371, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6370,
                    6369, 0, 6368, 0, 0, 0, 6367, 0, 0, 0, 0, 0, 0, 0, 6366, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6365, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 6364, 6363, 0, 6362, 0, 0, 0, 6361, 0, 0, 0, 0, 0, 0, 0,
                    6360, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6359, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6358, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6357, 6356, 0, 6355, 0, 0, 0, 6354, 0, 0, 0, 0,
                    0, 0, 0, 6353, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6352,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 6351, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 6350, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6349, 0, 0, 0, 6348, 0, 6347,
                    6346, 0, 0, 0, 0, 6345, 0, 6344, 6343, 0, 0, 6342, 6341, 0, 6340,
                    0, 0, 0, 0, 0, 0, 6339, 0, 6338, 6337, 0, 0, 6336, 6335, 0, 6334,
                    0, 0, 0, 0, 6333, 6332, 0, 6331, 0, 0, 0, 6330, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6329, 0, 6328, 6327, 0, 0, 6326, 6325, 0, 6324, 0, 0,
                    0, 0, 6323, 6322, 0, 6321, 0, 0, 0, 6320, 0, 0, 0, 0, 0, 0, 0, 0,
                    6319, 6318, 0, 6317, 0, 0, 0, 6316, 0, 0, 0, 0, 0, 0, 0, 6315, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6314, 0, 6313,
                    6312, 0, 0, 6311, 6310, 0, 6309, 0, 0, 0, 0, 6308, 6307, 0, 6306,
                    0, 0, 0, 6305, 0, 0, 0, 0, 0, 0, 0, 0, 6304, 6303, 0, 6302, 0, 0,
                    0, 6301, 0, 0, 0, 0, 0, 0, 0, 6300, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 6299, 6298, 0, 6297, 0, 0, 0, 6296, 0, 0, 0, 0,
                    0, 0, 0, 6295, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6294,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6293, 0, 6292, 6291, 0, 0,
                    6290, 6289, 0, 6288, 0, 0, 0, 0, 6287, 6286, 0, 6285, 0, 0, 0,
                    6284, 0, 0, 0, 0, 0, 0, 0, 0, 6283, 6282, 0, 6281, 0, 0, 0, 6280,
                    0, 0, 0, 0, 0, 0, 0, 6279, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 6278, 6277, 0, 6276, 0, 0, 0, 6275, 0, 0, 0, 0, 0, 0, 0,
                    6274, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6273, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6272, 6271, 0, 6270, 0, 0, 0, 6269, 0, 0, 0,
                    0, 0, 0, 0, 6268, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6267, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6266, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6265, 0, 6264, 6263, 0, 0,
                    6262, 6261, 0, 6260, 0, 0, 0, 0, 6259, 6258, 0, 6257, 0, 0, 0,
                    6256, 0, 0, 0, 0, 0, 0, 0, 0, 6255, 6254, 0, 6253, 0, 0, 0, 6252,
                    0, 0, 0, 0, 0, 0, 0, 6251, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 6250, 6249, 0, 6248, 0, 0, 0, 6247, 0, 0, 0, 0, 0, 0, 0,
                    6246, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6245, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6244, 6243, 0, 6242, 0, 0, 0, 6241, 0, 0, 0,
                    0, 0, 0, 0, 6240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6239, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6238, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6237, 6236, 0, 6235, 0, 0, 0,
                    6234, 0, 0, 0, 0, 0, 0, 0, 6233, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6232, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6231, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6230, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6229, 0, 6228, 6227, 0,
                    0, 6226, 6225, 0, 6224, 0, 0, 0, 0, 6223, 6222, 0, 6221, 0, 0, 0,
                    6220, 0, 0, 0, 0, 0, 0, 0, 0, 6219, 6218, 0, 6217, 0, 0, 0, 6216,
                    0, 0, 0, 0, 0, 0, 0, 6215, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 6214, 6213, 0, 6212, 0, 0, 0, 6211, 0, 0, 0, 0, 0, 0, 0,
                    6210, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6209, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 6208, 6207, 0, 6206, 0, 0, 0, 6205, 0, 0, 0,
                    0, 0, 0, 0, 6204, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    6203, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6202, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6201, 6200, 0, 6199, 0, 0, 0,
                    6198, 0, 0, 0, 0, 0, 0, 0, 6197, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6196, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6195, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6194, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6193, 6192, 0, 6191, 0, 0, 0,
                    6190, 0, 0, 0, 0, 0, 0, 0, 6189, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 6188, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6187, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6186, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1600,
            };
        }

        static class Hash {
            static class Adjust {
                public final short[] TABLE = {
                        0, 5628, 7017, 1298, 2918, 2442, 8070, 6383, 6383, 7425, 2442, 5628, 8044, 7425, 3155, 6383, 2918, 7452,
                        1533, 6849, 5586, 7452, 7452, 1533, 2209, 6029, 2794, 3509, 7992, 7733, 7452, 131, 6029, 4491, 1814,
                        7452, 6110, 3155, 7077, 6675, 532, 1334, 7555, 5325, 3056, 1403, 1403, 3969, 4491, 1403, 7592, 522,
                        8070, 1403, 0, 1905, 3584, 2918, 922, 3304, 6675, 0, 7622, 7017, 3210, 2139, 1403, 5225, 0, 3969, 7992,
                        5743, 5499, 5499, 5345, 7452, 522, 305, 3056, 7017, 7017, 2139, 1338, 3056, 7452, 1403, 6799, 3204,
                        3290, 4099, 1814, 2191, 4099, 5743, 1570, 1334, 7363, 1905, 0, 6799, 4400, 1480, 6029, 1905, 0, 7525,
                        2028, 2794, 131, 7646, 3155, 4986, 1858, 2442, 7992, 1607, 3584, 4986, 706, 6029, 5345, 7622, 6322,
                        5196, 1905, 6847, 218, 1785, 0, 4099, 2981, 6849, 4751, 3950, 7733, 3056, 5499, 4055, 6849, 1533, 131,
                        5196, 2918, 3879, 5325, 2794, 6029, 0, 0, 322, 7452, 6178, 2918, 2320, 6675, 3056, 6675, 1533, 6029,
                        1428, 2280, 2171, 6788, 7452, 3325, 107, 4262, 311, 5562, 7857, 6110, 2139, 4942, 4600, 1905, 0, 3083,
                        5345, 7452, 6675, 0, 6112, 4099, 7017, 1338, 6799, 2918, 1232, 3584, 522, 6029, 5325, 1403, 6759, 6849,
                        508, 6675, 2987, 7745, 6870, 896, 7452, 1232, 4400, 12, 2981, 3850, 4491, 6849, 0, 6675, 747, 4491,
                        7525, 6675, 7452, 7992, 6921, 7323, 6849, 3056, 1199, 2139, 6029, 6029, 190, 4351, 7891, 4400, 7134,
                        1533, 1194, 3950, 6675, 5345, 6383, 7622, 131, 1905, 2883, 6383, 1533, 5345, 2794, 4303, 1403, 0, 1338,
                        2794, 992, 4871, 6383, 4099, 2794, 3889, 6184, 3304, 1905, 6383, 3950, 3056, 522, 1810, 3975, 7622,
                        7452, 522, 6799, 5866, 7084, 7622, 6528, 2798, 7452, 1810, 7907, 642, 5345, 1905, 6849, 6675, 7745,
                        2918, 4751, 3229, 2139, 6029, 5207, 6601, 2139, 7452, 5890, 1428, 5628, 7622, 2139, 3146, 2400, 578,
                        941, 7672, 1814, 3210, 1533, 4491, 12, 2918, 1900, 7425, 2794, 2987, 3465, 1377, 3822, 3969, 3210, 859,
                        5499, 6878, 1377, 3056, 4027, 8065, 8065, 5207, 4400, 4303, 3210, 3210, 0, 6675, 357, 5628, 5512, 1905,
                        3452, 1403, 7646, 859, 6788, 3210, 2139, 378, 5663, 7733, 870, 0, 4491, 4813, 2110, 578, 2139, 3056,
                        4099, 1905, 1298, 4672, 2191, 3950, 5499, 3969, 4974, 6323, 6029, 7414, 6383, 0, 4974, 3210, 795, 4099,
                        131, 5345, 5345, 6576, 1810, 1621, 4400, 2918, 1905, 2442, 2679, 6322, 7452, 2110, 1403, 6383, 2653,
                        5132, 6856, 7841, 2794, 6110, 2028, 6675, 7425, 6999, 7441, 6029, 183, 6675, 4400, 859, 1403, 2794,
                        5985, 5345, 1533, 322, 4400, 1227, 5890, 4474, 4491, 3574, 8166, 6849, 7086, 5345, 5345, 5459, 3584,
                        6675, 3969, 7579, 8044, 2295, 2577, 1480, 5743, 3304, 5499, 330, 4303, 6863, 3822, 4600, 4751, 5628,
                        3822, 2918, 6675, 2400, 6663, 1403, 6849, 6029, 3145, 6110, 3210, 747, 3229, 3056, 2918, 7733, 330,
                        4055, 7322, 5628, 2987, 3056, 1905, 2903, 669, 5325, 2845, 4099, 5225, 6283, 4099, 5000, 642, 4055,
                        5345, 8034, 2918, 1041, 5769, 7051, 1538, 2918, 3366, 608, 4303, 3921, 0, 2918, 1905, 218, 6687, 5963,
                        859, 3083, 2987, 896, 5056, 1905, 2918, 4415, 7966, 7646, 2883, 5628, 7017, 8029, 6528, 4474, 6322,
                        5562, 6669, 4610, 7006,
                };
            }

            static public class Values {
                public final short[] TABLE = {
                        148, 2934, 166, 5107, 4628, 166, 166, 166, 166, 3033, 166, 4692, 166, 5571, 2225, 166,
                        5340, 3423, 166, 3191, 1752, 166, 5212, 166, 166, 3520, 166, 166, 166, 1867, 166, 3313,
                        166, 3461, 166, 166, 3174, 1737, 5010, 5008, 166, 4344, 2868, 3877, 166, 4089, 166, 5041,
                        4748, 4073, 4066, 5298, 3502, 1812, 166, 5309, 166, 233, 3493, 166, 166, 3728, 5236, 4252,
                        4010, 2149, 166, 164, 4580, 3039, 4804, 3874, 166, 6170, 2812, 166, 4334, 166, 166, 166,
                        166, 166, 166, 1862, 224, 2131, 6081, 166, 2710, 166, 166, 166, 4765, 166, 1964, 5060,
                        166, 1897, 166, 3987, 166, 166, 5566, 2021, 166, 45, 166, 166, 3283, 3932, 166, 166,
                        3519, 166, 166, 291, 166, 166, 5132, 2800, 166, 166, 166, 5531, 4054, 166, 3509, 166,
                        166, 4908, 3028, 1756, 1910, 4671, 2729, 5224, 166, 121, 3327, 3317, 166, 181, 2371, 5541,
                        166, 1787, 2666, 5134, 5698, 166, 5480, 3870, 166, 3823, 166, 3165, 5343, 5123, 5089, 166,
                        2422, 3724, 166, 2735, 1953, 5724, 4444, 4871, 166, 166, 5001, 5512, 3133, 5171, 166, 2216,
                        166, 4877, 4542, 166, 166, 166, 5270, 166, 166, 166, 1922, 69, 3547, 166, 166, 166,
                        166, 166, 231, 4547, 5155, 3357, 3464, 166, 72, 3332, 166, 4392, 5971, 3896, 4451, 3173,
                        2569, 166, 4466, 2518, 1698, 2850, 5349, 166, 166, 4457, 5062, 166, 2202, 1650, 2191, 166,
                        1950, 2583, 166, 5293, 2032, 5893, 166, 3994, 5392, 3878, 96, 166, 166, 3195, 166, 4001,
                        1900, 2513, 6027, 166, 166, 166, 166, 5407, 166, 166, 2332, 5125, 5891, 3096, 3172, 166,
                        166, 3065, 166, 166, 4535, 166, 166, 166, 4553, 3131, 3693, 166, 2255, 2613, 166, 166,
                        166, 166, 2866, 166, 166, 166, 2940, 5333, 3199, 166, 2628, 4312, 166, 166, 1794, 4681,
                        2058, 3606, 166, 166, 3542, 2166, 4696, 2520, 166, 4739, 166, 2563, 166, 166, 3681, 166,
                        166, 166, 4127, 1967, 2972, 166, 5227, 166, 166, 5551, 4255, 56, 166, 5553, 3219, 4367,
                        166, 3218, 4749, 2886, 3695, 3711, 2228, 166, 166, 166, 2268, 5054, 3749, 4825, 166, 4933,
                        4992, 4530, 166, 4892, 3400, 166, 197, 166, 6078, 166, 166, 3971, 166, 166, 5357, 1852,
                        3377, 166, 5196, 3740, 5320, 166, 166, 3099, 166, 4562, 6061, 3294, 166, 166, 166, 166,
                        3266, 3627, 2567, 166, 228, 2773, 166, 166, 53, 1833, 2401, 124, 166, 4272, 3922, 5959,
                        2903, 3923, 166, 6155, 166, 166, 166, 166, 216, 166, 5247, 166, 5591, 166, 166, 82,
                        87, 4526, 166, 166, 5439, 166, 4935, 166, 3187, 1869, 166, 1764, 5500, 6023, 3356, 166,
                        3350, 2457, 2455, 166, 1637, 166, 3342, 166, 166, 3355, 5154, 166, 276, 166, 166, 166,
                        3371, 5969, 166, 1665, 166, 166, 166, 166, 166, 166, 166, 4092, 1712, 3122, 5086, 166,
                        166, 4906, 166, 2591, 166, 166, 166, 1894, 2997, 166, 4476, 4384, 166, 4747, 4109, 2655,
                        166, 5978, 1636, 4898, 166, 166, 166, 166, 166, 166, 166, 5207, 166, 166, 3712, 3876,
                        91, 5876, 3786, 5998, 166, 166, 166, 4391, 166, 166, 2832, 2220, 4435, 166, 166, 5796,
                        3156, 6112, 166, 1643, 1821, 3129, 166, 4200, 166, 5857, 166, 166, 2351, 5902, 1855, 5043,
                        166, 3167, 5191, 3996, 5718, 4876, 3071, 2965, 5735, 5930, 6149, 2345, 3297, 3822, 166, 166,
                        307, 6019, 1859, 2981, 4914, 3320, 6165, 2328, 140, 2372, 308, 166, 2280, 5081, 166, 3275,
                        166, 159, 2399, 2327, 5489, 4690, 6059, 4492, 4269, 6058, 166, 19, 166, 3323, 5708, 128,
                        4812, 2949, 166, 166, 2890, 2630, 5237, 166, 256, 3673, 4621, 5380, 166, 3353, 166, 1651,
                        2573, 1635, 4011, 3429, 3370, 3720, 166, 166, 6108, 3848, 5104, 2851, 1998, 166, 166, 5106,
                        20, 166, 2633, 166, 166, 166, 166, 5662, 125, 3651, 1731, 4702, 166, 3197, 166, 2947,
                        3046, 4196, 2185, 6100, 166, 2602, 2908, 2487, 166, 5232, 166, 4028, 5919, 166, 2680, 3608,
                        3252, 166, 4899, 166, 166, 166, 166, 2529, 166, 166, 166, 166, 166, 2534, 166, 2299,
                        4076, 166, 3643, 166, 3921, 166, 166, 166, 1939, 2124, 1829, 2436, 3892, 166, 3481, 271,
                        5307, 1697, 166, 166, 5098, 2906, 5545, 166, 5980, 3203, 166, 1903, 4626, 4674, 6118, 6097,
                        5926, 4136, 1677, 3232, 4720, 166, 166, 166, 229, 2012, 3620, 166, 3798, 166, 166, 2609,
                        3489, 3809, 166, 166, 166, 166, 166, 166, 166, 5826, 166, 166, 166, 4903, 166, 166,
                        166, 166, 6168, 166, 5052, 5044, 5644, 2375, 2677, 4012, 3062, 5831, 4752, 166, 4125, 2610,
                        2062, 3238, 292, 2533, 5872, 51, 166, 1947, 4225, 166, 2288, 4845, 166, 5788, 166, 5717,
                        166, 166, 5549, 5619, 166, 4165, 166, 2721, 2311, 5501, 4416, 4383, 166, 166, 3068, 5499,
                        5936, 166, 4204, 4766, 4688, 1870, 5220, 166, 166, 166, 166, 237, 2523, 6039, 3061, 2793,
                        3998, 166, 2545, 2309, 3144, 3679, 3969, 166, 166, 166, 4379, 3574, 205, 2808, 5822, 166,
                        166, 2188, 4823, 4990, 5561, 5711, 166, 5627, 6034, 5253, 3783, 5047, 4405, 166, 59, 1755,
                        3178, 318, 166, 4710, 2933, 3409, 6062, 2821, 166, 6099, 166, 4178, 166, 166, 4122, 36,
                        4779, 166, 166, 4323, 3073, 5410, 2101, 166, 166, 44, 5690, 166, 3265, 166, 5222, 5909,
                        1838, 166, 4755, 2215, 166, 4082, 166, 166, 3210, 5140, 3124, 5238, 166, 5913, 2321, 166,
                        2416, 5976, 3918, 5078, 4218, 5703, 4897, 6011, 5685, 2214, 166, 166, 6180, 5175, 1715, 166,
                        166, 3760, 4497, 1808, 4826, 166, 2540, 166, 166, 5513, 4971, 5915, 166, 166, 2525, 166,
                        4480, 42, 232, 2412, 2797, 3229, 5263, 2852, 5543, 2126, 3562, 166, 2872, 4695, 5985, 5136,
                        2714, 4262, 5473, 166, 4160, 4347, 166, 166, 166, 166, 5271, 166, 166, 5108, 166, 166,
                        166, 166, 5437, 4875, 3963, 4362, 5820, 5559, 4890, 4728, 166, 166, 2692, 166, 4870, 3591,
                        5472, 166, 2690, 166, 5854, 3817, 166, 280, 166, 166, 113, 4128, 3396, 166, 4264, 5058,
                        2283, 166, 2281, 4916, 5671, 166, 2708, 166, 166, 4589, 166, 166, 4689, 166, 1686, 166,
                        166, 166, 166, 166, 1774, 166, 166, 166, 5651, 3777, 2234, 166, 3864, 18, 3589, 4592,
                        4777, 166, 166, 5254, 4245, 166, 166, 166, 4368, 5172, 3522, 166, 4306, 153, 5230, 166,
                        5598, 5420, 311, 2414, 4159, 2985, 5137, 166, 2179, 1801, 166, 4595, 2083, 2020, 166, 3602,
                        2170, 4259, 3048, 166, 166, 4193, 2350, 166, 166, 2702, 166, 4521, 166, 166, 2496, 166,
                        4593, 2006, 166, 166, 2292, 4135, 166, 6069, 4623, 166, 166, 4827, 3995, 4291, 3243, 166,
                        166, 166, 5622, 166, 3539, 166, 166, 4915, 4373, 2479, 3775, 6008, 5838, 4321, 1612, 5530,
                        166, 3773, 4267, 4086, 3081, 2261, 166, 166, 4785, 4641, 5292, 166, 4820, 5612, 5556, 166,
                        166, 166, 4396, 6084, 3414, 166, 3331, 2380, 5921, 4315, 2340, 166, 5511, 166, 4713, 3754,
                        2912, 2553, 166, 3468, 5388, 166, 1932, 3540, 5834, 166, 166, 3186, 5258, 166, 4107, 166,
                        166, 166, 166, 166, 166, 166, 166, 2108, 12, 2368, 2789, 166, 166, 4148, 1878, 166,
                        166, 2324, 4179, 2945, 2531, 166, 166, 166, 4485, 3765, 2308, 166, 2754, 166, 6102, 166,
                        1921, 260, 2241, 166, 2592, 166, 166, 166, 4964, 166, 3055, 5261, 4943, 2916, 166, 201,
                        5728, 166, 5759, 4314, 4730, 6024, 166, 4926, 4762, 1834, 2055, 166, 40, 166, 5416, 166,
                        3722, 2360, 1928, 166, 4889, 4590, 5550, 3498, 166, 6003, 2029, 4106, 4346, 3758, 166, 2753,
                        103, 1891, 5067, 166, 3398, 2079, 5784, 3074, 3787, 166, 166, 3936, 166, 5766, 166, 4847,
                        3928, 5119, 166, 5181, 4602, 2605, 5712, 4523, 166, 166, 4717, 166, 2227, 2181, 166, 4678,
                        166, 166, 4901, 166, 4980, 166, 166, 166, 166, 5806, 2894, 5631, 4995, 2608, 166, 166,
                        166, 3917, 166, 3417, 166, 2795, 1655, 3189, 3364, 166, 4839, 3510, 4212, 5641, 6091, 138,
                        166, 166, 3343, 4620, 2722, 4566, 166, 3518, 3424, 166, 166, 1653, 166, 5057, 166, 5375,
                        4833, 166, 4273, 4348, 166, 166, 166, 4912, 166, 3662, 166, 4281, 166, 5169, 166, 5883,
                        2737, 2572, 4685, 4068, 166, 4214, 166, 166, 2409, 166, 166, 4571, 166, 5624, 5722, 5949,
                        166, 3675, 166, 166, 5109, 3428, 166, 166, 5446, 166, 3290, 166, 3309, 166, 166, 4776,
                        166, 166, 166, 166, 166, 166, 5617, 2860, 166, 166, 166, 166, 3629, 1741, 166, 166,
                        183, 4973, 3047, 2854, 75, 2035, 3652, 2159, 166, 4150, 6037, 3225, 4519, 1902, 2678, 2413,
                        1961, 166, 166, 166, 166, 4972, 1847, 166, 5636, 4017, 166, 3345, 166, 4520, 166, 2861,
                        166, 3092, 6060, 157, 2542, 2298, 4496, 166, 2607, 6110, 5707, 2314, 166, 166, 273, 166,
                        5952, 166, 4957, 322, 6065, 2272, 6140, 2438, 3458, 3287, 166, 166, 166, 166, 2684, 288,
                        3354, 166, 166, 3983, 1702, 166, 166, 166, 2393, 2435, 4202, 3308, 5805, 5085, 166, 166,
                        1938, 166, 166, 2171, 5892, 2337, 166, 4648, 3116, 2486, 4363, 3567, 166, 166, 2822, 2041,
                        166, 4703, 3956, 5192, 166, 3975, 5720, 3647, 2134, 5932, 166, 166, 5160, 263, 166, 166,
                        166, 4549, 166, 166, 1701, 3086, 166, 166, 4737, 166, 2252, 166, 170, 166, 166, 166,
                        2301, 5478, 166, 166, 5979, 3007, 166, 166, 166, 4104, 166, 2469, 2700, 166, 4998, 3376,
                        166, 1840, 166, 166, 4470, 166, 5235, 3930, 166, 166, 166, 6031, 166, 166, 166, 3827,
                        4700, 166, 166, 166, 166, 166, 166, 4103, 3976, 166, 166, 166, 166, 5027, 4322, 5130,
                        166, 4741, 2132, 4118, 3080, 4137, 166, 6179, 166, 166, 166, 166, 166, 6120, 4188, 166,
                        2251, 166, 3253, 166, 4887, 166, 4293, 5241, 166, 166, 166, 166, 166, 166, 5076, 166,
                        166, 4177, 166, 221, 166, 2757, 5377, 166, 43, 166, 166, 3180, 5540, 166, 213, 4541,
                        166, 166, 166, 166, 166, 1641, 166, 4578, 4639, 166, 166, 1683, 2139, 1689, 5249, 5773,
                        5226, 166, 2820, 166, 5516, 5045, 166, 4896, 5657, 5189, 166, 5770, 2725, 5148, 166, 166,
                        166, 2929, 166, 3479, 166, 166, 4564, 3752, 4305, 4232, 166, 5906, 1779, 166, 2709, 4941,
                        4342, 166, 4882, 166, 4277, 2322, 166, 4879, 1610, 3038, 166, 3762, 2054, 5652, 166, 4524,
                        3820, 4806, 166, 166, 104, 3416, 4869, 4243, 4854, 166, 4114, 166, 2121, 166, 3463, 3556,
                        166, 4795, 166, 2118, 3920, 166, 166, 4667, 5046, 166, 166, 2088, 4360, 5787, 2198, 4233,
                        5552, 3970, 3523, 2037, 5791, 166, 166, 4299, 2336, 166, 166, 166, 4173, 4588, 3626, 5187,
                        166, 3363, 4611, 294, 4962, 5243, 2719, 6022, 4976, 3559, 166, 2662, 5779, 6151, 166, 3527,
                        166, 5404, 6132, 1839, 166, 3090, 166, 2253, 166, 5441, 5518, 6049, 166, 166, 6136, 3026,
                        3474, 5960, 166, 3937, 4105, 166, 2348, 2039, 4738, 166, 5233, 3882, 3840, 166, 278, 190,
                        166, 5751, 4313, 166, 3855, 166, 166, 6171, 166, 166, 5381, 3941, 166, 166, 166, 166,
                        3334, 166, 2038, 6088, 166, 1918, 5037, 2325, 2378, 4894, 3514, 3715, 5168, 166, 166, 4083,
                        2873, 166, 166, 166, 2693, 166, 3543, 166, 2577, 3013, 166, 166, 4594, 2622, 166, 166,
                        166, 3401, 166, 166, 5447, 5328, 5547, 6133, 2335, 3739, 166, 166, 166, 166, 5614, 3492,
                        3610, 3466, 166, 5336, 4354, 166, 4662, 166, 166, 4283, 166, 166, 303, 5904, 166, 2717,
                        166, 166, 2276, 5564, 2386, 5661, 2040, 166, 1630, 4652, 166, 4840, 166, 110, 5329, 3979,
                        5734, 2550, 166, 166, 6007, 5999, 2978, 4771, 5360, 166, 4023, 166, 166, 5920, 4065, 166,
                        3880, 166, 5422, 1813, 166, 6166, 73, 166, 166, 3669, 5762, 5077, 166, 2953, 85, 166,
                        3517, 166, 116, 166, 2738, 3710, 166, 1634, 166, 166, 166, 2290, 3001, 166, 166, 3037,
                        2400, 3410, 166, 1791, 4231, 166, 3546, 5009, 5299, 2807, 166, 166, 1675, 1619, 2374, 3093,
                        5302, 3278, 2330, 5301, 2343, 2307, 3274, 5017, 2265, 3700, 2465, 166, 139, 4292, 166, 5056,
                        3952, 166, 4528, 2388, 1886, 166, 166, 3016, 3698, 5881, 166, 2379, 3223, 166, 166, 3847,
                        2407, 5493, 3183, 3307, 166, 265, 166, 2421, 6161, 2057, 5363, 3863, 2474, 166, 166, 5427,
                        166, 2140, 2955, 166, 3070, 4237, 5018, 5988, 5570, 275, 4862, 2357, 166, 195, 166, 2593,
                        6047, 166, 2878, 166, 166, 2781, 3004, 4180, 166, 5593, 166, 5973, 2544, 5064, 166, 4324,
                        4701, 166, 3084, 166, 166, 5372, 4725, 166, 5650, 166, 166, 2786, 166, 3781, 3583, 3682,
                        1850, 4420, 3296, 5173, 4461, 166, 166, 166, 2984, 166, 93, 166, 166, 4336, 5943, 2922,
                        3300, 166, 4843, 166, 166, 166, 166, 2094, 166, 2939, 166, 4656, 166, 5146, 166, 166,
                        166, 166, 2104, 3977, 4660, 5312, 166, 1865, 166, 5487, 5558, 3380, 166, 1957, 3162, 3281,
                        166, 3588, 3268, 2099, 166, 166, 2319, 4913, 4187, 5503, 5782, 150, 166, 52, 5450, 166,
                        166, 166, 2941, 5877, 166, 4031, 5393, 166, 3931, 4166, 3135, 3445, 166, 5053, 5430, 4836,
                        166, 5315, 3389, 4636, 166, 166, 3441, 166, 166, 3767, 2961, 166, 4761, 4604, 3179, 166,
                        166, 4751, 2148, 2015, 166, 123, 5013, 166, 2936, 166, 2063, 166, 5823, 166, 5096, 166,
                        166, 4198, 166, 166, 166, 3845, 166, 166, 238, 166, 2703, 3541, 166, 4813, 166, 4477,
                        2349, 4197, 5996, 3324, 4789, 3063, 166, 166, 5504, 5273, 2805, 13, 166, 5601, 5402, 4119,
                        5206, 166, 166, 4251, 3704, 4176, 1963, 2882, 166, 202, 3125, 3318, 112, 166, 3362, 4835,
                        3420, 3974, 5099, 166, 4433, 166, 166, 166, 1766, 2663, 166, 166, 4683, 166, 166, 5485,
                        47, 5101, 5341, 5765, 3390, 1648, 4341, 3945, 6045, 1645, 166, 5578, 2594, 166, 166, 3772,
                        166, 166, 3196, 3603, 166, 5399, 166, 5075, 166, 5911, 4632, 4781, 5313, 270, 166, 2346,
                        166, 166, 166, 1986, 166, 166, 4958, 166, 166, 166, 4048, 166, 3076, 166, 166, 4891,
                        166, 166, 57, 166, 220, 166, 166, 166, 4117, 166, 166, 166, 166, 5194, 2658, 166,
                        166, 2942, 6071, 4182, 166, 2976, 5816, 166, 166, 166, 166, 3985, 4211, 2514, 166, 166,
                        166, 2504, 3446, 1711, 166, 166, 2107, 5190, 166, 34, 166, 3912, 5382, 3003, 166, 166,
                        166, 2999, 2404, 4734, 4455, 2087, 166, 2405, 156, 166, 2830, 3303, 296, 3295, 2067, 4268,
                        166, 166, 5642, 166, 166, 1901, 166, 5133, 166, 166, 166, 166, 3176, 2973, 4677, 166,
                        166, 6164, 3000, 2396, 2734, 5697, 5989, 166, 2823, 5265, 5852, 166, 166, 2623, 2625, 2287,
                        4844, 1758, 166, 166, 166, 166, 166, 6073, 166, 5379, 2389, 5279, 2444, 5515, 166, 4038,
                        166, 4948, 5640, 166, 166, 3572, 4258, 166, 166, 166, 5204, 166, 4603, 5797, 166, 166,
                        166, 1725, 4600, 166, 166, 5498, 166, 4152, 166, 172, 4758, 166, 2598, 2489, 2076, 4366,
                        2568, 166, 4352, 3782, 166, 166, 3059, 3946, 5138, 5727, 4484, 5694, 166, 3796, 166, 166,
                        166, 166, 5334, 1778, 2245, 166, 4517, 4419, 2250, 182, 5856, 166, 2835, 4495, 1858, 2033,
                        6014, 6086, 3211, 166, 166, 154, 2145, 166, 129, 3661, 2661, 5860, 6143, 2640, 3890, 6160,
                        166, 166, 2747, 166, 166, 2291, 282, 2476, 166, 166, 3825, 166, 1925, 166, 4489, 166,
                        166, 166, 4034, 166, 166, 166, 166, 166, 166, 122, 4708, 4919, 2373, 2453, 5419, 5954,
                        297, 5290, 166, 1978, 166, 4932, 3501, 166, 3085, 3386, 166, 5405, 4512, 166, 3209, 5740,
                        4020, 5495, 5815, 314, 166, 3190, 4824, 166, 166, 3448, 207, 1623, 6096, 5878, 166, 1836,
                        166, 166, 2728, 166, 5278, 3419, 3012, 5618, 5266, 3078, 166, 166, 2244, 166, 4569, 6068,
                        166, 3336, 166, 5677, 6052, 5079, 166, 5453, 5245, 5799, 166, 1982, 166, 5958, 4619, 5821,
                        166, 5285, 284, 1631, 5710, 6070, 5365, 2189, 3242, 166, 2752, 5483, 5297, 6150, 5522, 166,
                        1815, 166, 166, 166, 5801, 166, 166, 5398, 166, 166, 166, 2967, 2515, 3169, 166, 166,
                        2562, 166, 1617, 2069, 166, 166, 6154, 166, 3721, 166, 5327, 166, 166, 166, 5592, 166,
                        166, 2286, 1716, 3903, 166, 2395, 286, 3587, 6146, 3286, 4186, 5882, 5894, 5737, 6032, 5879,
                        2761, 4829, 3788, 166, 166, 3233, 5356, 5693, 166, 2429, 2449, 141, 3444, 5186, 166, 166,
                        3477, 4080, 4584, 166, 166, 3670, 1851, 3824, 4337, 3886, 2792, 166, 5867, 166, 166, 3557,
                        3147, 166, 166, 2200, 166, 2505, 166, 4310, 4865, 5656, 5992, 5672, 166, 5199, 135, 3023,
                        2994, 4472, 166, 166, 166, 2019, 4319, 3472, 166, 166, 166, 29, 206, 3944, 3027, 5804,
                        4731, 5449, 166, 2825, 3310, 166, 6172, 5202, 166, 2516, 3644, 4557, 166, 166, 166, 166,
                        2671, 4427, 3432, 3276, 5584, 5536, 4645, 3202, 166, 2612, 166, 4249, 2425, 3259, 4622, 166,
                        2411, 4303, 4206, 166, 166, 166, 3734, 6063, 118, 166, 166, 3641, 166, 166, 166, 4937,
                        1871, 3421, 2208, 166, 166, 166, 166, 4881, 166, 166, 166, 166, 3298, 166, 61, 166,
                        166, 166, 3293, 6145, 71, 3619, 166, 166, 3383, 1624, 320, 2187, 4113, 166, 166, 166,
                        166, 166, 5080, 2344, 5625, 2358, 1621, 4230, 5579, 5359, 295, 4248, 5267, 3883, 6124, 187,
                        5112, 2122, 166, 166, 166, 5142, 6004, 166, 5322, 6175, 3639, 3182, 4425, 166, 175, 166,
                        166, 166, 5778, 3939, 3484, 166, 166, 5832, 5248, 5935, 4467, 5858, 166, 5038, 166, 166,
                        3102, 166, 4880, 166, 166, 166, 166, 3418, 1666, 5338, 3680, 5291, 4441, 3385, 166, 5733,
                        4503, 2774, 166, 2631, 4153, 166, 2000, 166, 166, 5345, 166, 166, 4298, 1804, 4707, 166,
                        1613, 1952, 2111, 166, 166, 166, 166, 166, 2897, 166, 166, 4044, 166, 166, 166, 166,
                        2863, 5475, 166, 166, 166, 1704, 166, 3609, 2782, 2018, 166, 5361, 166, 3694, 3733, 166,
                        2785, 1969, 166, 166, 2834, 1868, 3779, 1877, 60, 166, 4143, 3902, 166, 4361, 3188, 2498,
                        6009, 166, 115, 166, 3138, 166, 4575, 6080, 133, 2030, 166, 166, 166, 2306, 2136, 3043,
                        3447, 2142, 166, 3799, 1646, 5269, 3640, 166, 2674, 5502, 166, 5467, 166, 5069, 166, 166,
                        4654, 4581, 5274, 5036, 4364, 166, 3115, 166, 2128, 4544, 5433, 2086, 2584, 4413, 166, 166,
                        5385, 166, 234, 166, 1625, 166, 166, 166, 5139, 2511, 4974, 2766, 166, 166, 166, 2095,
                        3990, 217, 166, 2988, 4061, 166, 209, 4883, 166, 166, 166, 166, 166, 4326, 166, 5465,
                        2859, 166, 2887, 166, 2231, 166, 1658, 166, 2246, 166, 1844, 166, 166, 3087, 2871, 3872,
                        1660, 48, 166, 166, 3622, 166, 1709, 166, 166, 6177, 6173, 166, 3569, 166, 166, 166,
                        241, 3660, 3631, 166, 166, 5319, 5141, 174, 166, 166, 4412, 166, 5145, 166, 1919, 166,
                        5276, 166, 2385, 166, 1618, 166, 166, 2501, 166, 166, 1734, 5966, 3145, 166, 1690, 4025,
                        1664, 4559, 2433, 2392, 3552, 4006, 1896, 166, 166, 2546, 4450, 5396, 4221, 4046, 166, 166,
                        2642, 166, 4448, 166, 2784, 3480, 4807, 166, 166, 3534, 166, 166, 5272, 166, 166, 2831,
                        4263, 166, 166, 166, 166, 4414, 5628, 3486, 166, 3748, 166, 4598, 3719, 3598, 3611, 166,
                        4792, 5059, 4110, 166, 2656, 166, 166, 84, 5429, 166, 166, 166, 281, 1955, 166, 166,
                        166, 3616, 4997, 166, 166, 166, 166, 3230, 166, 166, 166, 166, 166, 166, 77, 166,
                        166, 166, 1800, 166, 4236, 166, 166, 166, 166, 166, 5757, 2530, 1662, 166, 4607, 1659,
                        166, 1685, 3341, 166, 1699, 4058, 3407, 1854, 4417, 3034, 166, 166, 166, 166, 5568, 166,
                        3206, 166, 5529, 166, 166, 166, 2116, 3487, 144, 166, 166, 166, 5523, 5373, 5321, 166,
                        6064, 2921, 166, 1696, 2473, 166, 166, 3716, 5689, 166, 4608, 3879, 166, 166, 166, 2156,
                        166, 4358, 2446, 166, 3958, 166, 5520, 4340, 4848, 166, 3285, 166, 2665, 166, 3459, 1905,
                        5115, 68, 5730, 166, 3127, 5029, 4370, 166, 3753, 166, 3674, 6025, 4490, 166, 4183, 166,
                        94, 166, 166, 4051, 3766, 3140, 4907, 3857, 166, 166, 4596, 166, 3888, 3040, 2507, 5643,
                        166, 166, 4311, 2618, 5582, 166, 166, 3678, 166, 1988, 166, 166, 4464, 166, 166, 166,
                        166, 4278, 3677, 2173, 5256, 166, 166, 5162, 166, 5178, 1644, 5094, 166, 2557, 5506, 166,
                        166, 166, 4927, 5348, 1797, 166, 166, 39, 166, 3866, 3655, 236, 5403, 2175, 3361, 166,
                        1976, 5993, 226, 166, 4643, 166, 5339, 4098, 2653, 4969, 166, 3346, 4984, 4635, 166, 166,
                        166, 166, 4981, 188, 166, 166, 28, 4088, 166, 166, 166, 25, 3663, 2696, 166, 4679,
                        5114, 5802, 166, 166, 166, 166, 166, 3810, 5749, 166, 1673, 4276, 166, 3756, 4184, 166,
                        5630, 166, 166, 166, 4531, 212, 5663, 166, 166, 2746, 166, 5386, 3618, 3594, 1887, 166,
                        166, 5443, 166, 1726, 4094, 5065, 4756, 166, 166, 5308, 5225, 2081, 166, 166, 3064, 166,
                        166, 1981, 3637, 4355, 1626, 166, 166, 4686, 166, 5793, 180, 5066, 2938, 3819, 4904, 3601,
                        166, 166, 2495, 5025, 5768, 2621, 4650, 3041, 166, 5897, 3633, 166, 166, 4375, 166, 5714,
                        1667, 3273, 3950, 1668, 166, 5855, 166, 2364, 166, 1881, 166, 2646, 5460, 166, 2770, 4951,
                        5414, 166, 4442, 2113, 5726, 298, 5934, 2053, 166, 166, 4053, 166, 166, 4514, 4697, 166,
                        166, 5198, 2707, 166, 5605, 166, 166, 5218, 2596, 166, 2110, 166, 1806, 2160, 166, 166,
                        2212, 166, 3636, 166, 166, 4377, 4021, 3707, 4502, 166, 4195, 166, 166, 166, 4108, 3725,
                        3676, 166, 2084, 166, 166, 166, 166, 4216, 166, 166, 6156, 166, 2896, 166, 166, 166,
                        166, 166, 166, 3826, 2870, 3793, 166, 166, 5927, 166, 2759, 166, 4613, 2297, 5638, 166,
                        2842, 5031, 4793, 5184, 166, 166, 2008, 166, 257, 2881, 117, 6051, 3044, 4079, 2833, 166,
                        6117, 166, 3236, 5469, 166, 166, 2874, 6076, 166, 1799, 80, 41, 166, 1864, 166, 5709,
                        1611, 5026, 5176, 168, 3269, 4081, 166, 166, 1970, 4550, 166, 4250, 4101, 4565, 5950, 5845,
                        97, 4064, 166, 5394, 4374, 4343, 166, 166, 4658, 3248, 166, 208, 1735, 4047, 2843, 166,
                        166, 166, 166, 2794, 166, 166, 5844, 166, 166, 3094, 2177, 5436, 3646, 166, 3564, 4682,
                        166, 5948, 5835, 162, 2059, 5151, 2034, 1926, 5941, 5903, 5177, 166, 166, 166, 4801, 3439,
                        1780, 166, 166, 3280, 3434, 166, 166, 4498, 5565, 4043, 166, 4432, 4722, 3959, 166, 3746,
                        166, 166, 177, 166, 166, 2748, 166, 4483, 166, 166, 4144, 166, 166, 166, 166, 2066,
                        2915, 166, 2049, 2130, 4684, 166, 49, 3506, 5391, 166, 2590, 6103, 1714, 2410, 3053, 3837,
                        4301, 166, 3255, 2644, 166, 166, 4014, 166, 2475, 4788, 2876, 166, 166, 166, 166, 166,
                        166, 4140, 166, 166, 321, 166, 1966, 166, 166, 2855, 3111, 3800, 166, 4446, 2551, 166,
                        166, 166, 2824, 166, 166, 166, 2164, 3010, 2226, 166, 4857, 166, 2582, 5118, 4582, 5917,
                        166, 166, 3338, 3482, 3328, 166, 4817, 166, 5371, 3830, 166, 3009, 1633, 3329, 4052, 166,
                        3701, 4983, 4500, 4487, 4878, 166, 166, 5482, 3544, 166, 3057, 2026, 4398, 2847, 3532, 3262,
                        3399, 166, 166, 166, 4478, 4167, 166, 3411, 2599, 5362, 166, 2711, 166, 166, 166, 166,
                        3452, 2522, 5586, 5548, 3279, 2538, 166, 166, 166, 4161, 166, 2123, 166, 166, 2660, 166,
                        166, 1706, 166, 15, 3537, 5051, 5869, 166, 3025, 166, 4447, 3744, 120, 166, 166, 166,
                        204, 2810, 166, 5124, 2376, 5306, 166, 166, 4493, 166, 166, 166, 5289, 6046, 166, 2762,
                        2541, 1857, 2467, 5163, 166, 166, 166, 166, 5830, 166, 2172, 3359, 166, 2928, 166, 166,
                        166, 6129, 166, 5445, 166, 166, 5924, 6144, 166, 102, 166, 166, 1678, 166, 4491, 5705,
                        166, 1753, 166, 3873, 5725, 4145, 1909, 166, 2155, 166, 166, 1848, 3315, 1874, 166, 4945,
                        2524, 166, 3263, 2362, 1785, 166, 166, 166, 152, 2102, 5723, 5131, 5754, 4032, 4029, 166,
                        4295, 3391, 166, 166, 166, 5282, 1747, 3159, 2235, 5583, 1786, 3630, 6111, 2974, 4797, 3623,
                        166, 2071, 4929, 166, 2603, 3964, 3378, 166, 166, 2654, 151, 3940, 4527, 4518, 166, 2430,
                        1884, 3812, 166, 2867, 166, 166, 166, 2756, 5418, 166, 2354, 4606, 166, 2153, 166, 4855,
                        166, 166, 1720, 166, 3213, 3926, 166, 5158, 4349, 166, 4828, 166, 166, 2031, 166, 2300,
                        166, 166, 166, 2211, 4954, 3121, 4754, 2485, 166, 166, 166, 3593, 166, 2718, 5317, 2765,
                        5120, 166, 2527, 166, 1994, 5947, 166, 166, 166, 6085, 2302, 100, 79, 2982, 3705, 2180,
                        2043, 166, 1872, 1671, 166, 3729, 166, 4944, 3665, 2217, 2119, 166, 5615, 166, 1620, 166,
                        166, 166, 166, 35, 3913, 2760, 166, 3688, 3672, 4042, 166, 166, 5117, 4227, 166, 4445,
                        2458, 3803, 4554, 4988, 166, 166, 3141, 3491, 166, 166, 166, 166, 5095, 4668, 5567, 166,
                        166, 2885, 1790, 2996, 166, 166, 166, 166, 3737, 166, 2470, 166, 166, 4339, 166, 166,
                        166, 4920, 166, 166, 3697, 5471, 166, 166, 3538, 4558, 3467, 5262, 5609, 3858, 166, 166,
                        5007, 2780, 2791, 2236, 5668, 3134, 166, 166, 5776, 3470, 3291, 166, 2532, 166, 166, 166,
                        3805, 264, 166, 3227, 166, 166, 166, 2334, 166, 5087, 101, 166, 3634, 58, 2813, 166,
                        166, 166, 3222, 4704, 4488, 4508, 5459, 2117, 5873, 166, 1828, 166, 166, 166, 166, 166,
                        2105, 166, 5613, 5761, 2920, 3098, 166, 166, 3277, 166, 166, 166, 166, 83, 166, 166,
                        166, 3967, 166, 5574, 166, 4985, 30, 3426, 166, 179, 3014, 4015, 246, 2556, 4449, 3723,
                        5611, 3436, 166, 4240, 3642, 166, 4536, 2048, 5810, 166, 1971, 166, 5557, 5323, 5022, 191,
                        5492, 166, 4837, 4426, 2537, 2271, 3177, 5674, 166, 2796, 1995, 166, 3906, 166, 4403, 3862,
                        4716, 2406, 3948, 4670, 4309, 166, 2575, 5358, 2951, 166, 3666, 3612, 5577, 4579, 4743, 166,
                        6072, 6036, 4563, 2586, 166, 5836, 166, 166, 5752, 166, 3563, 166, 2909, 3251, 92, 166,
                        4711, 4149, 166, 166, 3052, 5122, 2904, 2635, 1990, 166, 166, 166, 166, 166, 166, 166,
                        166, 4213, 166, 3103, 3142, 2683, 6105, 2209, 3175, 4215, 166, 166, 166, 166, 166, 166,
                        166, 5303, 4075, 5374, 166, 4174, 4154, 1895, 4538, 2764, 166, 5817, 6113, 4033, 166, 6090,
                        166, 2990, 166, 3164, 166, 166, 166, 247, 166, 6083, 3412, 166, 5738, 166, 3599, 166,
                        1904, 2162, 2547, 3960, 166, 166, 3154, 55, 166, 5991, 4921, 2879, 166, 166, 5347, 166,
                        166, 166, 2712, 4787, 166, 1908, 166, 166, 166, 3184, 166, 166, 166, 4572, 3846, 3657,
                        166, 166, 5481, 166, 166, 3397, 1856, 4978, 166, 3900, 3570, 3802, 166, 166, 2075, 4408,
                        166, 6079, 2313, 166, 166, 5756, 166, 166, 2070, 166, 166, 3137, 166, 166, 3686, 166,
                        166, 166, 166, 67, 5019, 166, 1742, 166, 5354, 166, 5149, 166, 2931, 4946, 6006, 166,
                        166, 2865, 4902, 3029, 1722, 3449, 166, 1987, 166, 62, 5626, 166, 166, 166, 2670, 1657,
                        5599, 3056, 166, 3791, 5020, 166, 1979, 4437, 1899, 166, 166, 196, 2636, 166, 143, 3475,
                        4317, 2512, 2415, 5033, 5024, 2112, 2864, 3551, 166, 1688, 33, 4585, 3648, 4399, 166, 166,
                        166, 166, 166, 1824, 166, 166, 166, 166, 166, 166, 4513, 166, 2478, 4407, 166, 166,
                        2492, 4130, 4318, 2980, 5746, 166, 2606, 4063, 4123, 166, 255, 166, 166, 4680, 166, 3586,
                        5975, 3935, 166, 5528, 166, 3158, 166, 166, 2614, 5035, 166, 3488, 3214, 166, 166, 166,
                        5413, 3713, 166, 5875, 4329, 5250, 166, 166, 3741, 166, 54, 1885, 3839, 166, 4924, 166,
                        166, 166, 4158, 166, 166, 2152, 1661, 166, 166, 4327, 166, 3933, 166, 5666, 166, 166,
                        2580, 166, 3404, 4111, 2862, 4438, 166, 166, 4072, 166, 166, 3938, 2958, 4302, 166, 3851,
                        166, 268, 166, 166, 1975, 222, 3204, 3438, 4616, 166, 4275, 3101, 2648, 3989, 5215, 166,
                        4229, 166, 5440, 166, 5093, 2639, 166, 166, 4439, 166, 2316, 4239, 166, 166, 166, 166,
                        166, 1817, 4486, 166, 3272, 166, 166, 4085, 2078, 2902, 166, 166, 166, 4381, 1853, 3054,
                        166, 166, 5005, 2669, 166, 2856, 2706, 166, 166, 166, 4185, 166, 1748, 166, 166, 166,
                        5771, 166, 166, 3915, 166, 166, 2205, 6122, 166, 166, 1632, 5400, 166, 2477, 4740, 166,
                        166, 166, 1802, 166, 2472, 3953, 166, 1849, 2604, 3780, 2560, 4786, 2566, 3576, 166, 4768,
                        166, 1951, 251, 5068, 166, 166, 166, 2619, 166, 166, 166, 5432, 166, 166, 5260, 5758,
                        3908, 166, 4141, 166, 5777, 166, 166, 166, 166, 166, 3961, 5143, 166, 3889, 3747, 3743,
                        166, 2818, 166, 166, 166, 3867, 166, 166, 3742, 4763, 2948, 5533, 166, 3966, 3555, 3843,
                        3503, 6005, 166, 4687, 2790, 4479, 5828, 3769, 5688, 166, 166, 166, 166, 3109, 166, 166,
                        166, 166, 4574, 81, 166, 166, 4576, 3369, 166, 166, 166, 4207, 166, 5072, 2210, 166,
                        184, 166, 4673, 166, 166, 166, 166, 166, 166, 1628, 3590, 1916, 4784, 4970, 166, 1832,
                        166, 166, 3584, 3384, 166, 166, 2880, 1783, 166, 166, 166, 166, 6115, 6121, 2157, 5428,
                        5859, 4861, 5635, 4331, 5839, 4223, 313, 166, 166, 6152, 2168, 166, 4112, 6089, 6012, 166,
                        5294, 3207, 166, 166, 4884, 166, 4655, 166, 166, 166, 1743, 166, 4077, 166, 4631, 166,
                        166, 2957, 1945, 4936, 166, 166, 5389, 166, 166, 5955, 166, 166, 1639, 2207, 4129, 166,
                        3582, 5560, 6147, 3088, 166, 166, 4529, 5259, 3118, 166, 3106, 2853, 166, 1845, 5660, 166,
                        3325, 3973, 2461, 2163, 166, 3083, 4190, 166, 166, 5505, 166, 166, 3226, 5507, 109, 6141,
                        3991, 166, 4939, 166, 166, 5889, 3986, 166, 3664, 4353, 2056, 166, 5071, 166, 166, 4376,
                        166, 1958, 2028, 166, 166, 1793, 166, 5252, 3536, 166, 166, 3525, 3580, 166, 166, 166,
                        1782, 5174, 2011, 1826, 3352, 3231, 166, 166, 4986, 2068, 2801, 166, 2500, 166, 5061, 166,
                        2263, 2632, 1993, 166, 2715, 4424, 166, 166, 6042, 4661, 166, 5074, 5479, 4822, 166, 166,
                        166, 166, 5600, 5853, 166, 1907, 166, 166, 166, 3808, 166, 5997, 5032, 4605, 166, 1732,
                        166, 166, 166, 3015, 5454, 166, 166, 166, 3806, 5444, 2238, 1946, 166, 166, 3221, 4922,
                        166, 6092, 166, 166, 4007, 166, 3425, 4282, 2571, 166, 1749, 166, 166, 38, 4744, 4900,
                        4257, 214, 5687, 166, 2490, 2979, 2924, 166, 4714, 219, 5344, 3836, 3302, 78, 1984, 2986,
                        2960, 166, 2869, 3507, 3335, 4967, 2892, 2723, 4849, 5070, 166, 166, 4629, 3815, 166, 4453,
                        4760, 166, 3224, 130, 166, 166, 166, 166, 166, 3408, 2494, 2691, 166, 4325, 2932, 5165,
                        5573, 166, 4769, 166, 5411, 5637, 2050, 166, 166, 2305, 166, 166, 4834, 24, 4693, 3554,
                        2491, 1738, 166, 166, 166, 23, 2758, 3072, 2564, 4800, 5537, 3545, 4133, 166, 166, 166,
                        5982, 166, 203, 166, 166, 290, 185, 166, 3774, 1929, 3379, 166, 166, 166, 166, 3002,
                        166, 3738, 166, 166, 3344, 4942, 5353, 2777, 2839, 4712, 1830, 2664, 166, 5884, 3516, 166,
                        5494, 4169, 2391, 3319, 166, 166, 5918, 2597, 166, 4821, 2787, 5719, 166, 166, 166, 1687,
                        6148, 3257, 254, 166, 5180, 6153, 5964, 306, 166, 6123, 166, 5208, 166, 3163, 5938, 1736,
                        166, 2502, 4910, 166, 166, 2549, 166, 2900, 3632, 3270, 166, 2082, 5953, 166, 107, 5750,
                        166, 166, 166, 5527, 1751, 4168, 2950, 166, 2659, 166, 4189, 1943, 2595, 166, 4191, 166,
                        166, 166, 166, 2998, 2296, 5221, 3617, 166, 5435, 2451, 2009, 3005, 2242, 3768, 3658, 166,
                        166, 166, 166, 166, 2481, 2256, 166, 166, 4074, 166, 3120, 166, 4409, 1759, 166, 166,
                        1679, 3659, 3499, 5219, 4501, 3082, 2047, 166, 166, 166, 4560, 2768, 5251, 166, 166, 166,
                        2437, 3993, 3215, 2447, 166, 166, 166, 2993, 4963, 166, 3045, 166, 166, 166, 166, 166,
                        166, 166, 5521, 166, 166, 4868, 166, 3895, 166, 6131, 3949, 3306, 3785, 166, 166, 4895,
                        4831, 166, 1772, 166, 166, 5928, 166, 2137, 4805, 2462, 310, 2667, 3561, 166, 166, 2312,
                        4931, 5255, 166, 166, 166, 5670, 166, 2285, 166, 4672, 5310, 166, 2103, 2174, 166, 166,
                        166, 166, 5417, 166, 4726, 4203, 166, 166, 166, 5581, 166, 5665, 166, 166, 5747, 166,
                        166, 2509, 1973, 2749, 5463, 166, 166, 4567, 5014, 166, 3322, 3051, 166, 4090, 166, 3709,
                        3887, 3478, 166, 166, 166, 166, 3565, 3934, 166, 32, 166, 166, 166, 2239, 166, 3947,
                        3849, 166, 2022, 166, 2169, 166, 4691, 98, 166, 3804, 4155, 1640, 4002, 166, 2138, 1739,
                        3730, 5970, 2274, 4873, 3119, 166, 4925, 3577, 3699, 4049, 3982, 166, 5161, 1744, 166, 166,
                        166, 5704, 4979, 2686, 5383, 5744, 2289, 166, 166, 166, 3927, 2539, 166, 166, 166, 2585,
                        166, 4723, 3755, 4509, 166, 4961, 2194, 2535, 166, 176, 166, 4494, 166, 4171, 166, 266,
                        166, 3454, 5369, 166, 166, 5899, 5284, 166, 3607, 3566, 5514, 166, 1843, 166, 3997, 4599,
                        2743, 166, 2857, 2497, 2751, 166, 166, 166, 3511, 5742, 166, 166, 166, 4504, 166, 166,
                        166, 5082, 4401, 166, 166, 5431, 166, 166, 1949, 4539, 166, 166, 4852, 166, 166, 3457,
                        166, 3433, 4669, 166, 1692, 2454, 3258, 6159, 166, 166, 166, 166, 166, 2788, 4350, 3249,
                        3816, 4893, 166, 4846, 166, 4993, 1708, 4138, 166, 2895, 2891, 166, 1860, 166, 2480, 1927,
                        3853, 166, 166, 166, 5100, 166, 3143, 5159, 166, 4286, 5182, 5246, 4975, 166, 2905, 166,
                        4917, 5102, 2044, 6016, 5673, 2005, 5090, 166, 4634, 3333, 166, 5702, 3413, 1762, 6094, 4284,
                        4431, 2641, 166, 4463, 5691, 166, 166, 3442, 3473, 4192, 2046, 166, 3838, 166, 3217, 3349,
                        166, 2243, 166, 3490, 166, 166, 166, 5922, 166, 166, 166, 4885, 1798, 2884, 2750, 5004,
                        2741, 166, 166, 5649, 166, 4410, 166, 166, 3382, 166, 166, 1913, 1703, 5532, 3770, 166,
                        5116, 2645, 2634, 4357, 5901, 166, 166, 5538, 166, 166, 166, 6028, 166, 166, 5840, 4102,
                        2704, 2091, 5287, 166, 4757, 2282, 166, 2650, 3528, 64, 253, 3732, 166, 166, 166, 166,
                        166, 3465, 166, 166, 166, 5848, 3110, 111, 166, 166, 3403, 2926, 6030, 3366, 1948, 4430,
                        5509, 3250, 3972, 2587, 3579, 166, 6048, 250, 5275, 4242, 2615, 3112, 3558, 166, 166, 2342,
                        166, 5157, 1917, 2733, 5647, 1934, 5675, 166, 3981, 2923, 5213, 5326, 37, 166, 5288, 3069,
                        166, 1923, 5755, 166, 166, 166, 1888, 166, 6041, 5895, 5376, 3727, 3901, 166, 5589, 166,
                        166, 4609, 166, 166, 166, 4706, 166, 4482, 1622, 166, 171, 166, 166, 4646, 4151, 2755,
                        4614, 166, 2072, 5409, 4469, 1647, 4434, 4633, 1915, 166, 3615, 4808, 166, 3388, 166, 5280,
                        2731, 166, 166, 2417, 166, 14, 166, 4533, 5126, 166, 2778, 3022, 166, 166, 166, 4830,
                        4764, 166, 166, 166, 4982, 166, 4265, 166, 2466, 5678, 147, 1883, 166, 166, 166, 114,
                        4000, 2427, 3597, 166, 4853, 5981, 166, 2023, 2519, 166, 1937, 2221, 4676, 166, 4522, 5716,
                        166, 2432, 5731, 166, 6020, 6163, 4351, 2442, 4380, 166, 4390, 1882, 6139, 4246, 262, 166,
                        1676, 5781, 2352, 1956, 200, 166, 166, 5800, 6184, 166, 2355, 149, 5962, 5524, 4238, 166,
                        5150, 166, 5888, 2423, 166, 5739, 3192, 4142, 166, 166, 166, 3201, 161, 4460, 2459, 158,
                        166, 166, 166, 166, 2689, 166, 166, 166, 166, 1889, 166, 166, 3374, 166, 70, 166,
                        2772, 166, 2995, 166, 2384, 4989, 166, 3299, 166, 166, 166, 166, 3614, 3645, 3415, 3160,
                        1727, 3735, 5201, 1693, 3531, 166, 166, 1776, 3871, 166, 166, 166, 166, 86, 3553, 166,
                        166, 166, 3392, 166, 166, 2232, 166, 4977, 2333, 3394, 2875, 2027, 5736, 166, 1719, 166,
                        4952, 2061, 2150, 5526, 166, 4637, 166, 4333, 166, 166, 4733, 4809, 3911, 166, 3460, 166,
                        5355, 3126, 4181, 4436, 300, 166, 3841, 166, 4770, 126, 5654, 166, 166, 166, 1730, 166,
                        166, 166, 5610, 166, 6002, 2197, 3807, 6109, 166, 166, 166, 166, 166, 5395, 4004, 166,
                        46, 166, 166, 2570, 4736, 5318, 4247, 166, 166, 166, 2293, 3031, 4591, 166, 245, 166,
                        5510, 1616, 3117, 4163, 166, 166, 4759, 3462, 4819, 4947, 166, 3128, 5946, 2278, 2969, 166,
                        166, 5183, 166, 166, 1729, 173, 2448, 166, 230, 2971, 166, 166, 5397, 166, 4093, 3348,
                        1866, 4280, 166, 6067, 3794, 166, 166, 166, 4729, 166, 3456, 166, 2394, 166, 4953, 166,
                        166, 2258, 4863, 166, 166, 4060, 166, 5468, 305, 166, 6134, 166, 166, 2326, 166, 3453,
                        2167, 2845, 166, 166, 166, 5597, 166, 166, 166, 166, 5462, 2809, 5994, 2899, 166, 166,
                        166, 5153, 166, 166, 1638, 166, 166, 4938, 3795, 166, 3842, 166, 166, 166, 2769, 3194,
                        166, 4745, 5508, 5604, 3910, 166, 166, 4147, 3239, 166, 166, 3548, 3859, 2092, 166, 2705,
                        166, 166, 3625, 4131, 166, 3513, 166, 166, 2987, 4555, 3107, 166, 166, 166, 166, 5713,
                        4698, 3079, 166, 5342, 166, 166, 2673, 2517, 2745, 1795, 166, 166, 166, 166, 166, 166,
                        2463, 166, 166, 2445, 5425, 6138, 166, 2687, 3254, 5871, 166, 2387, 4300, 166, 166, 3529,
                        1996, 166, 2369, 3818, 6126, 1615, 2643, 65, 4297, 166, 5324, 3311, 3852, 166, 3868, 4199,
                        3978, 166, 166, 166, 5466, 166, 166, 244, 166, 5929, 6157, 2390, 5639, 2267, 2073, 4610,
                        5774, 2521, 4556, 166, 4545, 4307, 2426, 2450, 166, 5783, 4968, 6176, 4156, 166, 166, 4126,
                        3549, 166, 3581, 5701, 3234, 166, 4013, 1879, 166, 6104, 5874, 166, 166, 3485, 4279, 2528,
                        5576, 166, 3992, 166, 3980, 4934, 166, 2176, 4228, 5164, 3784, 1933, 4120, 5055, 166, 166,
                        5015, 166, 166, 166, 2310, 1754, 166, 6087, 166, 166, 4548, 5268, 2930, 166, 3656, 166,
                        3042, 5229, 166, 4016, 2195, 166, 166, 166, 199, 1745, 3717, 166, 166, 74, 2668, 252,
                        4124, 4657, 5223, 166, 2186, 3628, 166, 166, 166, 4222, 3114, 2841, 5103, 3171, 5135, 166,
                        166, 2273, 166, 3899, 5332, 5842, 3575, 2579, 2431, 2464, 2229, 3604, 4561, 2977, 2815, 166,
                        3916, 166, 5825, 166, 1694, 166, 4030, 166, 5841, 166, 3881, 1831, 166, 5525, 3011, 166,
                        5535, 5217, 316, 4116, 166, 166, 2204, 166, 3136, 3650, 166, 5813, 1875, 4511, 4475, 166,
                        1999, 166, 2277, 166, 3024, 5484, 5546, 166, 3988, 5676, 166, 2213, 2264, 5214, 166, 4940,
                        5974, 166, 4750, 6077, 166, 1652, 3148, 166, 166, 166, 166, 2554, 166, 6167, 5257, 5300,
                        166, 166, 166, 166, 5408, 166, 166, 3402, 2141, 166, 4663, 5633, 3312, 166, 2814, 4930,
                        1959, 166, 166, 166, 3861, 166, 166, 302, 2624, 166, 166, 166, 1629, 1724, 166, 3909,
                        5281, 166, 2001, 4395, 5352, 4428, 2694, 4850, 166, 166, 5242, 5910, 166, 166, 166, 166,
                        166, 3212, 166, 2045, 166, 166, 166, 166, 166, 166, 3017, 4960, 4456, 166, 5616, 6093,
                        2151, 166, 166, 166, 315, 3381, 166, 166, 166, 4330, 166, 6158, 4721, 6075, 166, 166,
                        166, 4543, 2303, 166, 166, 3301, 166, 5000, 3929, 2543, 3437, 166, 166, 166, 3422, 166,
                        5987, 5729, 2428, 166, 4035, 5588, 3714, 3834, 5264, 5743, 166, 3305, 4886, 6107, 5156, 166,
                        166, 166, 166, 166, 1672, 5849, 5827, 5049, 6101, 2178, 2420, 3289, 166, 166, 4274, 6017,
                        2257, 166, 4172, 3451, 2367, 2382, 166, 2964, 4918, 3241, 2347, 6082, 99, 2383, 166, 4454,
                        163, 2460, 165, 304, 1818, 5580, 166, 312, 5790, 293, 5794, 5519, 5083, 3360, 5748, 166,
                        3750, 5034, 166, 166, 166, 1863, 3168, 166, 166, 166, 5111, 166, 166, 166, 166, 2183,
                        4510, 166, 166, 3495, 4382, 4235, 4462, 166, 4056, 5885, 17, 5028, 1614, 6038, 166, 2488,
                        5632, 3089, 166, 1940, 66, 4039, 3999, 235, 166, 166, 3829, 3954, 166, 2365, 269, 166,
                        166, 166, 166, 166, 166, 4418, 1796, 4709, 2004, 166, 3596, 5786, 166, 2819, 4624, 3152,
                        2968, 2838, 166, 5575, 1767, 5603, 166, 4386, 5890, 166, 1768, 4201, 3560, 166, 166, 166,
                        2184, 2262, 2966, 2716, 1765, 2611, 2983, 166, 4164, 4084, 142, 5314, 166, 166, 4071, 166,
                        2578, 2849, 3600, 166, 166, 166, 166, 5401, 4814, 3431, 166, 5088, 5084, 198, 166, 3578,
                        3764, 166, 2097, 166, 166, 5390, 4443, 166, 3166, 166, 4816, 166, 166, 166, 166, 3130,
                        5963, 1788, 2129, 1837, 4100, 6128, 166, 4586, 5945, 4772, 166, 5741, 3151, 3247, 5645, 4507,
                        5833, 3904, 6013, 2506, 3050, 4175, 1705, 3019, 166, 5942, 166, 2418, 3430, 2230, 5745, 166,
                        2093, 166, 166, 166, 166, 4666, 3246, 192, 2010, 4003, 3533, 5851, 166, 3621, 3684, 3066,
                        166, 166, 166, 5073, 3856, 166, 166, 2224, 166, 2637, 4270, 166, 166, 5679, 166, 5792,
                        5850, 166, 2589, 3060, 2196, 3476, 3150, 2025, 166, 166, 166, 2657, 166, 3685, 3790, 5587,
                        2817, 3692, 166, 166, 166, 2359, 2260, 5896, 2158, 119, 2816, 5753, 166, 2739, 5772, 166,
                        2919, 2147, 1985, 4271, 4838, 4991, 166, 166, 166, 5244, 166, 319, 166, 166, 2779, 4732,
                        4994, 5424, 166, 166, 3968, 3049, 3393, 4473, 4959, 5967, 5864, 5170, 4209, 166, 4810, 4815,
                        4205, 2339, 5023, 2279, 5050, 166, 5837, 132, 166, 166, 166, 2247, 21, 4775, 166, 166,
                        5286, 166, 4170, 4099, 4803, 5767, 166, 166, 166, 5811, 2240, 5699, 2499, 166, 4802, 166,
                        5785, 166, 166, 166, 3181, 3435, 166, 3339, 166, 5669, 3865, 2249, 5002, 166, 4694, 5461,
                        4753, 166, 3157, 166, 1960, 166, 166, 166, 2440, 166, 5818, 5534, 2439, 1717, 166, 3789,
                        2959, 166, 2943, 166, 2576, 166, 2002, 2007, 1819, 3256, 4402, 5311, 3832, 160, 166, 166,
                        2803, 166, 3264, 166, 5863, 166, 2017, 166, 2798, 166, 166, 166, 166, 5607, 4965, 166,
                        166, 166, 4537, 4378, 5944, 3494, 5457, 5602, 1942, 5900, 5780, 4411, 5147, 166, 4966, 2115,
                        155, 2827, 1980, 5063, 166, 285, 5912, 3304, 2963, 5179, 3220, 166, 166, 166, 2190, 3708,
                        5476, 1944, 2366, 3893, 166, 166, 166, 3759, 166, 5434, 2740, 1707, 4244, 5426, 166, 166,
                        166, 3155, 166, 4285, 166, 166, 166, 166, 5721, 166, 3833, 6001, 301, 166, 166, 2574,
                        186, 2724, 166, 1873, 3667, 166, 5216, 166, 2935, 2100, 4987, 166, 2284, 166, 166, 2911,
                        3828, 4009, 166, 2065, 166, 5496, 6130, 5563, 4387, 166, 3771, 3469, 2989, 2222, 4577, 3965,
                        4296, 2975, 3813, 3240, 166, 4780, 4481, 3387, 2338, 166, 6183, 166, 166, 166, 166, 166,
                        2675, 1761, 2600, 5167, 3170, 4773, 2165, 5166, 166, 2223, 4642, 166, 166, 4540, 166, 166,
                        166, 3897, 166, 2483, 1809, 5477, 3844, 4067, 2508, 2275, 166, 166, 166, 166, 166, 3497,
                        5458, 166, 249, 2956, 166, 4651, 166, 283, 166, 166, 4955, 4062, 2315, 2304, 3261, 2361,
                        4791, 4389, 1997, 166, 3455, 166, 166, 166, 166, 166, 166, 4746, 5695, 5296, 105, 1841,
                        3368, 166, 166, 166, 5228, 166, 3496, 4423, 2024, 3907, 4774, 166, 166, 166, 166, 166,
                        2294, 2193, 166, 166, 166, 166, 166, 166, 166, 166, 4393, 166, 166, 2127, 166, 4573,
                        166, 5350, 166, 5016, 3372, 166, 5653, 166, 5972, 4719, 166, 166, 166, 166, 166, 5370,
                        166, 6142, 166, 166, 3691, 2828, 166, 2601, 166, 2937, 2060, 3654, 3097, 2341, 5325, 4568,
                        4096, 2776, 166, 2946, 166, 166, 166, 5843, 1777, 5295, 2837, 4261, 4397, 5006, 5808, 4866,
                        166, 1713, 5732, 2954, 166, 166, 27, 166, 4308, 5629, 2652, 2434, 4474, 166, 4928, 166,
                        4727, 3811, 166, 166, 5234, 166, 6010, 166, 4911, 166, 4570, 166, 6000, 3450, 5304, 3919,
                        166, 166, 4008, 3942, 166, 272, 2363, 2064, 3595, 3505, 166, 166, 3957, 1695, 2452, 4659,
                        166, 1792, 166, 131, 5968, 166, 3731, 3905, 4115, 166, 166, 2468, 166, 2727, 166, 3526,
                        4724, 166, 4388, 3149, 5539, 5092, 4440, 6162, 166, 166, 193, 4429, 2493, 166, 166, 3683,
                        166, 6029, 166, 277, 166, 166, 166, 5240, 2408, 166, 309, 2561, 210, 166, 5200, 166,
                        166, 166, 1930, 5692, 2697, 166, 166, 166, 3330, 5331, 3860, 166, 166, 4335, 166, 50,
                        3605, 4289, 1763, 166, 166, 166, 166, 3521, 166, 166, 166, 3668, 166, 166, 166, 166,
                        166, 3271, 1656, 166, 166, 4782, 166, 2962, 166, 5907, 166, 3245, 3375, 2944, 5933, 166,
                        166, 5406, 5655, 3139, 5423, 166, 4359, 5231, 2548, 166, 3831, 2858, 5488, 166, 5824, 166,
                        166, 166, 3885, 4372, 166, 166, 4024, 166, 4811, 2970, 166, 4219, 211, 166, 3471, 166,
                        166, 166, 166, 3854, 166, 3358, 2877, 166, 166, 5205, 2804, 166, 166, 166, 4452, 166,
                        166, 166, 166, 3776, 166, 166, 3075, 4208, 166, 5623, 1974, 166, 2647, 166, 3235, 166,
                        166, 166, 5211, 166, 166, 4304, 2206, 166, 4157, 2182, 166, 1816, 2626, 166, 2893, 2248,
                        166, 166, 166, 166, 1983, 5648, 166, 194, 166, 2106, 4328, 166, 4742, 166, 166, 5572,
                        2329, 3314, 166, 6181, 166, 166, 26, 166, 6026, 166, 166, 2114, 1669, 4735, 166, 166,
                        4256, 166, 1861, 166, 5470, 2317, 166, 4404, 2482, 166, 5305, 4415, 5986, 4949, 5412, 166,
                        1728, 166, 1898, 166, 166, 4909, 1989, 166, 166, 166, 2836, 2051, 274, 166, 2799, 166,
                        5865, 1663, 4705, 5121, 2555, 166, 4316, 4287, 1880, 1825, 166, 3689, 166, 1733, 5012, 166,
                        166, 2237, 4471, 1682, 2910, 166, 5366, 166, 166, 166, 166, 4532, 166, 2802, 166, 166,
                        166, 4057, 2471, 166, 2889, 166, 166, 4026, 5682, 3091, 166, 1977, 166, 2901, 6137, 5658,
                        88, 2318, 1965, 166, 5914, 166, 166, 4468, 1822, 166, 6050, 5956, 2201, 166, 4644, 2918,
                        166, 3703, 166, 166, 3524, 4220, 2913, 4210, 166, 166, 2090, 166, 1906, 1911, 166, 166,
                        3671, 2370, 166, 2552, 166, 3763, 2259, 1924, 166, 5940, 166, 166, 166, 3185, 3821, 4069,
                        261, 2381, 3244, 166, 166, 5715, 166, 2052, 5905, 166, 2403, 166, 3030, 2199, 166, 3550,
                        166, 166, 1846, 166, 166, 95, 166, 289, 3208, 2559, 5195, 5091, 1654, 166, 1781, 1892,
                        166, 4516, 2629, 166, 1700, 3067, 166, 166, 166, 2080, 1680, 166, 166, 166, 5700, 166,
                        1820, 5491, 166, 4226, 166, 166, 166, 166, 4653, 166, 3508, 227, 5364, 166, 2098, 166,
                        299, 166, 5795, 166, 166, 166, 166, 3690, 4134, 5517, 4534, 5042, 4874, 5798, 4234, 166,
                        166, 166, 166, 3702, 166, 166, 3638, 3108, 3850, 166, 166, 166, 16, 166, 1775, 166,
                        4022, 166, 223, 4095, 166, 5127, 4266, 166, 189, 166, 166, 5203, 166, 1805, 3884, 3778,
                        166, 166, 2146, 4818, 166, 2848, 3440, 4506, 5886, 3006, 218, 166, 2377, 166, 4091, 5925,
                        166, 4320, 166, 2701, 3036, 166, 166, 166, 4715, 166, 3801, 166, 3161, 166, 2077, 166,
                        4254, 3032, 243, 1814, 166, 166, 166, 166, 166, 166, 166, 166, 1835, 166, 4394, 166,
                        5769, 4923, 166, 2917, 166, 166, 178, 166, 166, 1723, 166, 5887, 166, 4956, 2952, 166,
                        4665, 3925, 3443, 3123, 166, 166, 166, 166, 166, 166, 5144, 166, 4288, 2074, 2192, 5442,
                        6043, 1746, 2016, 5995, 2203, 166, 5686, 5659, 3193, 166, 4055, 166, 166, 2233, 3571, 5809,
                        5984, 2323, 166, 166, 1740, 89, 4356, 6053, 6106, 3282, 4796, 166, 6116, 6056, 2353, 2829,
                        166, 5807, 2042, 166, 166, 166, 1670, 5937, 4465, 5646, 166, 5562, 3008, 166, 2419, 3736,
                        166, 4132, 169, 166, 166, 166, 2402, 166, 166, 1968, 2398, 166, 1684, 1827, 4551, 2679,
                        3875, 166, 5585, 3835, 2295, 166, 1991, 1803, 2992, 166, 166, 5847, 2649, 166, 76, 5415,
                        166, 2269, 2397, 5387, 5337, 4422, 166, 2672, 4832, 4617, 166, 166, 166, 166, 4552, 166,
                        4612, 1750, 166, 1931, 166, 1691, 2424, 4194, 6018, 166, 166, 4458, 4856, 166, 2089, 3814,
                        166, 2844, 166, 3592, 166, 4867, 5128, 166, 2685, 166, 166, 2616, 1972, 2617, 3943, 4664,
                        166, 4999, 166, 166, 145, 3635, 166, 166, 4851, 166, 3483, 5039, 166, 3649, 3924, 166,
                        166, 166, 3105, 4260, 166, 6098, 166, 3568, 267, 2456, 3653, 2096, 166, 166, 166, 3512,
                        166, 3405, 166, 3504, 166, 166, 166, 4005, 2144, 1769, 166, 5474, 1920, 5554, 215, 2443,
                        3351, 166, 5961, 166, 166, 166, 166, 242, 2331, 166, 166, 5931, 166, 166, 5862, 166,
                        1710, 166, 166, 166, 3321, 166, 4139, 166, 166, 3515, 2732, 2510, 5544, 166, 166, 2783,
                        166, 166, 166, 4018, 4649, 5789, 166, 166, 166, 166, 166, 2726, 6074, 166, 166, 166,
                        5684, 166, 166, 3395, 166, 3100, 166, 5763, 3757, 1992, 166, 3198, 2003, 166, 166, 4675,
                        166, 1893, 5621, 166, 2270, 166, 166, 166, 5421, 5590, 5664, 4045, 166, 3687, 4406, 2699,
                        1811, 167, 4036, 5384, 166, 166, 4601, 1823, 4041, 239, 1954, 166, 146, 166, 166, 3077,
                        5152, 5814, 1649, 5681, 166, 5868, 166, 166, 3792, 4860, 166, 5335, 5110, 1718, 166, 166,
                        166, 166, 3718, 3365, 2826, 166, 166, 5021, 4783, 166, 5569, 5812, 166, 166, 1876, 166,
                        3260, 166, 1789, 5667, 4224, 166, 166, 4385, 166, 166, 2620, 166, 4162, 2883, 2143, 5497,
                        166, 166, 5316, 5680, 166, 166, 248, 4050, 166, 6021, 166, 2898, 4618, 166, 166, 166,
                        166, 166, 5368, 166, 5378, 1842, 1914, 3696, 3962, 166, 4345, 2581, 1773, 2109, 166, 4371,
                        166, 166, 3761, 5277, 5870, 3146, 166, 166, 166, 5764, 127, 3058, 4059, 4718, 166, 5097,
                        5040, 5351, 3205, 166, 166, 4996, 2991, 2014, 166, 5846, 2558, 2688, 5595, 4027, 3347, 2125,
                        5696, 5608, 166, 166, 3228, 3745, 5775, 166, 1757, 4647, 166, 5977, 3020, 166, 240, 2565,
                        166, 4459, 166, 3367, 166, 166, 166, 3104, 166, 166, 166, 166, 166, 166, 259, 5486,
                        2846, 166, 166, 166, 4778, 2713, 166, 3955, 5683, 2682, 2914, 5898, 166, 166, 166, 4400,
                        317, 166, 5185, 3021, 5983, 4332, 3891, 166, 3095, 5003, 166, 166, 166, 5367, 166, 279,
                        1784, 4019, 2736, 4905, 2651, 5346, 166, 4841, 166, 5606, 166, 166, 2806, 166, 5239, 166,
                        166, 3237, 5490, 166, 225, 166, 166, 2254, 166, 2742, 4587, 22, 166, 166, 166, 5555,
                        166, 108, 2927, 2218, 166, 2120, 166, 5452, 4087, 4369, 166, 166, 166, 166, 166, 4583,
                        4338, 6035, 2840, 4365, 3624, 11, 1770, 166, 4630, 166, 3216, 166, 166, 166, 4638, 4699,
                        3535, 2536, 4627, 166, 166, 5760, 1935, 166, 166, 5210, 166, 2219, 2484, 4597, 5193, 4799,
                        3706, 166, 166, 166, 166, 3337, 3113, 5951, 4294, 166, 4040, 3200, 4217, 5861, 2767, 3530,
                        4499, 2775, 4121, 134, 5939, 5880, 5908, 3869, 166, 166, 3316, 6095, 2441, 3288, 166, 3751,
                        4794, 166, 166, 5803, 6169, 2356, 6182, 6135, 6127, 166, 3018, 166, 1674, 166, 166, 4097,
                        166, 5923, 287, 5965, 5129, 166, 4078, 166, 166, 6114, 6015, 5990, 3573, 166, 4146, 2681,
                        90, 6055, 4864, 166, 166, 6119, 3284, 6054, 5456, 5113, 6125, 166, 6057, 166, 3292, 166,
                        166, 166, 166, 166, 6185, 5105, 1760, 166, 166, 166, 2720, 166, 2695, 5448, 166, 1936,
                        166, 1807, 3406, 166, 166, 2161, 1642, 166, 5030, 166, 2036, 5451, 3427, 166, 166, 166,
                        166, 3797, 166, 1627, 166, 4515, 166, 166, 166, 4241, 166, 166, 166, 2771, 166, 31,
                        5197, 2638, 3035, 166, 166, 3914, 166, 166, 4546, 166, 166, 166, 4253, 3500, 166, 166,
                        2526, 166, 2698, 166, 3726, 2744, 137, 166, 166, 2676, 166, 5594, 166, 166, 166, 4842,
                        166, 63, 2888, 3585, 4798, 166, 5011, 166, 5634, 5464, 166, 166, 5620, 3894, 4070, 166,
                        2730, 166, 166, 1810, 2503, 5957, 1721, 6066, 5188, 166, 166, 1890, 4505, 1771, 5455, 166,
                        3132, 3984, 166, 166, 2811, 1962, 166, 166, 4872, 106, 3898, 3267, 166, 2085, 166, 4950,
                        6040, 4525, 6044, 5866, 3613, 2907, 4615, 2135, 258, 166, 1681, 1941, 4888, 166, 4859, 6178,
                        6174, 4858, 5209, 1912, 3340, 166, 4640, 5706, 166, 2763, 3153, 3951, 166, 5542, 5596, 5819,
                        5330, 5048, 4037, 166, 6033, 4625, 3326, 2013, 5283, 136, 3373, 2154, 166, 166, 166, 4421,
                        166, 5438, 2627, 2266, 2320, 166, 2588, 4790, 4290, 166, 4767, 5829, 2925, 5916, 2133, 166,
                };
            }
        }
    }
}
