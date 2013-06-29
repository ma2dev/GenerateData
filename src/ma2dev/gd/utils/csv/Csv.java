package ma2dev.gd.utils.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CSVファイルを表現します。<br>
 * コンストラクタでコンフィグを設定可能です。
 *
 * @author ma2dev
 *
 */
public class Csv {

	private CsvConfiguration config;
	private List<List<IData>> cellArray;

	/**
	 * コンストラクタ<br>
	 * 設定されるコンフィグはデフォルトです。
	 *
	 */
	public Csv() {
		this(new CsvConfiguration());
	}

	/**
	 * コンストラクタ<br>
	 * コンフィグを設定できます。
	 *
	 * @param config
	 *            コンフィグ
	 */
	public Csv(CsvConfiguration config) {
		this.config = Objects.requireNonNull(config, "config must not be null.");
		cellArray = new ArrayList<List<IData>>();
	}

	/**
	 * コンフィグを取得します。
	 *
	 * @return コンフィグ
	 */
	public CsvConfiguration getConfiguration() {
		return config;
	}

	/**
	 * 行と列を指定して情報を取得します。<br>
	 * 該当の指定箇所にデータが無い場合、nullを返却します。
	 *
	 * @param row
	 *            行
	 * @param column
	 *            列
	 * @return 情報
	 */
	public IData getCell(int row, int column) {
		IData cell = null;
		try {
			List<IData> line = cellArray.get(row);
			cell = line.get(column);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		return cell;
	}

	/**
	 * 行を指定して情報をListとして取得します。<br>
	 * 該当の指定行にデータが無い場合、nullを返却します。
	 *
	 * @param row
	 *            行番号
	 * @return 行の情報のcellのList
	 */
	public List<IData> getCells(int row) {
		List<IData> list = null;
		try {
			list = cellArray.get(row);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		return list;
	}

	/**
	 * 行数を取得します。
	 *
	 * @return 行数
	 */
	public int getRowSize() {
		return cellArray.size();
	}

	/**
	 * 行と列を指定して情報を設定します。<br>
	 * 行および列の値は0から始まります。<br>
	 * 指定された行・列に情報がすでに存在していた場合には、情報は置き換えられます。<br>
	 * 指定された行・列とすでに設定済みの情報の間に空のセルが存在する場合、該当のセルには空文字が設定されます。<br>
	 * すなわち、row:3, colum:3, data:"x"を下記のようなcsvオブジェクトに対して設定した場合、<br>
	 * [a][b]<br>
	 * 以下のようなcsvオブジェクトとなります。<br>
	 * [a][b]<br>
	 * []<br>
	 * [] [] [z]<br>
	 *
	 * @param row
	 *            行
	 * @param column
	 *            列
	 * @param data
	 *            情報
	 */
	public void setCell(int row, int column, String data) {
		if (row < 0) {
			throw new IllegalArgumentException("row must not be less than zero.");
		}
		if (column < 0) {
			throw new IllegalArgumentException("column must not be less than zero.");
		}

		IData inputData = new Cell(Objects.requireNonNull(data, "config must not be null."));

		List<IData> targetRow = null;
		if (cellArray.size() <= row) {
			// 設定対象の行がまだ無い場合
			targetRow = new ArrayList<IData>();

			// 空行の構築
			for (int i = cellArray.size(); i < row; i++) {
				List<IData> emptyData = new ArrayList<IData>();
				emptyData.add(new Cell(""));
				cellArray.add(emptyData);
			}
			cellArray.add(targetRow);
		} else {
			// 設定対象の行がすでにある場合
			targetRow = cellArray.get(row);
		}

		// 列データの構築
		if (targetRow.size() <= column) {
			// 設定対象の列がまだ無い場合

			// 空列の構築
			for (int i = targetRow.size(); i < column; i++) {
				IData emptyData = new Cell("");
				targetRow.add(emptyData);
			}
			targetRow.add(inputData);
		} else {
			// 設定対象の列がすでにある場合
			targetRow.set(column, inputData);
		}
	}

	/**
	 * Readerから読み込みます。
	 *
	 * @param reader
	 *            Readerオブジェクト
	 * @throws IOException
	 *             読み込みに失敗した場合
	 */
	public void read(Reader reader) throws IOException {
		String line;
		LineNumberReader lineNumberReader = new LineNumberReader(Objects.requireNonNull(reader,
				"reader must not be null."));
		while ((line = lineNumberReader.readLine()) != null) {
			cellArray.add(getCellOfLine(line));
		}
	}

	/**
	 * Writerに書き込みます。<br>
	 * 最終行に改行は入りません。
	 *
	 * @param writer
	 *            Writerオブジェクト
	 * @throws IOException
	 *             書き込みに失敗した場合
	 */
	public void write(Writer writer) throws IOException {
		BufferedWriter bw = new BufferedWriter(Objects.requireNonNull(writer, "writer must not be null."));
		bw.write(toString());
		bw.flush();
	}

	/**
	 * csv形式の文字列として出力します。<br>
	 * 最終行には改行は入りません。
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < cellArray.size(); i++) {
			sb.append(toStringOfLine(i));
			if (i != cellArray.size() - 1) {
				// 最終行以外は改行を入れる
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	/**
	 * 1行のcsv形式の文字列を分解し、Cellのリストとして返却します。
	 *
	 * @param line
	 *            1行のcsv形式の文字列
	 * @return Cellのリスト
	 */
	private List<IData> getCellOfLine(String line) {
		List<IData> list = new ArrayList<IData>();

		IData data = null;
		for (String s : line.split(config.getDelimiter(), config.getColumnLimit())) {
			data = new Cell(s);
			list.add(data);
		}

		return list;
	}

	/**
	 * 指定した行をcsv形式の文字列として返却します。
	 *
	 * @param rowNumber
	 *            指定行
	 * @return csv形式の文字列
	 */
	private String toStringOfLine(int rowNumber) {
		StringBuffer sb = new StringBuffer();

		List<IData> line = cellArray.get(rowNumber);
		for (int i = 0; i < line.size(); i++) {
			sb.append(line.get(i).getData());

			if (i != line.size() - 1) {
				// 行末以外はデリミタを入れる
				sb.append(config.getDelimiter());
			}
		}

		return sb.toString();
	}

}
