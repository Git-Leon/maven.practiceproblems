package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author leonhunter
 * @created 03/20/2020 - 12:32 PM
 */
public class TopWordsTest {
    @Test
    public void test1() {
        test("a a a  b  c c  d d d d  e e e e e", Arrays.asList("e", "d", "a"));
    }

    @Test
    public void test2() {
        test("e e e e DDD ddd DdD: ddd ddd aa aA Aa, bb cc cC e e e", Arrays.asList("e", "ddd", "aa"));
    }

    @Test
    public void test3() {
        test("uZq QOPBh zFFq zFFq,vdqFgDM.rjQDpsXr-rjQDpsXr!ezWlxoNQv?AVOPrvVcr QOPBh AVOPrvVcr:uZq QOPBh:vdqFgDM?AVOPrvVcr uZq ezWlxoNQv QOPBh AVOPrvVcr:AVOPrvVcr rjQDpsXr!QOPBh vdqFgDM!vdqFgDM:ezWlxoNQv rjQDpsXr zFFq-uZq/QOPBh QOPBh/QOPBh;rjQDpsXr AVOPrvVcr uZq,uZq ezWlxoNQv rjQDpsXr_zFFq,ezWlxoNQv!vdqFgDM vdqFgDM ezWlxoNQv!rjQDpsXr rjQDpsXr/vdqFgDM AVOPrvVcr rjQDpsXr ezWlxoNQv-QOPBh,uZq,AVOPrvVcr rjQDpsXr.QOPBh,uZq,ezWlxoNQv.AVOPrvVcr/QOPBh,QOPBh!QOPBh!uZq.QOPBh/uZq vdqFgDM AVOPrvVcr uZq QOPBh uZq-vdqFgDM rjQDpsXr_rjQDpsXr,AVOPrvVcr uZq ezWlxoNQv ezWlxoNQv ezWlxoNQv/ezWlxoNQv_QOPBh rjQDpsXr ezWlxoNQv;zFFq:uZq zFFq/uZq;QOPBh;uZq zFFq!ezWlxoNQv-ezWlxoNQv;rjQDpsXr/AVOPrvVcr?QOPBh!uZq vdqFgDM ezWlxoNQv;ezWlxoNQv_QOPBh AVOPrvVcr vdqFgDM AVOPrvVcr,uZq AVOPrvVcr rjQDpsXr:AVOPrvVcr!AVOPrvVcr/AVOPrvVcr vdqFgDM zFFq uZq.AVOPrvVcr uZq/uZq uZq ezWlxoNQv/rjQDpsXr AVOPrvVcr;zFFq?uZq QOPBh/rjQDpsXr:rjQDpsXr ezWlxoNQv vdqFgDM ezWlxoNQv;QOPBh_ezWlxoNQv/QOPBh_vdqFgDM QOPBh_QOPBh/uZq ezWlxoNQv ezWlxoNQv;uZq_uZq zFFq AVOPrvVcr AVOPrvVcr ezWlxoNQv vdqFgDM QOPBh;QOPBh?QOPBh QOPBh vdqFgDM uZq vdqFgDM rjQDpsXr-vdqFgDM/uZq;vdqFgDM/AVOPrvVcr uZq ezWlxoNQv QOPBh ezWlxoNQv/QOPBh ",
        Arrays.asList("qopbh", "uzq", "ezwlxonqv"));
    }

    @Test
    public void test4() {
        test("XUvKZq:UJyB:XUvKZq;XUvKZq UJyB,mSs ZciR ZciR XUvKZq",
                Arrays.asList("xuvkzq", "ujyb", "zcir"));
    }

    private void test(String input, List<String> expected) {
        // given
        TopWords topWords = new TopWords();

        // when
        List<String> actual = topWords.top3(input);

        // then
        Assert.assertEquals(expected, actual);
    }
}
