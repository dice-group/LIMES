package org.aksw.limes.core.measures.measure.string;

public class EDoubleMetaphone {
	private String metaphone, metaphone2, word;
	int size, last;
	boolean slavoGermanic = false;
	
	public EDoubleMetaphone(String word) {
		this.word = word.toUpperCase();
		size = word.length();
		last = size-1;
		this.metaphone = "";
		this.metaphone2 = "";
		slavoGermanic = checkSlavoGermanic();
		
		calculateMetaphone();
	}
	
	private boolean checkSlavoGermanic() {
		if(word.contains("W")||word.contains("K")||word.contains("CZ")||word.contains("WITZ"))
			return true;
		return false;
	}
	
	public String getDoubleMetaphoneRepresentation () {
		return metaphone;
	}
	
	public String getDoubleMetaphoneRepresentation2() {
		return metaphone2;
	}
	
	void metaphAdd(String main) {
		metaphone += main;
		metaphone2 += main;
	}
	
	void metaphAdd(String main, String alt) {
		metaphone += main;
		metaphone2 += alt;
	}
	
	char getAt(int index) {
		if(index>=size) {
			return Character.MIN_VALUE;
		}
		return word.charAt(index);
	}
	
	boolean stringAt(int start, int length, String ... values)
	{

        if (start < 0) return false;
        
        
        String target = word.substring(start, start+length);
        
        for (String test : values) {
        	if (test.equals(target)) {
        		return true;
        	}
        }
        
        return false;
	}
	
	boolean isVowel(int at)
	{
        if((at < 0) || (at >= size))
                return false;

        char it = word.charAt(at);

        if((it == 'A') || (it == 'E') || (it == 'I') || (it == 'O') || (it == 'U') || (it == 'Y') )
                return true;

        return false;
	}
	
	private void calculateMetaphone() {
		
        int current = 0;

        //pad the original string so that we can index beyond the edge of the world 
        word +=  "     ";
        
        //skip these when at start of word
        if(stringAt(0, 2, "GN", "KN", "PN", "WR", "PS", ""))
                current += 1;

        //Initial 'X' is pronounced 'Z' e.g. 'Xavier'
        if(getAt(0) == 'X')
        {
                metaphAdd("S"); //'Z' maps to 'S'
                current += 1;
        }

        ///////////main loop//////////////////////////
        while((metaphone.length() < 4) || (metaphone2.length() < 4))
        {
                if(current >= size)
                        break;

                switch(getAt(current))
                {
                        case 'A':
                        case 'E':
                        case 'I':
                        case 'O':
                        case 'U':
                        case 'Y':
                                if(current == 0)
                                        //all init vowels now map to 'A'
                                        metaphAdd("A"); 
                                current +=1;
                                break;
                        
                        case 'B':

                                //"-mb", e.g", "dumb", already skipped over...
                                metaphAdd("P");

                                if(getAt(current + 1) == 'B')
                                        current +=2;
                                else
                                        current +=1;
                                break;
                        
                        case 'Ç':
                                metaphAdd("S");
                                current += 1;
                                break;

                        case 'C':
                                //various germanic
                                if((current > 1)
                                        && !isVowel(current - 2) 
                                                && stringAt((current - 1), 3, "ACH", "") 
                                                        && ((getAt(current + 2) != 'I') && ((getAt(current + 2) != 'E') 
                                                                        || stringAt((current - 2), 6, "BACHER", "MACHER", "")) ))
                                {       
                                        metaphAdd("K");
                                        current +=2;
                                        break;
                                }

                                //special case 'caesar'
                                if((current == 0) && stringAt(current, 6, "CAESAR", ""))
                                {
                                        metaphAdd("S");
                                        current +=2;
                                        break;
                                }

                                //italian 'chianti'
                                if(stringAt(current, 4, "CHIA", ""))
                                {
                                        metaphAdd("K");
                                        current +=2;
                                        break;
                                }

                                if(stringAt(current, 2, "CH", ""))
                                {       
                                        //find 'michael'
                                        if((current > 0) && stringAt(current, 4, "CHAE", ""))
                                        {
                                                metaphAdd("K", "X");
                                                current +=2;
                                                break;
                                        }

                                        //greek roots e.g. 'chemistry', 'chorus'
                                        if((current == 0)
                                                && (stringAt((current + 1), 5, "HARAC", "HARIS", "") 
                                                        || stringAt((current + 1), 3, "HOR", "HYM", "HIA", "HEM", "")) 
                                                                && !stringAt(0, 5, "CHORE", ""))
                                        {
                                                metaphAdd("K");
                                                current +=2;
                                                break;
                                        }

                                        //germanic, greek, or otherwise 'ch' for 'kh' sound
                                        if((stringAt(0, 4, "VAN ", "VON ", "") || stringAt(0, 3, "SCH", ""))
                                                // 'architect but not 'arch', 'orchestra', 'orchid'
                                                || stringAt((current - 2), 6, "ORCHES", "ARCHIT", "ORCHID", "")
                                                        || stringAt((current + 2), 1, "T", "S", "")
                                                                || ((stringAt((current - 1), 1, "A", "O", "U", "E", "") || (current == 0))
                                                                        //e.g., 'wachtler', 'wechsler', but not 'tichner'
                                                                        && stringAt((current + 2), 1, "L", "R", "N", "M", "B", "H", "F", "V", "W", " ", "")))
                                        {
                                                metaphAdd("K");
                                        }else{  
                                                if(current > 0)
                                                {
                                                        if(stringAt(0, 2, "MC", ""))
                                                                //e.g., "McHugh"
                                                                metaphAdd("K");
                                                        else
                                                                metaphAdd("X", "K");
                                                }else
                                                        metaphAdd("X");
                                        }
                                        current +=2;
                                        break;
                                }
                                //e.g, 'czerny'
                                if(stringAt(current, 2, "CZ", "") && !stringAt((current - 2), 4, "WICZ", ""))
                                {
                                        metaphAdd("S", "X");
                                        current += 2;
                                        break;
                                }

                                //e.g., 'focaccia'
                                if(stringAt((current + 1), 3, "CIA", ""))
                                {
                                        metaphAdd("X");
                                        current += 3;
                                        break;
                                }

                                //double 'C', but not if e.g. 'McClellan'
                                if(stringAt(current, 2, "CC", "") && !((current == 1) && (getAt(0) == 'M')))
                                        //'bellocchio' but not 'bacchus'
                                        if(stringAt((current + 2), 1, "I", "E", "H", "") && !stringAt((current + 2), 2, "HU", ""))
                                        {
                                                //'accident', 'accede' 'succeed'
                                                if(((current == 1) && (getAt(current - 1) == 'A')) 
                                                                || stringAt((current - 1), 5, "UCCEE", "UCCES", ""))
                                                        metaphAdd("KS");
                                                //'bacci', 'bertucci', other italian
                                                else
                                                        metaphAdd("X");
                                                current += 3;
                                                break;
                                        }else{//Pierce's rule
                                                metaphAdd("K");
                                                current += 2;
                                                break;
                                        }

                                if(stringAt(current, 2, "CK", "CG", "CQ", ""))
                                {
                                        metaphAdd("K");
                                        current += 2;
                                        break;
                                }

                                if(stringAt(current, 2, "CI", "CE", "CY", ""))
                                {
                                        //italian vs. english
                                        if(stringAt(current, 3, "CIO", "CIE", "CIA", ""))
                                                metaphAdd("S", "X");
                                        else
                                                metaphAdd("S");
                                        current += 2;
                                        break;
                                }

                                //else
                                metaphAdd("K");
                                
                                //name sent in 'mac caffrey', 'mac gregor
                                if(stringAt((current + 1), 2, " C", " Q", " G", ""))
                                        current += 3;
                                else
                                        if(stringAt((current + 1), 1, "C", "K", "Q", "") 
                                                && !stringAt((current + 1), 2, "CE", "CI", ""))
                                                current += 2;
                                        else
                                                current += 1;
                                break;

                        case 'D':
                                if(stringAt(current, 2, "DG", ""))
                                        if(stringAt((current + 2), 1, "I", "E", "Y", ""))
                                        {
                                                //e.g. 'edge'
                                                metaphAdd("J");
                                                current += 3;
                                                break;
                                        }else{
                                                //e.g. 'edgar'
                                                metaphAdd("TK");
                                                current += 2;
                                                break;
                                        }

                                if(stringAt(current, 2, "DT", "DD", ""))
                                {
                                        metaphAdd("T");
                                        current += 2;
                                        break;
                                }
                                
                                //else
                                metaphAdd("T");
                                current += 1;
                                break;

                        case 'F':
                                if(getAt(current + 1) == 'F')
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("F");
                                break;

                        case 'G':
                                if(getAt(current + 1) == 'H')
                                {
                                        if((current > 0) && !isVowel(current - 1))
                                        {
                                                metaphAdd("K");
                                                current += 2;
                                                break;
                                        }

                                        if(current < 3)
                                        {
                                                //'ghislane', ghiradelli
                                                if(current == 0)
                                                { 
                                                        if(getAt(current + 2) == 'I')
                                                                metaphAdd("J");
                                                        else
                                                                metaphAdd("K");
                                                        current += 2;
                                                        break;
                                                }
                                        }
                                        //Parker's rule (with some further refinements) - e.g., 'hugh'
                                        if(((current > 1) && stringAt((current - 2), 1, "B", "H", "D", "") )
                                                //e.g., 'bough'
                                                || ((current > 2) && stringAt((current - 3), 1, "B", "H", "D", "") )
                                                //e.g., 'broughton'
                                                || ((current > 3) && stringAt((current - 4), 1, "B", "H", "") ) )
                                        {
                                                current += 2;
                                                break;
                                        }else{
                                                //e.g., 'laugh', 'McLaughlin', 'cough', 'gough', 'rough', 'tough'
                                                if((current > 2) 
                                                        && (getAt(current - 1) == 'U') 
                                                        && stringAt((current - 3), 1, "C", "G", "L", "R", "T", "") )
                                                {
                                                        metaphAdd("F");
                                                }else
                                                        if((current > 0) && getAt(current - 1) != 'I')
                                                                metaphAdd("K");

                                                current += 2;
                                                break;
                                        }
                                }

                                if(getAt(current + 1) == 'N')
                                {
                                        if((current == 1) && isVowel(0) && !slavoGermanic)
                                        {
                                                metaphAdd("KN", "N");
                                        }else
                                                //not e.g. 'cagney'
                                                if(!stringAt((current + 2), 2, "EY", "") 
                                                                && (getAt(current + 1) != 'Y') && !slavoGermanic)
                                                {
                                                        metaphAdd("N", "KN");
                                                }else
                                                        metaphAdd("KN");
                                        current += 2;
                                        break;
                                }

                                //'tagliaro'
                                if(stringAt((current + 1), 2, "LI", "") && !slavoGermanic)
                                {
                                        metaphAdd("KL", "L");
                                        current += 2;
                                        break;
                                }

                                //-ges-,-gep-,-gel-, -gie- at beginning
                                if((current == 0)
                                        && ((getAt(current + 1) == 'Y') 
                                                || stringAt((current + 1), 2, "ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER", "")) )
                                {
                                        metaphAdd("K", "J");
                                        current += 2;
                                        break;
                                }

                                // -ger-,  -gy-
                                if((stringAt((current + 1), 2, "ER", "") || (getAt(current + 1) == 'Y'))
                                                && !stringAt(0, 6, "DANGER", "RANGER", "MANGER", "")
                                                        && !stringAt((current - 1), 1, "E", "I", "") 
                                                                && !stringAt((current - 1), 3, "RGY", "OGY", "") )
                                {
                                        metaphAdd("K", "J");
                                        current += 2;
                                        break;
                                }

                                // italian e.g, 'biaggi'
                                if(stringAt((current + 1), 1, "E", "I", "Y", "") || stringAt((current - 1), 4, "AGGI", "OGGI", ""))
                                {
                                        //obvious germanic
                                        if((stringAt(0, 4, "VAN ", "VON ", "") || stringAt(0, 3, "SCH", ""))
                                                || stringAt((current + 1), 2, "ET", ""))
                                                metaphAdd("K");
                                        else
                                                //always soft if french ending
                                                if(stringAt((current + 1), 4, "IER ", ""))
                                                        metaphAdd("J");
                                                else
                                                        metaphAdd("J", "K");
                                        current += 2;
                                        break;
                                }

                                if(getAt(current + 1) == 'G')
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("K");
                                break;

                        case 'H':
                                //only keep if first & before vowel or btw. 2 vowels
                                if(((current == 0) || isVowel(current - 1)) 
                                        && isVowel(current + 1))
                                {
                                        metaphAdd("H");
                                        current += 2;
                                }else//also takes care of 'HH'
                                        current += 1;
                                break;

                        case 'J':
                                //obvious spanish, 'jose', 'san jacinto'
                                if(stringAt(current, 4, "JOSE", "") || stringAt(0, 4, "SAN ", "") )
                                {
                                        if(((current == 0) && (getAt(current + 4) == ' ')) || stringAt(0, 4, "SAN ", "") )
                                                metaphAdd("H");
                                        else
                                        {
                                                metaphAdd("J", "H");
                                        }
                                        current +=1;
                                        break;
                                }

                                if((current == 0) && !stringAt(current, 4, "JOSE", ""))
                                        metaphAdd("J", "A");//Yankelovich/Jankelowicz
                                else
                                        //spanish pron. of e.g. 'bajador'
                                        if(isVowel(current - 1) 
                                                && !slavoGermanic
                                                        && ((getAt(current + 1) == 'A') || (getAt(current + 1) == 'O')))
                                                metaphAdd("J", "H");
                                        else
                                                if(current == last)
                                                        metaphAdd("J", " ");
                                                else
                                                        if(!stringAt((current + 1), 1, "L", "T", "K", "S", "N", "M", "B", "Z", "") 
                                                                        && !stringAt((current - 1), 1, "S", "K", "L", ""))
                                                                metaphAdd("J");

                                if(getAt(current + 1) == 'J')//it could happen!
                                        current += 2;
                                else
                                        current += 1;
                                break;

                        case 'K':
                                if(getAt(current + 1) == 'K')
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("K");
                                break;

                        case 'L':
                                if(getAt(current + 1) == 'L')
                                {
                                        //spanish e.g. 'cabrillo', 'gallegos'
                                        if(((current == (size - 3)) 
                                                && stringAt((current - 1), 4, "ILLO", "ILLA", "ALLE", ""))
                                                         || ((stringAt((last - 1), 2, "AS", "OS", "") || stringAt(last, 1, "A", "O", "")) 
                                                                && stringAt((current - 1), 4, "ALLE", "")) )
                                        {
                                                metaphAdd("L", " ");
                                                current += 2;
                                                break;
                                        }
                                        current += 2;
                                }else
                                        current += 1;
                                metaphAdd("L");
                                break;

                        case 'M':
                                if((stringAt((current - 1), 3, "UMB", "") 
                                        && (((current + 1) == last) || stringAt((current + 2), 2, "ER", "")))
                                                //'dumb','thumb'
                                                ||  (getAt(current + 1) == 'M') )
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("M");
                                break;

                        case 'N':
                                if(getAt(current + 1) == 'N')
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("N");
                                break;

                        case 'Ñ':
                                current += 1;
                                metaphAdd("N");
                                break;

                        case 'P':
                                if(getAt(current + 1) == 'H')
                                {
                                        metaphAdd("F");
                                        current += 2;
                                        break;
                                }

                                //also account for "campbell", "raspberry"
                                if(stringAt((current + 1), 1, "P", "B", ""))
                                        current += 2;
                                else
                                        current += 1;
                                        metaphAdd("P");
                                break;

                        case 'Q':
                                if(getAt(current + 1) == 'Q')
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("K");
                                break;

                        case 'R':
                                //french e.g. 'rogier', but exclude 'hochmeier'
                                if((current == last)
                                        && !slavoGermanic
                                                && stringAt((current - 2), 2, "IE", "") 
                                                        && !stringAt((current - 4), 2, "ME", "MA", ""))
                                        metaphAdd("", "R");
                                else
                                        metaphAdd("R");

                                if(getAt(current + 1) == 'R')
                                        current += 2;
                                else
                                        current += 1;
                                break;

                        case 'S':
                                //special cases 'island', 'isle', 'carlisle', 'carlysle'
                                if(stringAt((current - 1), 3, "ISL", "YSL", ""))
                                {
                                        current += 1;
                                        break;
                                }

                                //special case 'sugar-'
                                if((current == 0) && stringAt(current, 5, "SUGAR", ""))
                                {
                                        metaphAdd("X", "S");
                                        current += 1;
                                        break;
                                }

                                if(stringAt(current, 2, "SH", ""))
                                {
                                        //germanic
                                        if(stringAt((current + 1), 4, "HEIM", "HOEK", "HOLM", "HOLZ", ""))
                                                metaphAdd("S");
                                        else
                                                metaphAdd("X");
                                        current += 2;
                                        break;
                                }

                                //italian & armenian
                                if(stringAt(current, 3, "SIO", "SIA", "") || stringAt(current, 4, "SIAN", ""))
                                {
                                        if(!slavoGermanic)
                                                metaphAdd("S", "X");
                                        else
                                                metaphAdd("S");
                                        current += 3;
                                        break;
                                }

                                //german & anglicisations, e.g. 'smith' match 'schmidt', 'snider' match 'schneider'
                                //also, -sz- in slavic language altho in hungarian it is pronounced 's'
                                if(((current == 0) 
                                                && stringAt((current + 1), 1, "M", "N", "L", "W", ""))
                                                        || stringAt((current + 1), 1, "Z", ""))
                                {
                                        metaphAdd("S", "X");
                                        if(stringAt((current + 1), 1, "Z", ""))
                                                current += 2;
                                        else
                                                current += 1;
                                        break;
                                }

                                if(stringAt(current, 2, "SC", ""))
                                {
                                        //Schlesinger's rule
                                        if(getAt(current + 2) == 'H')
                                                //dutch origin, e.g. 'school', 'schooner'
                                                if(stringAt((current + 3), 2, "OO", "ER", "EN", "UY", "ED", "EM", ""))
                                                {
                                                        //'schermerhorn', 'schenker'
                                                        if(stringAt((current + 3), 2, "ER", "EN", ""))
                                                        {
                                                                metaphAdd("X", "SK");
                                                        }else
                                                                metaphAdd("SK");
                                                        current += 3;
                                                        break;
                                                }else{
                                                        if((current == 0) && !isVowel(3) && (getAt(3) != 'W'))
                                                                metaphAdd("X", "S");
                                                        else
                                                                metaphAdd("X");
                                                        current += 3;
                                                        break;
                                                }

                                        if(stringAt((current + 2), 1, "I", "E", "Y", ""))
                                        {
                                                metaphAdd("S");
                                                current += 3;
                                                break;
                                        }
                                        //else
                                        metaphAdd("SK");
                                        current += 3;
                                        break;
                                }

                                //french e.g. 'resnais', 'artois'
                                if((current == last) && stringAt((current - 2), 2, "AI", "OI", ""))
                                        metaphAdd("", "S");
                                else
                                        metaphAdd("S");

                                if(stringAt((current + 1), 1, "S", "Z", ""))
                                        current += 2;
                                else
                                        current += 1;
                                break;

                        case 'T':
                                if(stringAt(current, 4, "TION", ""))
                                {
                                        metaphAdd("X");
                                        current += 3;
                                        break;
                                }

                                if(stringAt(current, 3, "TIA", "TCH", ""))
                                {
                                        metaphAdd("X");
                                        current += 3;
                                        break;
                                }

                                if(stringAt(current, 2, "TH", "") 
                                        || stringAt(current, 3, "TTH", ""))
                                {
                                        //special case 'thomas', 'thames' or germanic
                                        if(stringAt((current + 2), 2, "OM", "AM", "") 
                                                || stringAt(0, 4, "VAN ", "VON ", "") 
                                                        || stringAt(0, 3, "SCH", ""))
                                        {
                                                metaphAdd("T");
                                        }else{
                                                metaphAdd("0", "T");
                                        }
                                        current += 2;
                                        break;
                                }

                                if(stringAt((current + 1), 1, "T", "D", ""))
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("T");
                                break;

                        case 'V':
                                if(getAt(current + 1) == 'V')
                                        current += 2;
                                else
                                        current += 1;
                                metaphAdd("F");
                                break;

                        case 'W':
                                //can also be in middle of word
                                if(stringAt(current, 2, "WR", ""))
                                {
                                        metaphAdd("R");
                                        current += 2;
                                        break;
                                }

                                if((current == 0) 
                                        && (isVowel(current + 1) || stringAt(current, 2, "WH", "")))
                                {
                                        //Wasserman should match Vasserman
                                        if(isVowel(current + 1))
                                                metaphAdd("A", "F");
                                        else
                                                //need Uomo to match Womo
                                                metaphAdd("A");
                                }

                                //Arnow should match Arnoff
                                if(((current == last) && isVowel(current - 1)) 
                                        || stringAt((current - 1), 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY", "") 
                                                        || stringAt(0, 3, "SCH", ""))
				  {
                                        metaphAdd("", "F");
                                        current +=1;
                                        break;
                                }

                                //polish e.g. 'filipowicz'
                                if(stringAt(current, 4, "WICZ", "WITZ", ""))
                                {
                                        metaphAdd("TS", "FX");
                                        current +=4;
                                        break;
                                }

                                //else skip it
                                current +=1;
                                break;

                        case 'X':
                                //french e.g. breaux
                                if(!((current == last) 
                                        && (stringAt((current - 3), 3, "IAU", "EAU", "") 
                                                        || stringAt((current - 2), 2, "AU", "OU", ""))) )
                                        metaphAdd("KS");

                                if(stringAt((current + 1), 1, "C", "X", ""))
                                        current += 2;
                                else
                                        current += 1;
                                break;

                        case 'Z':
                                //chinese pinyin e.g. 'zhao'
                                if(getAt(current + 1) == 'H')
                                {
                                        metaphAdd("J");
                                        current += 2;
                                        break;
                                }else
                                        if(stringAt((current + 1), 2, "ZO", "ZI", "ZA", "") 
                                                || (slavoGermanic && ((current > 0) && getAt(current - 1) != 'T')))
                                        {
                                                metaphAdd("S", "TS");
                                        }
                                        else
                                                metaphAdd("S");

                                if(getAt(current + 1) == 'Z')
                                        current += 2;
                                else
                                        current += 1;
                                break;

                        default:
                                current += 1;
                }
        }

    }
}


