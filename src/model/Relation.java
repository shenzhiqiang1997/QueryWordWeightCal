package model;

public class Relation {
	private String name;
	private Word word1;
	private Word word2;
	private int d;
	private double R;


	public Relation(String name, Word word1, Word word2, int d) {
		super();
		this.name = name;
		this.word1 = word1;
		this.word2 = word2;
		this.d = d;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Word getWord1() {
		return word1;
	}

	public void setWord1(Word word1) {
		this.word1 = word1;
	}

	public Word getWord2() {
		return word2;
	}

	public void setWord2(Word word2) {
		this.word2 = word2;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public double getR() {
		return R;
	}

	public void setR(double r) {
		R = r;
	}

	@Override
	public String toString() {
		return "Relation [name=" + name + ", word1=" + word1 + ", word2=" + word2 + ", d=" + d + ", R=" + R + "]";
	}

	
	

}
