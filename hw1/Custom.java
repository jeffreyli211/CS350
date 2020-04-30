import java.lang.Math;

public class Custom {

	public static double getCustom(double [] outcomes, double [] probabilities) {
		int n = outcomes.length;
		double[] CDF = new double[n];
		CDF[0] = probabilities[0];
		for (int i = 1; i < n; i++) {
			CDF[i] = probabilities[i] + CDF[i-1];
		}
		
		double randVal = Math.random();
		double outcome = 0.0;
		for (int j = 0; j < n; j++) {
			if (randVal <= CDF[j]) {
				outcome = outcomes[j];
				break;
			}
		}
		return outcome;
	}

	public static void main(String[] args) {
		if (args.length == 11) {
			double[] values = new double[5];
			double[] probs = new double[5];
			for (int i = 0; i < 5; i++) {
				values[i] = Double.valueOf(args[2*i]);
				probs[i] = Double.valueOf(args[(2*i)+1]);
			}

			int n_times = Integer.valueOf(args[10]);
			while (n_times != 0) {
				double ret = getCustom(values, probs);
				System.out.println(ret);
				n_times--;
			}
		}
		else {
			System.out.println("Error: Incorrect number of parameters.");
		}
	}

}
