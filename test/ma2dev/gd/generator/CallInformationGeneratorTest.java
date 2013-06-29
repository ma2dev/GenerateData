package ma2dev.gd.generator;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma2dev.gd.data.DateString;
import ma2dev.gd.data.Reason;
import ma2dev.gd.data.TelNumber;
import ma2dev.gd.utils.MyRandom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallInformationGeneratorTest {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CallInformationGeneratorTest.class);

	private static List<String> prefixList;
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		prefixList = new ArrayList<>();
		prefixList.add("03");
		prefixList.add("0422");
		prefixList.add("042");
		prefixList.add("042");
		prefixList.add("042");
		prefixList.add("042");
		prefixList.add("042");
		prefixList.add("0428");
		prefixList.add("04");
		prefixList.add("042");
		prefixList.add("043");
		prefixList.add("0436");
		prefixList.add("0438");
		prefixList.add("0439");
		prefixList.add("044");
		prefixList.add("045");
		prefixList.add("0460");
		prefixList.add("046");
		prefixList.add("0463");
		prefixList.add("0465");
		prefixList.add("0466");
		prefixList.add("0467");
		prefixList.add("046");
		prefixList.add("0470");
		prefixList.add("0470");
		prefixList.add("04");
		prefixList.add("04");
		prefixList.add("047");
		prefixList.add("047");
		prefixList.add("0475");
		prefixList.add("0475");
		prefixList.add("0476");
		prefixList.add("0478");
		prefixList.add("0479");
		prefixList.add("0479");
		prefixList.add("048");
		prefixList.add("048");
		prefixList.add("0480");
		prefixList.add("048");
		prefixList.add("048");
		prefixList.add("049");
		prefixList.add("0493");
		prefixList.add("0494");
		prefixList.add("0495");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test() {
		TelNumber telNumber = new TelNumber(10, 10, prefixList, MyRandom.getInstance());

		SubscriberGenerator subscriberGenerator = new SubscriberGenerator(telNumber);
		subscriberGenerator.generate(10);
		List<String> subscriberList = subscriberGenerator.getSubscriberList();
		if (subscriberList == null) {
			log.error("subscriberList is null.");
		}

		String dayStr = "20130629";

		Date start = null;
		Date end = null;
		try {
			start = df.parse(dayStr + "000000");
			end = df.parse(dayStr + "235959");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		DateString dateString = new DateString(start, end, MyRandom.getInstance());

		Reason reason = new Reason(1.0, MyRandom.getInstance());

		long bufferTime = 1 * 60 * 60 * 1000; // 1時間

		CallInformationGenerator generator = new CallInformationGenerator(subscriberList, telNumber, dateString,
				reason, bufferTime, MyRandom.getInstance());

		generator.generate(10);
		log.debug("csv : \n----- \n{}\n-----", generator.toString());

		Writer w;
		try {
			w = new FileWriter("dat/" + dayStr + "-call_info.csv");
			generator.write(w);
			w.write("\n");
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
