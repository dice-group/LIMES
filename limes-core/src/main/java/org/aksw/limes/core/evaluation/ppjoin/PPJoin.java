package org.aksw.limes.core.evaluation.ppjoin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PPJoin {
	
	private static final int MAXDEPTH = 2;
	List<Record> records; // Holds all records
	List<String> universal = new ArrayList<String>(); // Universal set of tokens
	double threshold = 0.8; //Holds threshold value
	Map <Integer,Integer> recordPairs ;
	Map <Integer,Integer> similarRecords ;
	
	
	PPJoin() {
		this.records = new ArrayList<Record>();
		this.recordPairs = new HashMap<Integer,Integer>();
		this.similarRecords = new HashMap<Integer,Integer>();
	}
	
	// This method fetches records from input filename and returns a string array
	@SuppressWarnings("null")
	String[] fetchRecords(String filename) {
		
		String[] strRecords = new String[10];
        // One record
        String line = null;
        // record number
        int lineno = 0;
        try {
            // FileReader reads text file
            FileReader fileReader = 
                new FileReader(filename);

            // wrap FileReader in BufferedReader
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                strRecords[lineno] = line;
                lineno++;
            }   

            // close file
            bufferedReader.close();
            return strRecords;
            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" +filename+ "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"+ filename + "'");                  
        }
        return null;
	}
	
	// Method to update the frequency of each token of record in the universal list
	void updateFrequency() {
		
		Set<String> unique = new HashSet<String>(this.universal);
		
		for (String key : unique) {
			
			for (int i = 0; i < this.records.size(); i++) {
				for (int j=0;j < this.records.get(i).tokens.size();j++) {
					if (this.records.get(i).tokens.get(j).word .equals(key)) {
						records.get(i).tokens.get(j).setFrequency(Collections.frequency(universal, records.get(i).tokens.get(j).getToken()));
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	void createUniqueUniversal() {
		
		Set<String> hs = new HashSet<>();
		hs.addAll(this.universal);
		this.universal.clear();
		this.universal.addAll(hs);
	}
	
	void createRecords(String[] strRecords) {
		int i = 0;
		while(strRecords[i]!=null) {
			BreakIterator breakIterator = BreakIterator.getWordInstance();
		    breakIterator.setText(strRecords[i]);
		    int lastIndex = breakIterator.first();
		    int j=0;
		    String word;
		     
		    this.records.add(i,new Record());
		    while (BreakIterator.DONE != lastIndex) {
		        int firstIndex = lastIndex;
		        lastIndex = breakIterator.next();
		        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(strRecords[i].charAt(firstIndex))) {
		        	word = strRecords[i].substring(firstIndex, lastIndex);
		        	this.records.get(i).tokens.add(j,new Token());
		        	this.records.get(i).tokens.get(j++).word = word;
		            this.universal.add(strRecords[i].substring(firstIndex, lastIndex));
		        }
		    }
		      
		    records.get(i).setId(i+1);
		    i++;
		}
		
	}
	
	
	int prefixLength(Record r,double threshold) {
		return (int) Math.round(r.tokens.size() - (threshold * r.tokens.size()) + 1);
	}
	
	double jaccardConstraint (int x, int y, double threshold) {
		double alpha =0;
		alpha = (threshold/(threshold+1))* (x + y);
		return alpha;
	}
	
	Map<Integer,Integer> getTokenIndex(String token, int recordId) {
		Map<Integer,Integer> index = new HashMap<Integer,Integer>();
		for (int i=0;i<records.size();i++) {
			for (int j=0; j<records.get(i).tokens.size();j++) {
				if (records.get(i).tokens.get(j).word .equals(token)) {
					if (records.get(i).id != recordId)
						index.put(records.get(i).id,j);
						//break;
				}
			}
			
		}
		return index;
	}
	
	Record getRecordWithId(int id) {
		for (int i=0;i<records.size();i++) {
			if (records.get(i).id == id) {
				return records.get(i);
			}
				
			
		}
		return null;
	}
	
	void initializeCandidatePairs() {
		for (int i=0; i<this.records.size();i++) {
			this.recordPairs.put(new Integer(this.records.get(i).id),new Integer(0));
		}
	}
	
	public double calcByMerge(Record a, int aprefix, Record b, int bprefix) {

		int overlap = 0;
		int i = aprefix;
		int j = bprefix;

		while (i < a.tokens.size() && j < b.tokens.size()) {
			if (a.tokens.get(i).frequency == b.tokens.get(j).frequency) {
				overlap++;
				i++;
				j++;
			} else if (a.tokens.get(i).frequency < b.tokens.get(j).frequency)
				i++;
			else
				j++;
		}
		return overlap;

	}
	
	int findPositionInSet(TokenSet input) {
		int low = input.left;
		int high = input.right;
		while (low<=high) {
			int mid = (low+high)/2;
			if (input.tokenset.get(mid).frequency >= input.token.frequency) 
				return mid;
			else {
				low = mid+1;
			}
		}
		
		return -1;
	}
	
	void partition(TokenSet input) {
		
		int sl = input.tokenset.get(input.left).getFrequency();
		int w = input.token.getFrequency();
		int sr = input.tokenset.get(input.right).getFrequency();
		if ((sl > w) || (sr < w)) {
			input.setInSearchngRange(false);
			input.setNotInTokenSet(true);
			input.setSubsetl(null);
			input.setSubsetr(null);
			return;
		}
		
		int position = findPositionInSet(input);
		for (int i =0; i < position ; i++) {
			input.subsetl.add(i, input.tokenset.get(i));
		}
		int j=0;
		if ((position!=-1) && input.tokenset.get(position).getFrequency() == input.token.getFrequency()) {
			j = position+1;
			input.setNotInTokenSet(false);
		}
			
		else {
			j = position;
			input.setNotInTokenSet(true);
		}
		int rindex =0;
		if (j!=-1) {
			for (int k = j; k<input.tokenset.size(); k++) {
				input.subsetr.add(rindex, input.tokenset.get(k));
				rindex++;
			}
		}
		input.setInSearchngRange(true);
		
	}
	
	double suffixFilter (List<Token> x, List<Token> y, double Hmax, int recursiveDepth) {
		if (recursiveDepth > MAXDEPTH) {
			return Math.abs(x.size() - y.size());
		}
		int mid = y.size()/2;
		Token w = y.get(mid);
		int o = (int)(Hmax - Math.abs(x.size() - y.size()))/2;
		int ol = 0;
		int or = 0;
		if (x.size() < y.size()) {
			ol = 1;
			or = 0;
		}
		else {
			ol = 0;
			or = 1;
		}
		TokenSet yw = new TokenSet();
		Collections.sort(y);
		yw.tokenset = y;
		yw.token = w;
		yw.left = mid;
		yw.right = mid;
		partition(yw);
		
		TokenSet xw = new TokenSet();
		Collections.sort(x);
		xw.tokenset = x;
		xw.token = w;
		xw.left = mid - o - Math.abs(x.size() - y.size()) * ol ;
		xw.right = mid + o + Math.abs(x.size() - y.size()) * or;
		partition(xw);
		
		if (xw.isInSearchngRange() == false) {
			return Hmax + 1;
		}
		List<Token> xl = xw.getSubsetl();
		List<Token> yl = yw.getSubsetl();
		List<Token> xr = xw.getSubsetr();
		List<Token> yr = yw.getSubsetr();
		
		int diff;
		
		if (xw.isNotInTokenSet())
			diff = 1;
		else
			diff = 0;
		
		double H = Math.abs(xl.size() - yl.size()) + Math.abs(xr.size() - yr.size()) + diff;
		double Hl, Hr;
		 
		if (H > Hmax)
			return H;
		else {
			Hl = suffixFilter(xl,yl,Hmax - Math.abs(xr.size() - yr.size()) - diff,recursiveDepth+1);
			H = Hl + Math.abs(xr.size() - yr.size()) + diff;
			if (H <= Hmax) {
				Hr = suffixFilter(xr,yr,Hmax - Hl - diff, recursiveDepth+1);
				return Hl + Hr + diff;
			}
			else {
				return H;
			}
		}
		
	}
	
	@SuppressWarnings("null")
	void candidatePairs() {
		Map<Integer,Integer> tokenIndex = new HashMap<Integer,Integer>();
		this.initializeCandidatePairs();
		for (int k=0; k<records.size(); k++) {
			Record recordToCheck = this.records.get(k);
			int prefix = this.prefixLength(recordToCheck, this.threshold);
			double alpha = 0;
			for (int i=0;i<prefix;i++) {
				String t = recordToCheck.tokens.get(i).word;
				tokenIndex = getTokenIndex(t,recordToCheck.id);
				
				//For every entry in inverted index
				for (Map.Entry<Integer, Integer> entry : tokenIndex.entrySet()) {
					
				    int recordId = entry.getValue();
				    Record yrecord = getRecordWithId(recordId);
				    if (yrecord!=null && yrecord.tokens.size() >= (threshold*recordToCheck.tokens.size())) {
				    	alpha = jaccardConstraint(recordToCheck.tokens.size(), yrecord.tokens.size(), this.threshold);
				    	int ubound = 1+Math.min(recordToCheck.tokens.size(), yrecord.tokens.size()) - recordId;
				    	if (this.recordPairs.get(recordToCheck.id)+ubound >= alpha) {
				    		
				    		// Prefix Filtering
							this.recordPairs.put(yrecord.id, yrecord.id+1);
				    		
				    		// Suffix Filtering
				    		/*if (this.recordPairs.get(recordToCheck.id) == 0) { 
				    			double Hmax = recordToCheck.tokens.size() + yrecord.tokens.size() -
				    					2*((threshold/(threshold+1))*(recordToCheck.tokens.size()+yrecord.tokens.size()) - 
				    							(i + recordId - 2));
				    			List<Token> listx = new ArrayList<Token>();
				    			List<Token> listy = new ArrayList<Token>();
				    			for (int b=i+1;b<recordToCheck.tokens.size();b++) {
				    				listx.add(recordToCheck.tokens.get(b));
				    			}
				    			for (int c=recordId+1;c<yrecord.tokens.size();c++) {
				    				listy.add(yrecord.tokens.get(c));
				    			}
				    			
				    			double H = suffixFilter(listx,listy,Hmax,1);
				    			if (H <= Hmax) {
				    				this.recordPairs.put(yrecord.id, yrecord.id+1);
				    			}
				    			else {
				    				this.recordPairs.put(yrecord.id, -1);
				    			}
				    		} */
				    		
						}
						else {
							this.recordPairs.put(yrecord.id, 0);
						}
				    }
				   
				}
			}
			verify(recordToCheck,alpha);
		}
	}
	
	void verify (Record x, double alpha) {
		
		for (Map.Entry<Integer, Integer> entry : this.recordPairs.entrySet()) {
			int yid = entry.getValue();
			int overlap = yid; 
			int ubound;
			if (yid>0) {
				int xprefix = this.prefixLength(x, this.threshold);
				Record y = this.getRecordWithId(yid);
				int yprefix = this.prefixLength(y, this.threshold);
				if (x.tokens.get(xprefix).frequency < y.tokens.get(yprefix).frequency) {
					ubound = yid + x.tokens.size() - xprefix;
					if (ubound >= alpha) {
						overlap += calcByMerge(x,xprefix,y,yid);
					}
				} else {
					ubound = yid + y.tokens.size() - yprefix;
					if (ubound >= alpha) {
						overlap += calcByMerge(x,yid,y,yprefix);
					}
				}
				if (overlap >= alpha) {
					//this.similarRecords.putIfAbsent(x.id, y.id);
					this.similarRecords.put(x.id, y.id);
				}
			}
		} 
	}
	
}
