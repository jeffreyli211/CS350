package hw3;

public class Simulator {
	/* Average arrival rate of requests (requests per millisecond). */
	static double lambda;
	static int k;
	/* Average service times at the primary and secondary servers (milliseconds). */
	static double S0_Ts;
	static double S1_Ts;
	static double S0_mu;
	static double S1_mu;
	static double dropped = 0;

	public static void simulate(double time) {
		Controller.runSimulation(time, lambda, S0_Ts, S1_Ts, k);
	}

	public static void main(String args[]) {
		/* Simulation time (milliseconds). */
		double time = Double.parseDouble(args[0]);

		/* Average arrival rate of requests (requests per millisecond). */
		lambda = Double.parseDouble(args[1]);

		/* Average service times at the primary and secondary servers (milliseconds). */
		S0_Ts = Double.parseDouble(args[2]);
		S1_Ts = Double.parseDouble(args[4]);
		k = Integer.parseInt(args[3]);
		S0_mu = 1/S0_Ts;
		S1_mu = 1/S1_Ts;

		simulate(time);
	}
}
