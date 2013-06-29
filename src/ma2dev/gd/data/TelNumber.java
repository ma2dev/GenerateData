package ma2dev.gd.data;

import java.util.List;

import ma2dev.gd.generator.IGenerateValue;
import ma2dev.gd.utils.MyRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 電話番号をランダムに生成します。
 * 
 * @author ma2dev
 *
 */
public class TelNumber implements IGenerateValue {
	private static final Logger log = LoggerFactory.getLogger(MyRandom.class);

	/** 電話番号桁数下限値 */
	private int lowDigit;
	/** 電話番号桁数上限値 */
	private int upperDigit;
	/** 電話番号接頭字(市外局番+市内局番などを想定)のリスト */
	private List<String> prefixList;
	/** 乱数生成器 */
	private MyRandom random;

	/**
	 * コンストラクタ
	 * 
	 * @param lowerDigit
	 *            電話番号桁数下限値
	 * @param upperDigit
	 *            電話番号桁数上限値
	 * @param prefixList
	 *            電話番号接頭字のリスト
	 * @param random
	 *            乱数生成器
	 */
	public TelNumber(int lowerDigit, int upperDigit, List<String> prefixList, MyRandom random) {
		this.lowDigit = lowerDigit;
		this.upperDigit = upperDigit;
		this.prefixList = prefixList;
		this.random = random;
		log.debug("lowerDigit: {}, upperDigit: {}", lowerDigit, upperDigit);
	}

	@Override
	public String getValue() {
		StringBuffer genTelNum = new StringBuffer();

		// 桁数の決定
		int digit = random.getInteger(lowDigit, upperDigit);

		// prefixの決定
		int index = random.getInteger(0, prefixList.size() - 1);
		String prefix = prefixList.get(index);
		genTelNum.append(prefix);

		// 乱数で生成する残り桁数の算出
		int suffixLength = digit - prefix.length();

		// 残り桁の数字列生成
		long pow = (long) Math.pow(10, suffixLength) - 1; // 残り桁での最大値(5桁の場合100000-1=99999)
		long suffixNumber = random.getLong(1, pow); // オール0の電話番号は無しとする

		// 0パディング
		String suffixString = String.format("%0" + suffixLength + "d", suffixNumber);
		genTelNum.append(suffixString);

		log.debug("digit: {}, prefix[{}]: {}, suffixMax[{}]: {}, suffixNumber: {}, paded: {}, result: {}", digit,
				index, prefix, suffixLength, pow, suffixNumber, suffixString, genTelNum.toString());

		// 返却
		return genTelNum.toString();
	}
}
