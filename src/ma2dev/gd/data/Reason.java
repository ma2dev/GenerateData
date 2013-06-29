package ma2dev.gd.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma2dev.gd.generator.IGenerateValue;
import ma2dev.gd.utils.MyRandom;

/**
 * 切断要因をランダムに生成します。
 * 
 * @author ma2dev
 * 
 */
public class Reason implements IGenerateValue {
	private static final Logger log = LoggerFactory.getLogger(Reason.class);

	/** 正常切断 */
	public static final String REASON_VALUE_NORMAL = "0";
	/** 異常切断 */
	public static final String REASON_VALUE_ERROR = "1";

	private MyRandom random;
	private double probability;

	/**
	 * コンストラクタ<br>
	 * 
	 * @param probability
	 *            異常切断の発生確率(%)
	 * @param random
	 *            乱数生成器
	 */
	public Reason(double probability, MyRandom random) {
		this.probability = probability / 100.0;
		this.random = random;
	}

	@Override
	public String getValue() {
		String result = REASON_VALUE_NORMAL;
		double randValu = random.getDouble(0.0, 1.0);
		if (0.0 <= randValu && randValu <= probability) {
			// 異常切断
			result = REASON_VALUE_ERROR;
		} else {
			result = REASON_VALUE_NORMAL;
		}

		log.debug("result[{}%]: {}", probability * 100.0, result);

		return result;
	}
}
