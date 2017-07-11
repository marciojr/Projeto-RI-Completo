package processor;

public class Tuple {

	private Integer docId;
	private double score;

	public Tuple(Integer docId, Integer score) {
		this.docId = docId;
		this.score = score;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double frequencyValue) {
		this.score = score;
	}

}
