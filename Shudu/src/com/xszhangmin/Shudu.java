package com.xszhangmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shudu {

	private List<List<Integer>> nums = new ArrayList<List<Integer>>();

	private List<IndexStatus> nulllists = new ArrayList<IndexStatus>();

	public List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

	public Shudu(Integer[][] arr) {
		int y = 0;
		for (Integer[] a : arr) {
			nums.add(Arrays.asList(a));
			int x = 0;
			for (Integer tmp : a) {
				if (null == tmp) {
					nulllists.add(new IndexStatus(new Index(x, y)));
				}
				x++;
			}
			y++;
		}
		initNullList();
	}

	/**
	 * 初始化所有空格的可能值
	 */
	public void initNullList() {
		for (IndexStatus is : nulllists) {
			setMaybesForIndexStatus(is);
		}
		Collections.sort(nulllists, new IndexStatusComp());
	}

	/**
	 * 初始化单个空格的可能值
	 * 
	 * @param is
	 */
	private void setMaybesForIndexStatus(IndexStatus is) {
		Index index = is.getIndex();
		is.clear();
		for (Integer in : values) {
			if (sureNot(index, in)) {
				continue;
			}
			is.addMaybeValue(in);
		}
	}

	private List<Integer> getLineFromIndex(Index i) {
		return nums.get(i.getY());
	}

	private List<Integer> getRowFromIndex(Index i) {
		List<Integer> s = new ArrayList<Integer>();
		for (List<Integer> l : nums) {
			s.add(l.get(i.getX()));
		}
		return s;
	}

	private List<Integer> getRoundFromIndex(Index index) {
		List<Integer> s = new ArrayList<Integer>();
		Index top = index.getTop();
		Index bottom = index.getBottem();
		for (int i = 0; i < nums.size(); i++) {
			if (i < bottom.getY() || i > top.getY()) {
				continue;
			}
			List<Integer> l = nums.get(i);
			for (int j = 0; j < l.size(); j++) {
				if (j < bottom.getX() || j > top.getX()) {
					continue;
				}
				s.add(l.get(j));
			}

		}
		return s;
	}

	private List<Index> getRoundNullIndexFromIndex(Index index) {
		List<Index> s = new ArrayList<Index>();
		Index top = index.getTop();
		Index bottom = index.getBottem();
		for (int i = 0; i < nums.size(); i++) {
			if (i < bottom.getY() || i > top.getY()) {
				continue;
			}
			List<Integer> l = nums.get(i);
			for (int j = 0; j < l.size(); j++) {
				if (j < bottom.getX() || j > top.getX()) {
					continue;
				}
				if (i == index.getY() && j == index.getX()) {
					continue;
				}
				if (null == l.get(j)) {
					s.add(new Index(j, i));
				}
			}

		}
		return s;
	}

	public void print() {
		for (List<Integer> l : nums) {
			for (Integer in : l) {
				System.out.print(" " + in);
			}
			System.out.println();
		}
	}

	public void run() throws SelfConficException {
		System.out.println(guess());
	}

	public void caculateUntilNotChange() throws SelfConficException {
		boolean hasChanged = true;
		int oldsize = 0;
		int newsize = 0;
		while (hasChanged) {
			oldsize = nulllists.size();
			caclulte();
			newsize = nulllists.size();
			hasChanged = oldsize != newsize;
		}
	}

	public void caclulte() throws SelfConficException {
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				Integer i = nums.get(y).get(x);
				Index index = new Index(x, y);
				if (null == i) {
					trySetValue(index);
				}
			}
		}
	}

	private void trySetValue(Index index) throws SelfConficException {
		List<Integer> line = getLineFromIndex(index);
		List<Integer> row = getRowFromIndex(index);
		List<Integer> rou = getRoundFromIndex(index);
		int count = 0;
		Integer t = null;
		for (Integer in : values) {
			if (line.contains(in)) {
				continue;
			}
			if (row.contains(in)) {
				continue;
			}
			if (rou.contains(in)) {
				continue;
			}
			if (sure(index, in)) {
				t = in;
				count++;
				break;
			}
			t = in;
			count++;
		}
		if (count == 1) {
			set(index, t);
			System.out.println(index.toString());
			System.out.println(t);
		}
		if (count == 0) {
			throw new SelfConficException();
		}
	}

	private boolean sure(Index index, Integer in) {
		List<Index> inlist = getRoundNullIndexFromIndex(index);
		for (Index tmp : inlist) {
			if (!sureNot(tmp, in)) {
				return false;
			}
		}
		return true;
	}

	private boolean sureNot(Index index, Integer in) {
		List<Integer> line = getLineFromIndex(index);
		List<Integer> row = getRowFromIndex(index);
		List<Integer> rou = getRoundFromIndex(index);
		if (line.contains(in)) {
			// System.out.println("index :" + index + " line:" + line + "
			// contains :" + in);
			return true;
		}
		if (row.contains(in)) {
			// System.out.println("index :" + index + " row:" + row + " contains
			// :" + in);
			return true;
		}
		if (rou.contains(in)) {
			// System.out.println("index :" + index + " rou:" + rou + " contains
			// :" + in);
			return true;
		}
		return false;
	}

	public void set(Index i, Integer v) {
		List<Integer> l = nums.get(i.getY());
		l.set(i.getX(), v);

		IndexStatus tmp = null;
		for (IndexStatus is : nulllists) {
			if (is.getIndex().equals(i)) {
				if (null == v) {
					return;
				}
				tmp = is;
			}
		}
		if (null == v) {
			nulllists.add(new IndexStatus(i));
		}
		nulllists.remove(tmp);
	}

	public Index getIndex(int x, int y) {
		return new Index(x, y);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (List<Integer> l : nums) {
			sb.append(l);
			sb.append(" ");
		}

		return sb.toString();
	}

	public boolean guess() {
		if (null == nulllists || 0 == nulllists.size()) {
			return true;
		}
		initNullList();
		IndexStatus is = nulllists.get(0);
		Index index = is.getIndex();
		Set<Integer> maybeValues = is.getValuesMayBe();
		System.out.println(
				"start to guess index:" + index + " its maybevalues is :" + Arrays.asList(maybeValues.toArray()));
		for (Integer in : maybeValues) {
			if (guessByOne(index, in)) {
				return true;
			}
		}
		System.out.println("guess fail!");
		return false;

	}

	private boolean guessByOne(Index index, Integer in) {
		if (sureNot(index, in)) {
			System.out.println("index:" + index + " must not be :" + in + "! rollback index:" + index + " to null!");
			rollBack(index);
			return false;
		}
		set(index, in);
		System.out.println("Guess index:" + index.toString() + " value:" + in);
		if (sure(index, in) && nulllists.size() == 0) {
			return true;
		}
		initNullList();
		IndexStatus is = nulllists.get(0);
		Index next = is.getIndex();
		Set<Integer> maybeValues = is.getValuesMayBe();
		System.out.println(
				"start to guess index:" + next + " its maybevalues is :" + Arrays.asList(maybeValues.toArray()));
		for (Integer tmp : maybeValues) {
			boolean result = guessByOne(next, tmp);
			if (result) {
				return true;
			}
		}
		System.out.println("index:" + next + " ,its nexts are all wrong! so roll back it!");
		rollBack(index);
		System.out.println("guess " + index + " wrong! rollback :" + index + " to null!");
		rollBack(index);
		return false;
	}

	private void rollBack(Index index) {
		set(index, null);
	}

	class Index {
		private int x;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		private int y;

		public Index(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Index getTop() {
			return new Index(toTopX(x), toTopY(y));
		}

		public Index getBottem() {
			return new Index(toBottemX(x), toBottemY(y));
		}

		private int toBottemY(int y2) {
			return (y2 / 3) * 3;
		}

		private int toBottemX(int x2) {
			return (x2 / 3) * 3;
		}

		private int toTopY(int y2) {
			return (y2 / 3) * 3 + 2;
		}

		private int toTopX(int x2) {
			return (x2 / 3) * 3 + 2;
		}

		public String toString() {
			return "x=" + (x + 1) + " y=" + (y + 1);
		}

		public int hashCode() {
			return (x + 1) * (y + 1);
		}

		public boolean equals(Object i) {
			return (x == ((Index) i).getX() && y == ((Index) i).getY());
		}
	}

	class IndexStatus {
		private Index index;
		private Set<Integer> valuesMayBe = new HashSet<Integer>();

		public IndexStatus(Index index) {
			this.index = index;
		}

		public void clear() {
			valuesMayBe.clear();
		}

		public Index getIndex() {
			return index;
		}

		public void addMaybeValue(Integer in) {
			valuesMayBe.add(in);
		}

		public Set<Integer> getValuesMayBe() {
			return valuesMayBe;
		}

		public int getMaybeNums() {
			return valuesMayBe.size();
		}

		public boolean hasMapbeValues() {
			return valuesMayBe.size() > 0;
		}

		public String toString() {
			return String.format("index :%s valuesMayBe:%s", index, Arrays.asList(valuesMayBe.toArray()));
		}

	}

	class IndexStatusComp implements Comparator<IndexStatus> {

		@Override
		public int compare(IndexStatus i1, IndexStatus i2) {
			return (i1.getMaybeNums() < i2.getMaybeNums()) ? -1 : (i1.getMaybeNums() == i2.getMaybeNums() ? 0 : 1);
		}

	}

	public static void main(String[] args) throws SelfConficException {

		// Integer[][] arrOOO = {
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// {null , null, null , null, null, null, null, null, null},
		// };
		// Integer[][] arr1 = {
		// {null , 5 , null , 8 , 1 , null, null, null, 9 },
		// {8 , null, 2 , null, null, 7 , 1 , null, 5 },
		// {null , 6 , null , 3 , null, null, 7 , null, null},
		// {null , null, null , null, 7 , null, 2 , null, 3 },
		// {null , 1 , null , 5 , null, 6 , null, 8 , null},
		// {3 , null, 7 , null, 9 , null, null, null, null},
		// {null , null, 5 , null, null, 3 , null, 1 , null},
		// {1 , null, 8 , 7 , null, null, 5 , null, 6 },
		// {6 , null, null , null, 2 , 5 , null, 7 , null},
		// };
		Integer[][] arr = { { null, null, null, null, null, null, 8, null, null },
				{ 4, null, null, 2, null, 8, null, 5, 1 }, { null, 8, 3, 9, null, null, null, null, 7 },
				{ null, 4, null, 5, null, null, null, 8, 2 }, { null, null, 5, null, null, null, 4, null, null },
				{ 8, 7, null, null, null, 9, null, 3, null }, { 2, null, null, null, null, 7, 1, 6, null },
				{ 3, 6, null, 1, null, 5, null, null, 4 }, { null, null, 4, null, null, null, null, null, null }, };

		Shudu shudu = new Shudu(arr);
		// Index index = shudu.getIndex(2, 4);
		// shudu.getRoundFromIndex(index);
		shudu.run();
		shudu.print();
	}
}
