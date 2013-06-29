package ma2dev.gd.data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import ma2dev.gd.data.Reason;
import ma2dev.gd.utils.MyRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReasonTest {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ReasonTest.class);

	@Test
	public final void test切断要因生成() {

		Reason reason = new Reason(0.0, MyRandom.getInstance());
		assertThat(reason.getValue(), is("0"));

		reason = new Reason(0.1, MyRandom.getInstance());
		reason.getValue();

		reason = new Reason(50.0, MyRandom.getInstance());
		reason.getValue();

		reason = new Reason(100.0, MyRandom.getInstance());
		assertThat(reason.getValue(), is("1"));
	}
}
