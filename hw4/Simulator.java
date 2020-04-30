package hw4;

import java.util.*;

public class Simulator {
	/* Average arrival rate of requests (requests per millisecond). */
	static double lambda;
	/* Average service times at the primary and secondary servers (milliseconds). */
	static double Ts0;
	static double Ts1;
	static double Ts2;

	/* PMF for server 3. */
	static List<Prob> PMF = new ArrayList<Prob>();

	/* Server limit of server 2. */
	static double K2;

	/* Route probabiltiies for server 0 & server 3. */
	static double[] S0Routes = new double[2];
	static double[] S3Routes = new double[3];

	/* Number of requests. */
	static int num_Requests = 0;

	/* Number of dropped requests. */
	static double dropped = 0;

	public static void simulate(double time) {
		Controller.runSimulation(time, lambda, Ts0, Ts1, Ts2, PMF, K2);
	}

	public static void main(String args[]) {
		/* Simulation time (milliseconds). */
		double time = Double.parseDouble(args[0]);

		/* Average arrival rate of requests (requests per millisecond). */
		lambda = Double.parseDouble(args[1]);

		/* Average service times for servers 0, 1, 2. */
		Ts0 = Double.parseDouble(args[2]);
		Ts1 = Double.parseDouble(args[3]);
		Ts2 = Double.parseDouble(args[4]);

		/* Create PMF for service time at server 3. */
		/* (t1,p1) */
		Prob pmf1 = new Prob(Double.parseDouble(args[5]), Double.parseDouble(args[6]));
		/* (t2,p2) */
		Prob pmf2 = new Prob(Double.parseDouble(args[7]), pmf1.getProb() + Double.parseDouble(args[8]));
		/* (t3,p3) */
		Prob pmf3 = new Prob(Double.parseDouble(args[9]), pmf2.getProb() + Double.parseDouble(args[10]));
		PMF.add(pmf1);
		PMF.add(pmf2);
		PMF.add(pmf3);

		/* Server 2 size limit. */
		K2 = Double.parseDouble(args[11]);

		/* Create routing probabilities. */
		S0Routes[0] = Double.parseDouble(args[12]);
		S0Routes[1] = S3Routes[0] + Double.parseDouble(args[13]);

		S3Routes[0] = Double.parseDouble(args[14]);
		S3Routes[1] = S3Routes[0] + Double.parseDouble(args[15]);
		S3Routes[2] = S3Routes[1] + Double.parseDouble(args[16]);
		simulate(time);
	}
}
