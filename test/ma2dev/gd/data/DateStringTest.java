package ma2dev.gd.data;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ma2dev.gd.data.DateString;
import ma2dev.gd.utils.MyRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateStringTest {
	private static final Logger log = LoggerFactory.getLogger(DateStringTest.class);

	@Test
	public final void test日付取得() {
		Calendar startCal = new GregorianCalendar();
		Calendar endCal = new GregorianCalendar();
		startCal.clear();
		endCal.clear();

		startCal.set(2013, 6 - 1, 29, 0, 0, 0);
		endCal.set(2013, 6 - 1, 29, 23, 59, 59);

		Date start = startCal.getTime();
		Date end = endCal.getTime();

		log.debug("Start Date[{}]: {}", start.getTime(), start.toString());
		log.debug("  End Date[{}]: {}", end.getTime(), end.toString());

		DateString dateString = new DateString(start, end, MyRandom.getInstance());
		String result = dateString.getValue();

		log.debug("result: {}", result);
	}

}
