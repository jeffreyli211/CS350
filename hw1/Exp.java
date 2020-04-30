import java.lang.Math;

public class Exp {

	public static double getExp(double lambda) {
		double y = Math.random();
		double x = (-1.0 * Math.log(1-y))/lambda;
		return x;
	}

	public static void main(String[] args) {
		if (args.length == 2) {
			double lam = Double.valueOf(args[0]);
			int n = Integer.valueOf(args[1]);
			while (n != 0) {
				double val = getExp(lam);
				System.out.println(val);
				n--;
			}
		}
		else {
			System.out.println("Error: Incorrect number of parameters.");
		}
	}
}
