package ma2dev.gd.generator;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ma2dev.gd.data.Reason;
import ma2dev.gd.utils.MyRandom;
import ma2dev.gd.utils.csv.Csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 呼情報テストデータ生成器
 * 
 * @author ma2dev
 * 
 */
public class CallInformationGenerator {
	private static final Logger log = LoggerFactory.getLogger(MyRandom.class);

	private List<String> subscriberList;
	private List<String> allTelNumList;

	private IGenerateValue telNumber;
	private IGenerateValue dateString;
	private IGenerateValue reason;

	private MyRandom random;
	private Csv csv;

	/** ペア生成用時刻幅(ミリ秒) */
	private long bufferTime;
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	private static final int CALLINFO_SRCTELNUM_INDEX = 0;
	private static final int CALLINFO_DSTTELNUM_INDEX = 1;
	private static final int CALLINFO_STARTTIME_INDEX = 2;
	private static final int CALLINFO_ENDTIME_INDEX = 3;
	private static final int CALLINFO_REASON_INDEX = 4;

	// 異常切断時のパターン
	private static final int REASON_ERROR_P1_NONTELNUM = 0;
	private static final int REASON_ERROR_P2_SRCTELONLY = 1;
	private static final int REASON_ERROR_P3_TELNUMPAIR = 2;

	/** サンプルデータ用の電話番号リストのサイズ(契約者電話番号に対する割合 */
	private static final double TELNUMBERLIST_TIMES = 2.0;

	/**
	 * コンストラクタ
	 * 
	 * @param subscriberList
	 *            契約電話番号リスト
	 * @param telNumber
	 *            電話番号生成器
	 * @param dateString
	 *            日付情報生成器
	 * @param reason
	 *            切断要因生成器
	 * @param bufferTime
	 *            時刻生成時の±時刻(ミリ秒)
	 * @param random
	 *            乱数生成器
	 */
	public CallInformationGenerator(List<String> subscriberList, IGenerateValue telNumber, IGenerateValue dateString,
			IGenerateValue reason, long bufferTime, MyRandom random) {
		csv = new Csv();
		this.subscriberList = Objects.requireNonNull(subscriberList, "subscriberList must not be null.");

		this.telNumber = Objects.requireNonNull(telNumber, "telNumber must not be null.");
		this.dateString = Objects.requireNonNull(dateString, "dateString must not be null.");
		this.reason = Objects.requireNonNull(reason, "reason must not be null.");

		this.bufferTime = bufferTime;

		this.random = Objects.requireNonNull(random, "random must not be null.");

		// 電話番号サンプルデータの生成
		this.allTelNumList = generateAllTelNumList();
	}

	/**
	 * データ生成
	 * 
	 * @param n
	 *            データ件数
	 */
	public void generate(int n) {
		String reasonStr = null;
		for (int i = 0; i < n; i++) {
			// 発信者電話番号
			// 着信者電話番号
			String[] telnums = generateTelNumberPair();
			csv.setCell(i, CALLINFO_SRCTELNUM_INDEX, telnums[0]);
			csv.setCell(i, CALLINFO_DSTTELNUM_INDEX, telnums[1]);

			// 開始時刻
			// 切断時刻
			String[] dates = generateDatePair();
			// 切断要因
			reasonStr = reason.getValue();

			if (dates == null) {
				// 時刻情報生成に失敗している場合は異常切断パターンに落とす
				csv.setCell(i, CALLINFO_STARTTIME_INDEX, "");
				csv.setCell(i, CALLINFO_ENDTIME_INDEX, "");
				csv.setCell(i, CALLINFO_REASON_INDEX, Reason.REASON_VALUE_ERROR);

				log.warn("generateDatePair error: {}, {}, {}, {}, {}", telnums[0], telnums[1], "", "",
						Reason.REASON_VALUE_ERROR);
				continue;
			}

			if (reasonStr.equals(Reason.REASON_VALUE_NORMAL)) {
				// 正常切断
				csv.setCell(i, CALLINFO_STARTTIME_INDEX, dates[0]);
				csv.setCell(i, CALLINFO_ENDTIME_INDEX, dates[1]);
			} else {
				// 異常切断
				// パターンの決定(パターンは一様乱数で決定するものとする)
				int pattern = random.getInteger(REASON_ERROR_P1_NONTELNUM, REASON_ERROR_P3_TELNUMPAIR);
				switch (pattern) {
				case REASON_ERROR_P1_NONTELNUM:
					// 時刻情報無し
					csv.setCell(i, CALLINFO_STARTTIME_INDEX, "");
					csv.setCell(i, CALLINFO_ENDTIME_INDEX, "");
					break;
				case REASON_ERROR_P2_SRCTELONLY:
					// 終了だけ無し(予期せぬ終了?)
					csv.setCell(i, CALLINFO_STARTTIME_INDEX, dates[0]);
					csv.setCell(i, CALLINFO_ENDTIME_INDEX, "");
					break;
				case REASON_ERROR_P3_TELNUMPAIR:
					// 情報とも時刻情報有り
					csv.setCell(i, CALLINFO_STARTTIME_INDEX, dates[0]);
					csv.setCell(i, CALLINFO_ENDTIME_INDEX, dates[1]);
					break;
				default:
					log.error("reason pattern error. [{}]", pattern);
					break;
				}
				log.debug("reason pattern: {}", pattern);
			}
			csv.setCell(i, CALLINFO_REASON_INDEX, reasonStr);
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
			list.add(telNumber.getValue());
		}

		return list;
	}

	/**
	 * 電話番号のペアを生成します。
	 * 
	 * @return 電話番号文字列のペア
	 */
	private String[] generateTelNumberPair() {
		String[] pair = new String[2];

		// 1つ目は契約者電話番号から取得
		int index = random.getInteger(0, subscriberList.size() - 1);
		String first = subscriberList.get(index);

		// 2つ目は契約者電話番号リストもしくは新規に生成した電話番号から取得
		String second = null;
		do {
			index = random.getInteger(0, allTelNumList.size() - 1);
			second = allTelNumList.get(index);
		} while (first.equals(second)); // ペアの重複は許容しない

		pair[0] = first;
		pair[1] = second;
		log.debug("p0: {}, p1: {}", pair[0], pair[1]);

		return pair;
	}

	/**
	 * 日付文字列のペアを生成します。
	 * 
	 * @return 日付文字列のペア
	 */
	private String[] generateDatePair() {
		String[] pair = new String[2];

		// baseとなる日付情報を1つ生成する
		// baseとなる日付は必ず通話の収集対象日となることを期待する
		String baseDate = dateString.getValue();
		String pairDate = null;
		do {
			pairDate = generateDate(baseDate, bufferTime);
			if (pairDate == null) {
				// 日付情報の生成失敗
				// リトライしても駄目なのでやめる(バグのみ)
				log.error("generateDate error. - baseDate: {}, bufferTime: {}", baseDate, bufferTime);
				return null;
			}
		} while (baseDate.equals(pairDate)); // 同一日時にはしない

		if (baseDate.compareTo(pairDate) < 0) {
			// baseDate -> pairDate の日付順
			pair[0] = baseDate;
			if (isNextDay(baseDate, pairDate)) {
				log.debug("isNextDay is true: {}, {}", baseDate, pairDate);
				// 翌日の日付になっているので、通話中状態として終了時刻に該当する日付は空とする
				pair[1] = "";
			} else {
				pair[1] = pairDate;
			}
			log.debug("execute isNextDay: {}, {} (->)", baseDate, pairDate);
		} else {
			log.debug("no execute isNextDay: {}, {} (<-)", baseDate, pairDate);
			// pairDate -> baseDate の日付順
			pair[0] = pairDate;
			pair[1] = baseDate;
		}

		return pair;
	}

	/**
	 * 基底となる日付文字列から特定範囲の日付文字列を生成します。
	 * 
	 * @param s
	 *            基底となる日付文字列
	 * @param n
	 *            範囲
	 * @return 生成した文字列を返却します。生成に失敗した場合は null を返却します。
	 */
	private String generateDate(String s, long n) {
		// long値への変換
		long time = 0;
		try {
			time = df.parse(s).getTime();
		} catch (ParseException e) {
			log.error("Parse error. [{}]", s, e);
			return null;
		}

		long start = time - n;
		long end = time + n;
		long result = random.getLong(start, end);

		Date toDate = new Date(result);
		String toStr = df.format(toDate);

		return toStr;
	}

	/**
	 * yyyyMMddHHmmss の文字列で与えられた日付が異なる日かどうかを判定します。
	 * 
	 * @param a
	 *            基準となる日付
	 * @param b
	 *            判定する日付
	 * @return 異なれば true を、異ならなければ false を返却します。
	 */
	private boolean isNextDay(String a, String b) {
		String ymd1 = a.substring(0, 8);
		String ymd2 = b.substring(0, 8);

		if (!ymd1.equals(ymd2)) {
			log.debug("return=true - original: {}, {}, sub: {}, {}", a, b, ymd1, ymd2);
			return true;
		} else {
			log.debug("return=false - original: {}, {}, sub: {}, {}", a, b, ymd1, ymd2);
			return false;
		}
	}
}
