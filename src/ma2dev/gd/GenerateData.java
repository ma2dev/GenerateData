package ma2dev.gd;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ma2dev.gd.data.DateString;
import ma2dev.gd.data.Reason;
import ma2dev.gd.data.Service;
import ma2dev.gd.data.TelNumber;
import ma2dev.gd.generator.CallInformationGenerator;
import ma2dev.gd.generator.IGenerateValue;
import ma2dev.gd.generator.ServiceInformationGenerator;
import ma2dev.gd.generator.SubscriberGenerator;
import ma2dev.gd.utils.MyRandom;
import ma2dev.gd.utils.csv.Csv;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateData {
	private static final Logger log = LoggerFactory.getLogger(GenerateData.class);

	public static final String COUNT_OPTION_CHAR = "n";
	public static final String PREFIX_OPTION_CHAR = "i";
	public static final String OUTPUT_OPTION_CHAR = "o";
	public static final String PROPERTIES_OPTION_CHAR = "p";

	/** 電話番号桁数下限値 */
	private int lowerDigit;
	/** 電話番号桁数上限値 */
	private int upperDigit;

	/** 契約者数 */
	private int subscriberNum;
	/** 通話件数 */
	private int callInfoNum;

	/** 異常切断率(%) */
	private double reasonProbability;
	/** サービス契約率 */
	private double serviceProbability;

	/** 最大通話時間 */
	private long bufferTime;

	public GenerateData() {
		lowerDigit = 10;
		upperDigit = 10;
		subscriberNum = 10000;
		callInfoNum = subscriberNum * 10;

		reasonProbability = 0.1; // 0.1%
		serviceProbability = 40.0; // 40.0%

		bufferTime = 60 * 60 * 1000; // 1h
	}

	public boolean execute(String prefixFile, String outputDir, String propertiesFile) {
		boolean result = true;

		List<String> prefixList = null;
		try {
			prefixList = genPrefixList(prefixFile);
		} catch (IOException e) {
			log.error("genPrefixList error.\n{}", e);
			System.out.println("プログラムは異常終了しました。");
			System.exit(-1);
		}
		MyRandom random = MyRandom.getInstance();

		IGenerateValue telNumber = new TelNumber(lowerDigit, upperDigit, prefixList, random);
		SubscriberGenerator subscriberGenerator = new SubscriberGenerator(telNumber);
		subscriberGenerator.generate(subscriberNum);
		List<String> subscriberList = subscriberGenerator.getSubscriberList();

		Date[] dateArray = null;
		try {
			dateArray = getTodayDate();
		} catch (ParseException e) {
			log.error("getTodayDate error.\n{}", e);
			System.out.println("プログラムは異常終了しました。");
			return false;
		}
		IGenerateValue dateString = new DateString(dateArray[0], dateArray[1], random);

		IGenerateValue reason = new Reason(reasonProbability, random);

		CallInformationGenerator callInformationGenerator = new CallInformationGenerator(subscriberList, telNumber,
				dateString, reason, bufferTime, random);

		IGenerateValue service = new Service(serviceProbability, random);
		ServiceInformationGenerator serviceInformationGenerator = new ServiceInformationGenerator(subscriberList,
				telNumber, service, random);

		// データ生成
		callInformationGenerator.generate(callInfoNum);
		serviceInformationGenerator.generate();

		// ファイル出力
		try {
			String todayStr = getTodayString();

			Writer callInfoWriter = new FileWriter(outputDir + "/" + todayStr + "-call_info.csv");
			callInformationGenerator.write(callInfoWriter);
			callInfoWriter.write("\n");
			callInfoWriter.close();

			Writer serviceInfoWriter = new FileWriter(outputDir + "/" + todayStr + "-service_info.csv");
			serviceInformationGenerator.write(serviceInfoWriter);
			serviceInfoWriter.write("\n");
			serviceInfoWriter.close();
		} catch (ParseException e) {
			log.error("{}", e);
			result = false;
		} catch (IOException e) {
			log.error("{}", e);
			result = false;
		}

		return result;
	}

	/**
	 * prefix ファイルを読み込んでListに詰めます。
	 * 
	 * @param file
	 *            prefixファイル
	 * @return list
	 * @throws IOException
	 *             読み込み失敗
	 */
	private List<String> genPrefixList(String file) throws IOException {
		Csv csv = new Csv();
		Reader reader = new FileReader(file);
		csv.read(reader);
		reader.close();

		List<String> list = new ArrayList<>();
		for (int i = 0; i < csv.getRowSize(); i++) {
			String s = (String) csv.getCell(i, 0).getData();
			if (s == null) {
				log.error("prefix date read error.");
				return null;
			}

			list.add(s);
		}

		return list;
	}

	/**
	 * 本日の00:00:00と23:59:59を返却します。
	 * 
	 * @return date
	 * @throws ParseException
	 *             パース失敗
	 */
	private Date[] getTodayDate() throws ParseException {
		Calendar cal = GregorianCalendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

		Date[] dateArray = new Date[2];
		String s = String.format("%d%02d%02d", year, month, day);
		dateArray[0] = df.parse(s + "000000");
		dateArray[1] = df.parse(s + "235959");

		log.debug("date base: {}", s);

		return dateArray;
	}

	/**
	 * yyyyMMddを返却します。
	 * 
	 * @return date
	 * @throws ParseException
	 *             パース失敗
	 */
	private String getTodayString() throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date d = new Date();
		String result = df.format(d);

		log.debug("date(yyyyMMdd): {}", result);

		return result;
	}

	/**
	 * @param args
	 *            オプションについては実行時に表示されるUsage参照。
	 */
	public static void main(String[] args) {
		String msg = null;

		msg = "プログラムの実行を開始します。";
		System.out.println(msg);
		log.info(msg);

		// オプションチェック ---------------------------------------------------------
		Options options = new Options();

		// prefixファイルのオプション
		@SuppressWarnings("static-access")
		Option prefix = OptionBuilder // OptionBuilder
				.hasArg(true) // オプションの後にパラメータが必須か
				.withDescription("prefixファイルを指定します。") // Usage出力用の説明
				.withArgName("prefixfile") // パラメータ名
				.withLongOpt("prefix") // オプションの別名
				.create(PREFIX_OPTION_CHAR); // オプション作成
		prefix.setRequired(true);
		options.addOption(prefix);

		// 出力先のオプション
		@SuppressWarnings("static-access")
		Option outputdirOpt = OptionBuilder // OptionBuilder
				.hasArg(true) // オプションの後にパラメータが必須か
				.withDescription("データ出力先ディレクトリを指定します。") // Usage出力用の説明
				.withArgName("outputdir") // パラメータ名
				.withLongOpt("output") // オプションの別名
				.create(OUTPUT_OPTION_CHAR); // オプション作成
		outputdirOpt.setRequired(true);
		options.addOption(outputdirOpt);

		// プロパティファイルのオプション
		@SuppressWarnings("static-access")
		Option propertiesfileOpt = OptionBuilder // OptionBuilder
				.hasArg(true) // オプションの後にパラメータが必須か
				.withDescription("プロパティファイルを指定します。") // Usage出力用の説明
				.withArgName("properties") // パラメータ名
				.withLongOpt("prop") // オプションの別名
				.create(PROPERTIES_OPTION_CHAR); // オプション作成
		// propertiesfileOpt.setRequired(true); // TODO とりあえず今は使わない
		options.addOption(propertiesfileOpt);

		CommandLineParser parser = new BasicParser();
		CommandLine commandLine = null;

		try {
			commandLine = parser.parse(options, args);

		} catch (org.apache.commons.cli.ParseException e) {
			// オプションの指定が誤っている場合
			showUsage(options);
			return;
		}

		// 「-p」の場合
		String prefixFile = null;
		if (commandLine.hasOption(PREFIX_OPTION_CHAR)) {
			// 引数を取得
			prefixFile = commandLine.getOptionValue(PREFIX_OPTION_CHAR);

			File file = new File(prefixFile);
			msg = "指定されたprefixファイルは存在しません。";
			if (file.exists() == false || file.isFile() == false) {
				// ファイル無し
				System.err.println(msg);
				log.error(msg);
				return;
			}
		}

		// 「-o」の場合
		String outputDir = null;
		if (commandLine.hasOption(OUTPUT_OPTION_CHAR)) {
			// 引数を取得
			outputDir = commandLine.getOptionValue(OUTPUT_OPTION_CHAR);

			File file = new File(outputDir);
			msg = "指定されたディレクトリは存在しません。";
			if (file.exists() == false || file.isDirectory() == false) {
				// ディレクトリ無し
				System.err.println(msg);
				log.error(msg);
				return;
			}
		}

		// 「-p」の場合
		String propertiesFile = null;
		if (commandLine.hasOption(PROPERTIES_OPTION_CHAR)) {
			// 引数を取得
			propertiesFile = commandLine.getOptionValue(PROPERTIES_OPTION_CHAR);

			File file = new File(propertiesFile);
			msg = "指定されたプロパティファイルは存在しません。";
			if (file.exists() == false || file.isFile() == false) {
				// ファイル無し
				System.err.println(msg);
				log.error(msg);
				return;
			}
		}

		// main --------------------------------------------------------------
		GenerateData generateData = new GenerateData();
		boolean result = generateData.execute(prefixFile, outputDir, propertiesFile);

		if (result == true) {
			msg = "プログラムは正常終了しました。";
			System.out.println(msg);
			log.info(msg);
		} else {
			msg = "プログラムは異常終了しました。";
			System.out.println(msg);
			log.error(msg);
		}
	}

	/**
	 * Usage出力
	 * 
	 * @param options
	 *            オプション情報
	 */
	private static void showUsage(Options options) {
		HelpFormatter help = new HelpFormatter();
		// ヘルプを出力
		help.printHelp("GenerateData", options, true);
	}
}
