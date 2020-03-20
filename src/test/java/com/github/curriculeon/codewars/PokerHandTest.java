package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author leonhunter
 * @created 03/20/2020 - 2:23 PM
 */
public class PokerHandTest {
    private PokerHand.Result loss = PokerHand.Result.LOSS;
    private PokerHand.Result win = PokerHand.Result.WIN;
    private PokerHand.Result tie = PokerHand.Result.TIE;

    @Test
    public void test1() {
        test("Highest straight flush wins", loss, "2H 3H 4H 5H 6H", "KS AS TS QS JS");
    }

    @Test
    public void test2() {
        test("Straight flush wins of 4 of a kind", win, "2H 3H 4H 5H 6H", "AS AD AC AH JD");
    }

    @Test
    public void test3() {
        test("Highest 4 of a kind wins", win, "AS AH 2H AD AC", "JS JD JC JH 3D");
    }

    @Test
    public void test4() {
        test("4 Of a kind wins of full house", loss, "2S AH 2H AS AC", "JS JD JC JH AD");
    }

    @Test
    public void test5() {
        test("Full house wins of flush", win, "2S AH 2H AS AC", "2H 3H 5H 6H 7H");
    }

    @Test
    public void test6() {
        test("Equal straight is tie", 	  	       tie,  "2S 3H 4H 5S 6C", "3D 4C 5H 6H 2S");
    }

    @Test
    public void test7() {
        test("Equal cards is tie",		             tie,  "2S AH 4H 5S 6C", "AD 4C 5H 6H 2C");
    }

    @Test
    public void testRemainder()
    {
        test("Highest flush wins",                 win,  "AS 3S 4S 8S 2S", "2H 3H 5H 6H 7H");
        test("Flush wins of straight",             win,  "2H 3H 5H 6H 7H", "2S 3H 4H 5S 6C");
        test("Straight wins of three of a kind",   win,  "2S 3H 4H 5S 6C", "AH AC 5H 6H AS");
        test("3 Of a kind wins of two pair",       loss, "2S 2H 4H 5S 4C", "AH AC 5H 6H AS");
        test("2 Pair wins of pair",                win,  "2S 2H 4H 5S 4C", "AH AC 5H 6H 7S");
        test("Highest pair wins",                  loss, "6S AD 7H 4S AS", "AH AC 5H 6H 7S");
        test("Pair wins of nothing",               loss, "2S AH 4H 5S KC", "AH AC 5H 6H 7S");
        test("Highest card loses",                 loss, "2S 3H 6H 7S 9C", "7H 3C TH 6H 9S");
        test("Highest card wins",                  win,  "4S 5H 6H TS AC", "3S 5H 6H TS AC");
    }

    private void test(String description, PokerHand.Result expected, String playerHand, String opponentHand) {
        PokerHand player = new PokerHand(playerHand);
        PokerHand opponent = new PokerHand(opponentHand);
        Assert.assertEquals(description + ":", expected, player.compareWith(opponent));
    }
}
