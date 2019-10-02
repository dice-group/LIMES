package org.aksw.limes.core.measures.measure.phoneticmeasure;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class PhoneticMeasureTest {

	@Test
	public void testCaverphone1() throws Exception {
		Caverphone1Measure caver1 = new Caverphone1Measure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(caver1.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	@Test
	public void testCaverphone2() throws Exception {
		Caverphone2Measure caver2 = new Caverphone2Measure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(caver2.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	
	
	@Test
	public void testMetaphoneMeasure() throws Exception {
		MetaphoneMeasure metaphone = new MetaphoneMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(metaphone.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	@Test
	public void testDoubleMetaphoneMeasure() throws Exception {
		DoubleMetaphoneMeasure doublemetaphone = new DoubleMetaphoneMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(doublemetaphone.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	
	

	@Test
	public void testRefinedSoundexMeasure() throws Exception {
		RefinedSoundexMeasure rsoundex = new RefinedSoundexMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(rsoundex.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	@Test
	public void testNysiisMeasure() throws Exception {
		NysiisMeasure nysiis = new NysiisMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(nysiis.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	
	
	@Test
	public void testMatchRatingApproachEncoderMeasure() throws Exception {
		MatchRatingApproachEncoderMeasure match = new MatchRatingApproachEncoderMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(match.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	@Test
	public void testKoelnPhoneticMeasure() throws Exception {
		KoelnPhoneticMeasure koeln = new KoelnPhoneticMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(koeln.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	@Test
	public void testSoundexMeasure() throws Exception {
		SoundexMeasure soundex = new SoundexMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(soundex.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
	@Test
	public void testDaitchMokotoffSoundexMeasure() throws Exception {
		DaitchMokotoffSoundexMeasure daitch = new DaitchMokotoffSoundexMeasure();

		Set<String> a = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Mohammed");
		Set<String> b = new HashSet<>();
		a.add("Abdullah");
		a.add("Ahmed");
		a.add("Ali");
		for(String str1: a) {
			for(String str2: b) {
				assertEquals(daitch.getSimilarity(str1, str2), 0.5d, 0.d);
			}
		}
	}
}
