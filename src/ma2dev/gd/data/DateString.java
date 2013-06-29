package ma2dev.gd.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import ma2dev.gd.generator.IGenerateValue;
import ma2dev.gd.utils.MyRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日付を yyyyMMddHHmmss のフォーマットでランダムに生成します。
 * 
 * @author ma2dev
 * 
 */
public class DateString implements IGenerateValue {
	private static final Logger log = LoggerFactory.getLogger(DateString.class);

	private Date startDate;
	private Date endDate;
	private MyRandom random;

	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * コンストラクタ
	 * 
	 * @param start
	 *            開始日
	 * @param end
	 *            終了日
	 * @param random
	 *            乱数生成器
	 */
	public DateString(Date start, Date end, MyRandom random) {
		this.startDate = start;
		this.endDate = end;
		this.random = random;
	}

	@Override
	public String getValue() {
		long startValue = startDate.getTime();
		long endValue = endDate.getTime();

		long resultValue = random.getLong(startValue, endValue);
		Date resutlDate = new Date(resultValue);

		String resultString = df.format(resutlDate);

		log.debug("startLong: {}, endLong: {}, resultLong: {}, resultStr: {}", startValue, endValue, resultValue,
				resultString);

		return resultString;
	}
}
