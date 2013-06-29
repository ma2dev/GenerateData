package ma2dev.gd.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ma2dev.gd.utils.MyRandom;
import ma2dev.gd.utils.csv.Csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInformationGenerator {

	private static final Logger log = LoggerFactory.getLogger(ServiceInformationGenerator.class);

	private Csv csv;
	private List<String> subscriberList;
	private List<String> allTelNumList;

	private IGenerateValue telNumber;
	private IGenerateValue service;
	private MyRandom random;

	/** サンプルデータ用の電話番号リストのサイズ(契約者電話番号に対する割合 */
	private static final double TELNUMBERLIST_TIMES = 2.0;

	private static final int SERVICEINFO_SRCTELNUM_INDEX = 0;
	private static final int SERVICEINFO_DISPLAYSERVICE_INDEX = 1;
	private static final int SERVICEINFO_INTERRUPTSERVICE_INDEX = 2;
	private static final int SERVICEINFO_FAMILYFREESERVICE_START_INDEX = 3;
	private static final int SERVICEINFO_FAMILYFREESERVICE_MAX_NUM = 10;

	private static final double RANDOM_MEAN = 4.0;
	private static final double RANDOM_SIGMA = 3.0;

	/**
	 * コンストラクタ
	 * 
	 * @param subscriberList
	 *            契約電話番号リスト
	 * @param telNumber
	 *            電話番号生成器
	 * @param service
	 *            サービス契約状態生成器
	 * @param random
	 *            乱数生成器
	 */
	public ServiceInformationGenerator(List<String> subscriberList, IGenerateValue telNumber, IGenerateValue service,
			MyRandom random) {
		csv = new Csv();
		this.subscriberList = Objects.requireNonNull(subscriberList, "subscriberList must not be null.");

		this.telNumber = Objects.requireNonNull(telNumber, "telNumber must not be null.");
		this.service = Objects.requireNonNull(service, "service must not be null.");

		this.random = Objects.requireNonNull(random, "random must not be null.");

		// 電話番号サンプルデータの生成
		this.allTelNumList = generateAllTelNumList();
	}

	/**
	 * データ生成<br>
	 * 生成件数は契約電話番号リスト分
	 */
	public void generate() {
		for (int i = 0; i < subscriberList.size(); i++) {
			// 契約電番
			csv.setCell(i, SERVICEINFO_SRCTELNUM_INDEX, subscriberList.get(i));

			// 番号表示サービス
			String display = service.getValue();
			csv.setCell(i, SERVICEINFO_DISPLAYSERVICE_INDEX, display);

			// 割り込み通話
			String interrupt = service.getValue();
			csv.setCell(i, SERVICEINFO_INTERRUPTSERVICE_INDEX, interrupt);

			// 家族無料通話
			List<String> familyFreeList = generateFamilyFree(subscriberList.get(i), RANDOM_MEAN, RANDOM_SIGMA);
			for (int j = 0; j < familyFreeList.size(); j++) {
				int column = j + SERVICEINFO_FAMILYFREESERVICE_START_INDEX;
				csv.setCell(i, column, familyFreeList.get(j));
			}
		}
	}

	/**
	 * ファイル出力
	 * 
	 * @param writer
	 *            出力先
	 * @throws IOException
	 *             ファイル出力で異常が発生した場合
	 */
	public void write(Writer writer) throws IOException {
		csv.write(writer);
	}

	@Override
	public String toString() {
		return csv.toString();
	}

	/**
	 * サンプルデータ用の電話番号リストの生成<br>
	 * このリストは、契約者電話番号のリストに加えて、新規電話番号を追加したリストとする。<br>
	 * 新規に加える電話番号は他の電話番号と重複していても問題ないものとする(ダミーのため)。
	 * 
	 * @return 電話番号文字列のリスト
	 */
	private List<String> generateAllTelNumList() {
		List<String> list = new ArrayList<>();
		list.addAll(subscriberList);

		// リストのサイズを確定
		int listSize = (int) ((double) subscriberList.size() * TELNUMBERLIST_TIMES);
		int newGenSize = listSize - subscriberList.size();

		log.debug("AllTelNumListSize: {}, subscriberListSize: {}, newGenSize: {}", listSize, subscriberList.size(),
				newGenSize);

		for (int i = 0; i < newGenSize; i++) {
			String s = telNumber.getValue();
			log.debug("add telnum: {}", s);
			list.add(s);
			// list.add(telNumber.getValue());
		}

		return list;
	}

	/**
	 * 家族無料通話対象番号の生成
	 * 
	 * @param subscriber
	 *            契約者電話番号(この番号以外の番号が生成される)
	 * @param mean
	 *            正規乱数に使用する平均
	 * @param sigma
	 *            正規乱数に使用するσ
	 * @return 家族無料通話対象番号のリスト
	 */
	private List<String> generateFamilyFree(String subscriber, double mean, double sigma) {
		int telCnt = 0;
		do {
			double value = random.getNormalDistributedNumber(mean, sigma); // 件数は正規分布に従う
			telCnt = (int) value;
			log.debug("getNormalDistributedNumber - value: {}, toInt: {}", value, telCnt);
		} while (telCnt < 0 || SERVICEINFO_FAMILYFREESERVICE_MAX_NUM < telCnt);
		// telCnt は 0 < telCnt < SERVICEINFO_FAMILYFREESERVICE_MAX_NUM に納める

		log.debug("telNumber cnt: {}", telCnt);

		return generateTelNumbers(telCnt, subscriber);
	}

	private List<String> generateTelNumbers(int n, String ignoreNumber) {
		List<String> list = new ArrayList<>();

		int maxSize = allTelNumList.size();
		for (int i = 0; i < n; i++) {
			int index = random.getInteger(0, maxSize - 1);
			String tel = allTelNumList.get(index);
			log.debug("choice telNumber: {}", tel);
			if (ignoreNumber.equals(tel)) {
				// やり直し
				i--;
				log.debug("collision telNumber - ignore: {}, tel: {}", ignoreNumber, tel);
				continue;
			}
			if (list.contains(tel)) {
				// やり直し
				i--;
				log.debug("collision telNumber (list) - tel: {}", tel);
				continue;
			}
			list.add(tel);
		}

		log.debug("n: {}, listSize: {}", n, list.size());

		return list;
	}
}
