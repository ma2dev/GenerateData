package ma2dev.gd.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 契約者(発番号に該当)の電話番号リストを生成します。
 * 
 * @author ma2dev
 * 
 */
public class SubscriberGenerator {

	private static final Logger log = LoggerFactory.getLogger(SubscriberGenerator.class);

	private List<String> list;
	/** 重複チェック用一時データ */
	private Map<String, String> map;
	private IGenerateValue telNumber;

	/**
	 * コンストラクタ
	 * 
	 * @param telNumber
	 *            電話番号生成器
	 */
	public SubscriberGenerator(IGenerateValue telNumber) {
		list = null;
		map = new HashMap<>();
		this.telNumber = telNumber;
	}

	/**
	 * 電話番号リストを生成します。
	 * 
	 * @param n
	 *            生成数
	 * @return リスト
	 * @throws IllegalArgumentException
	 *             n が正でない場合
	 */
	public void generate(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException("n must be positive");
		}

		for (int i = 0; i < n; i++) {
			String key = telNumber.getValue();
			String result = map.put(key, "");
			if (result != null) {
				// 重複していた場合は再度生成
				i--;
				log.debug("collision: {}", key);
			}
		}

		list = new ArrayList<String>(map.keySet());
	}

	/**
	 * 契約者電話番号のリストを取得します。
	 * 
	 * @return 契約者電話番号のリスト。生成していない場合は null を返却します。
	 */
	public List<String> getSubscriberList() {
		if (list == null) {
			log.error("list is null. must be executed before SubscriberGenerator#generate(int).");
		}

		return list;
	}
}
