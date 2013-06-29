package ma2dev.gd.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import ma2dev.gd.data.TelNumber;
import ma2dev.gd.utils.MyRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelNumberTest {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TelNumberTest.class);

	@Test
	public final void test電話番号生成() {
		List<String> prefixList = new ArrayList<>();
		prefixList.add(new String("090"));
		prefixList.add(new String("080"));
		prefixList.add(new String("070"));
		MyRandom random = MyRandom.getInstance();

		TelNumber telNumber = new TelNumber(9, 13, prefixList, random);
		log.debug("gen: {}", telNumber.getValue());
	}
	
	//@Test
	public final void test() {
		String max = Long.toBinaryString(Long.MAX_VALUE);
		String min = Long.toBinaryString(Long.MIN_VALUE);
		long diff = -1-Long.MIN_VALUE;
		System.out.println(diff);
		String diffStr = Long.toBinaryString(diff);
		log.debug("Long.MAX_VALUE: {}, Long.MIN_VALUE: {}, diff:{}, -1: {}", max, min, diffStr, Long.toBinaryString(-1L));
	}
}
