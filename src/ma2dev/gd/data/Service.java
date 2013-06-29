package ma2dev.gd.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma2dev.gd.generator.IGenerateValue;
import ma2dev.gd.utils.MyRandom;

/**
 * サービス契約情報をランダムに生成します。<br>
 * 契約有無のみの情報を持つサービスにのみ適用可能です。
 * 
 * @author ma2dev
 * 
 */
public class Service implements IGenerateValue {
	private static final Logger log = LoggerFactory.getLogger(Service.class);

	/** 契約有り */
	public static final String SERVICE_ON = "1";
	/** 契約無し */
	public static final String SERVICE_OFF = "0";

	private double probability;
	private MyRandom random;

	/**
	 * コンストラクタ<br>
	 * 
	 * @param probability
	 *            契約率(%)
	 * @param random
	 *            乱数生成器
	 */
	public Service(double probability, MyRandom random) {
		this.probability = probability / 100.0;
		this.random = random;
	}

	@Override
	public String getValue() {
		double randValu = random.getDouble(0.0, 1.0);
		String result = null;
		if (0.0 <= randValu && randValu <= probability) {
			result = SERVICE_ON;
		} else {
			result = SERVICE_OFF;
		}
		log.debug("result[{}%]: {}", probability * 100.0, result);

		return result;
	}

}
