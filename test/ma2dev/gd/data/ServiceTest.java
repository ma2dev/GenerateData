package ma2dev.gd.data;

import static org.junit.Assert.*;
import ma2dev.gd.utils.MyRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTest {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ServiceTest.class);

	@Test
	public final void testサービス契約有無生成() {
		Service service = new Service(50.0, MyRandom.getInstance());
		service.getValue();
	}

}
