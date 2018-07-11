package org.aksw.limes.core.measures.measure.bags;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * The Class String2BagMappers simply parses the data(strings) to bags
 *
 * @author Cedric Richter
 */
public class String2BagMapper {

    //(A, B, B, C) ==> {A:1, B:2, C:1}
    public static Multiset<String> getBag(String s){
        Multiset<String> out = HashMultiset.create();

        String buffer = "";
        String whitespaceBuffer = "";
        int state = 0;

        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);

            switch(state){
                case 0:
                    if(c == '(')
                        state = 1;
                    break;
                case 1:
                    if(c == ',') {
                        out.add(buffer);
                        buffer = "";
                    }else if(c == ')'){
                        state = 2;
                    }else if(Character.isWhitespace(c)){
                        if(whitespaceBuffer.isEmpty()) whitespaceBuffer += c;
                    }else{
                        if(!whitespaceBuffer.isEmpty() && !buffer.isEmpty()){
                            buffer += whitespaceBuffer;
                        }
                        whitespaceBuffer = "";
                        buffer += c;
                    }
                    break;
            }
        }

        if(state != 2)
            return null;

        return out;
    }

}
