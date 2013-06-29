package ma2dev.gd.generator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import ma2dev.gd.data.TelNumber;
import ma2dev.gd.utils.MyRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriberGeneratorTest {
	private static final Logger log = LoggerFactory.getLogger(SubscriberGeneratorTest.class);

	@Test
	public final void test電話番号生成() {
		List<String> prefixList = new ArrayList<>();
		prefixList.add(new String("03"));
		prefixList.add(new String("06"));
		prefixList.add(new String("044"));
		MyRandom random = MyRandom.getInstance();
		TelNumber telNumber = new TelNumber(9, 10, prefixList, random);

		SubscriberGenerator generator = new SubscriberGenerator(telNumber);
		generator.generate(10);
		List<String> list = generator.getSubscriberList();

		for (int i = 0; i < list.size(); i++) {
			log.debug("TelNum[{}]: {}", i, list.get(i));
		}
	}

}
