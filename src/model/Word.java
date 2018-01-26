package model;

public class Word {
	private String name;
	private int di;

	public Word(String name, int di) {
		super();
		this.name = name;
		this.di = di;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDi() {
		return di;
	}

	public void setDi(int di) {
		this.di = di;
	}

	@Override
	public String toString() {
		return "Word [name=" + name + ", di=" + di + "]";
	}
	
}
