package ma2dev.gd.utils;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyRandom {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MyRandom.class);

	/** 乱数ジェネレータ．1つしか存在しない(Singleton)． */
	private static MyRandom instanceOfMyRandom;

	private Random random;

	private MyRandom() {
		random = new Random();
	}

	private MyRandom(long seed) {
		random = new Random(seed);
	}

	/**
	 * 乱数の種をセットする．
	 * 
	 * @param seed
	 *            乱数の種
	 */
	public void setSeed(long seed) {
		random.setSeed(seed);
	}

	/**
	 * TMyRandom型のインスタンスを得る．<BR>
	 * 乱数の種は，このメソッド実行時の時間により決定する．
	 * 
	 * @return TMyRandomのインスタンス
	 */
	public static MyRandom getInstance() {
		if (instanceOfMyRandom == null) {
			instanceOfMyRandom = new MyRandom();
		}
		return instanceOfMyRandom;
	}

	/**
	 * TMyRandom型のインスタンスを得る．<BR>
	 * 乱数の種は，seedになる．
	 * 
	 * @param seed
	 *            乱数の種
	 * @return TMyRandomのインスタンス
	 */
	public static MyRandom getInstance(long seed) {
		if (instanceOfMyRandom == null) {
			instanceOfMyRandom = new MyRandom(seed);
		} else {
			instanceOfMyRandom.setSeed(seed);
		}
		return instanceOfMyRandom;
	}

	/**
	 * min < = randomDouble < = max となる実数乱数randomDoubleを返す．
	 * 
	 * @param min
	 *            乱数の最小値
	 * @param max
	 *            乱数の最大値
	 * @return 実数乱数
	 */
	public double getDouble(double min, double max) {
		double randomDouble = 0.0;
		randomDouble = random.nextDouble();
		randomDouble *= (max - min);
		randomDouble += min;
		return randomDouble;
	}

	/**
	 * min <= randomInt <= max となる整数乱数randomIntを返す．
	 * 
	 * @param min
	 *            乱数の最小値
	 * @param max
	 *            乱数の最大値
	 * @return 整数乱数
	 */
	public int getInteger(int min, int max) {
		int randomInt = random.nextInt(max - min + 1);
		randomInt += min;
		return randomInt;
	}

	/**
	 * min <= randomLong <= max となる整数乱数randomLongを返す．
	 * 
	 * @param min
	 *            乱数の最小値
	 * @param max
	 *            乱数の最大値
	 * @return 整数乱数
	 */
	public long getLong(long min, long max) {
		long randomInt = nextLong(max - min + 1);
		randomInt += min;
		return randomInt;
	}

	/**
	 * {@link Random#nextInt(int)}のlong版<br>
	 * 乱数ジェネレータのシーケンスを使って、0 から指定された値の範囲 (0 は含むが、その指定された値は含まない) で一様分布の long
	 * 型の擬似乱数を返します。
	 * 
	 * @param n
	 *            返される乱数の限界値。正の値でなければならない
	 * @return 乱数ジェネレータのシーケンスに基づく、0 (これを含む) から n (これを含まない) の範囲の一様分布の log
	 *         型の次の擬似乱数値
	 * @throws IllegalArgumentException
	 *             n が正でない場合
	 */
	private long nextLong(long n) {
		if (n <= 0) {
			throw new IllegalArgumentException("n must be positive");
		}

		long bits, val;
		do {
			bits = (random.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	/**
	 * 正規乱数を返す．
	 * 
	 * @param mean
	 *            平均値
	 * @param sigma
	 *            標準偏差
	 * @return 正規乱数
	 */
	public double getNormalDistributedNumber(double mean, double sigma) {
		return mean + sigma * random.nextGaussian();
	}

}
