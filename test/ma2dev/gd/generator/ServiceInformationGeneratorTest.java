package ma2dev.gd.generator;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ma2dev.gd.data.Service;
import ma2dev.gd.data.TelNumber;
import ma2dev.gd.utils.MyRandom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInformationGeneratorTest {
	private static final Logger log = LoggerFactory.getLogger(ServiceInformationGeneratorTest.class);
	private static List<String> prefixList;

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
	public final void testデータ生成() {

		String dayStr = "20130629";
		MyRandom random = MyRandom.getInstance();

		TelNumber telNumber = new TelNumber(10, 10, prefixList, random);
		SubscriberGenerator subscriberGenerator = new SubscriberGenerator(telNumber);
		subscriberGenerator.generate(10);
		List<String> subscriberList = subscriberGenerator.getSubscriberList();

		Service service = new Service(40.0, random);

		ServiceInformationGenerator generator = new ServiceInformationGenerator(subscriberList, telNumber, service,
				random);
		generator.generate();
		String result = generator.toString();

		log.debug("csv : \n----- \n{}\n-----", result);

		Writer w;
		try {
			w = new FileWriter("dat/" + dayStr + "-service_info.csv");
			generator.write(w);
			w.write("\n");
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
