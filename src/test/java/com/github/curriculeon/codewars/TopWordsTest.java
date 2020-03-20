package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
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

    @Test
    public void test5() {
        test("dJqlCaWNRq scXneeqz scXneeqz dJqlCaWNRq dCslRQ ZdUmkyed:BQp-hjNp ZdUmkyed hjNp,GNqKRj dCslRQ ZdUmkyed,scXneeqz GNqKRj:hjNp!scXneeqz_hjNp?scXneeqz-dCslRQ-dJqlCaWNRq hjNp;dCslRQ.scXneeqz.dCslRQ scXneeqz hjNp dJqlCaWNRq hjNp scXneeqz_hjNp-scXneeqz hjNp scXneeqz hjNp/scXneeqz hjNp hjNp_dJqlCaWNRq;GNqKRj ZdUmkyed dCslRQ dCslRQ.scXneeqz_GNqKRj scXneeqz/dCslRQ ZdUmkyed.dCslRQ.dJqlCaWNRq hjNp!GNqKRj ZdUmkyed:GNqKRj;scXneeqz hjNp;hjNp!scXneeqz.hjNp scXneeqz dJqlCaWNRq hjNp.dJqlCaWNRq hjNp GNqKRj scXneeqz hjNp;dJqlCaWNRq/scXneeqz?scXneeqz ZdUmkyed/dCslRQ dJqlCaWNRq scXneeqz.scXneeqz_ZdUmkyed hjNp scXneeqz?dJqlCaWNRq ZdUmkyed-dJqlCaWNRq/dCslRQ:hjNp;GNqKRj/scXneeqz dJqlCaWNRq;scXneeqz ZdUmkyed GNqKRj dJqlCaWNRq,ZdUmkyed-dJqlCaWNRq:dJqlCaWNRq hjNp;GNqKRj!dJqlCaWNRq!scXneeqz ZdUmkyed?dJqlCaWNRq ZdUmkyed scXneeqz hjNp?GNqKRj?dJqlCaWNRq,dJqlCaWNRq.dJqlCaWNRq scXneeqz!scXneeqz dJqlCaWNRq.ZdUmkyed ZdUmkyed:ZdUmkyed scXneeqz.",
                Arrays.asList(""));
    }


    @Test
    public void test6() {
        test("gQTnie okdloQQcY?kqPvrGFseV okdloQQcY!kqPvrGFseV_gQTnie siogDBpA-RBVWpArzX ddmrS siogDBpA?siogDBpA?ddmrS ddmrS!ddmrS siogDBpA;FymLaOGu!okdloQQcY esBMTBO gQTnie okdloQQcY Eb'F siogDBpA-kqPvrGFseV kqPvrGFseV Eb'F RBVWpArzX okdloQQcY okdloQQcY:okdloQQcY siogDBpA FymLaOGu,Eb'F.ddmrS FymLaOGu;gQTnie.gQTnie FymLaOGu!FymLaOGu!okdloQQcY/FymLaOGu!FymLaOGu gQTnie.FymLaOGu RBVWpArzX_ddmrS.ddmrS okdloQQcY.ddmrS-ddmrS ddmrS kqPvrGFseV_kqPvrGFseV;FymLaOGu okdloQQcY okdloQQcY?ddmrS siogDBpA siogDBpA ddmrS:siogDBpA.RBVWpArzX FymLaOGu kqPvrGFseV ddmrS.okdloQQcY_FymLaOGu okdloQQcY?ddmrS siogDBpA,siogDBpA kqPvrGFseV_FymLaOGu.okdloQQcY!Eb'F?Eb'F ddmrS gQTnie kqPvrGFseV esBMTBO FymLaOGu FymLaOGu:FymLaOGu kqPvrGFseV kqPvrGFseV?FymLaOGu ddmrS esBMTBO,okdloQQcY FymLaOGu okdloQQcY RBVWpArzX,ddmrS-kqPvrGFseV,Eb'F FymLaOGu gQTnie RBVWpArzX:siogDBpA:siogDBpA FymLaOGu kqPvrGFseV!kqPvrGFseV-siogDBpA!FymLaOGu ddmrS okdloQQcY FymLaOGu FymLaOGu;siogDBpA gQTnie okdloQQcY.ddmrS esBMTBO.okdloQQcY siogDBpA!ddmrS FymLaOGu okdloQQcY_okdloQQcY kqPvrGFseV gQTnie,ddmrS ddmrS:",
                Arrays.asList(""));
    }

    @Test
    public void test7() {
        test("uMixSntVz JIYuhvV.uMixSntVz_MzWbdmAZx uMixSntVz MzWbdmAZx;lNZ;MzWbdmAZx uMixSntVz JIYuhvV!JIYuhvV lNZ uMixSntVz_lNZ lNZ!lNZ?uMixSntVz:uMixSntVz uMixSntVz;lNZ uMixSntVz.lNZ_JIYuhvV?lNZ lNZ JIYuhvV lNZ JIYuhvV lNZ JIYuhvV!lNZ JIYuhvV/JIYuhvV JIYuhvV uMixSntVz uMixSntVz lNZ-lNZ/lNZ uMixSntVz JIYuhvV,lNZ JIYuhvV?uMixSntVz uMixSntVz,uMixSntVz lNZ-JIYuhvV MzWbdmAZx.JIYuhvV:lNZ lNZ JIYuhvV:JIYuhvV;JIYuhvV-uMixSntVz uMixSntVz;JIYuhvV!uMixSntVz?lNZ uMixSntVz uMixSntVz lNZ JIYuhvV lNZ-uMixSntVz:uMixSntVz JIYuhvV lNZ.MzWbdmAZx JIYuhvV lNZ-lNZ/JIYuhvV lNZ lNZ JIYuhvV;JIYuhvV JIYuhvV JIYuhvV,JIYuhvV_JIYuhvV JIYuhvV uMixSntVz uMixSntVz ",
                Arrays.asList(""));
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
