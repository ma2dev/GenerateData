package ma2dev.gd.utils.csv;

/**
 * CSVの設定を管理します。
 *
 * @author ma2dev
 *
 */
public class CsvConfiguration {

	private String delimiter;
	private int columnLimit;

	/**
	 * コンストラクタ<br>
	 * 設定値は以下のデフォルトが使用されます。<br>
	 * デリミタ: ","<br>
	 * 列数上限値: -1 (上限無し)
	 */
	public CsvConfiguration() {
		delimiter = ","; // default
		columnLimit = -1; // csvの列数上限値を設定
	}

	/**
	 * コンストラクタ<br>
	 * デリミタを指定できます。<br>
	 * 他の設定値はデフォルト
	 *
	 * @param delimiter
	 *            デリミタとなる文字列
	 */
	public CsvConfiguration(String delimiter) {
		this();
		setDelimiter(delimiter);
	}

	/**
	 * コンストラクタ<br>
	 * CSVの列数上限値を設定できます。<br>
	 * 他の設定値はデフォルト
	 *
	 * @param columnLimit
	 *            列数上限値<br>
	 *            上限値は{@link String#split(String, int)}
	 *            の第2引数と同様です。上限を設定しない場合は負値を設定します。
	 */
	public CsvConfiguration(int columnLimit) {
		this();
		setColumnLimit(columnLimit);
	}

	/**
	 * コンストラクタ<br>
	 * デリミタおよびCSVの列数上限値を設定できます。
	 *
	 * @param delimiter
	 *            デリミタとなる文字列
	 * @param columnLimit
	 *            列数上限値<br>
	 *            上限値は{@link String#split(String, int)}
	 *            の第2引数と同様です。上限を設定しない場合は負値を設定します。
	 */
	public CsvConfiguration(String delimiter, int columnLimit) {
		this();
		setDelimiter(delimiter);
		setColumnLimit(columnLimit);
	}

	/**
	 * 現在のデリミタを取得します。
	 *
	 * @return デリミタ
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * デリミタを設定します。
	 *
	 * @param delimiter
	 *            デリミタ
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * CSVの列数上限値を取得します。
	 *
	 * @return 列数上限値<br>
	 *         上限値は{@link String#split(String, int)}
	 *         の第2引数と同様です。負値の場合は上限が無いことを意味します。
	 */
	public int getColumnLimit() {
		return columnLimit;
	}

	/**
	 * CSVの列数上限値を設定します。
	 *
	 * @param columnLimit
	 *            列数上限値<br>
	 *            上限値は{@link String#split(String, int)}
	 *            の第2引数と同様です。上限を設定しない場合は負値を設定します。
	 */
	public void setColumnLimit(int columnLimit) {
		this.columnLimit = columnLimit;
	}

}
