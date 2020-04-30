package hw4;

import java.util.*;

public class Controller {

	/**
	 * initialize the schedule with a birth event and a monitor event
	 * 
	 * @return a schedule with two events
	 */
	public static PriorityQueue<Event> initSchedule() {
		PriorityQueue<Event> schedule = new PriorityQueue<Event>();

		schedule.add(new Event(Event.getTimeOfNextBirth(), EventType.BIRTH));
		schedule.add(new Event(Event.getTimeOfNextMonitor(), EventType.MONITOR));

		return schedule;
	}

	/* ============================================================================================================ */
	/* Helper functions to calculate the statistics */
	public static double calc_Util(State server, double simT) {
		return server.busyTime / simT;
	}

	public static double calc_S1Util(State server, int processor, double simT) {
		if (processor == 1) {
			return server.busyTimeP1 / simT;
		}
		else {
			return server.busyTimeP2 / simT;
		}
	}

	public static double calc_avgQLen(State master, State server) {
		return server.totalQueueLen / master.numMonitorEvents;
	}

	public static double calc_avgRespT(State server) {
		return server.totalRequestTime / server.numCompletedRequests;
	}
	/* ============================================================================================================ */

	/* Randomly select a service time from the PMF. */
	public static double pickTs() {
		double drawProb = Math.random();
		if (drawProb <= Simulator.PMF.get(0).getProb()) {
			return Simulator.PMF.get(0).getTime();
		}
		else if (drawProb <= Simulator.PMF.get(1).getProb()) {
			return Simulator.PMF.get(1).getTime();
		}
		else {
			return Simulator.PMF.get(2).getTime();
		}
	}

	public static int pickS0Route() {
		double drawProb = Math.random();
		/*
		for (int i = 0; i < probs.length; i++) {
			if (chooseServer <= probs[i]) {
				return i;
			}
		}
		*/
		if (drawProb <= Simulator.S0Routes[0]) {
			return 1;
		}
		else {
			return 2;
		}
	}

	public static int pickS3Route() {
		double drawProb = Math.random();
		if (drawProb <= Simulator.S3Routes[0]) {
			return -1;	// -1 indicates to leave system
		}
		if (drawProb <= Simulator.S3Routes[1]) {
			return 1;
		}
		else {
			return 2;
		}
	}

	public static void runSimulation(double simulationTime, double lambda, double Ts0, double Ts1, double Ts2, List<Prob> PMF, double K2) {
		/**
		 * declare the data structures that hold the state of the system
		 */
		State MASTER = new State();
		State state0 = new State();
		State state1 = new State();
		State state2 = new State();
		State state3 = new State();

		PriorityQueue<Event> schedule = initSchedule();
		double time = 0, maxTime = simulationTime;
		while (time < maxTime) {
			Event event = schedule.remove();
			time = event.getTime();
			event.function(schedule, MASTER, state0, state1, state2, state3, time, K2);
		}

		/**
		 * output the statistics over the simulated system
		 */
		System.out.println("");
		System.out.printf("S0 UTIL: %f\n", calc_Util(state0, simulationTime));
		System.out.printf("S0 QLEN: %f\n", calc_avgQLen(MASTER, state0));
		System.out.printf("S0 TRESP: %f\n", calc_avgRespT(state0));
		System.out.println("");
		System.out.printf("S1,1 UTIL: %f\n", calc_S1Util(state1, 1, simulationTime));
		System.out.printf("S1,2 UTIL: %f\n", calc_S1Util(state1, 2, simulationTime));
		System.out.printf("S1 QLEN: %f\n", calc_avgQLen(MASTER, state1));
		System.out.printf("S1 TRESP: %f\n", calc_avgRespT(state1));
		System.out.println("");
		System.out.printf("S2 UTIL: %f\n", calc_Util(state2, simulationTime));
		System.out.printf("S2 QLEN: %f\n", calc_avgQLen(MASTER, state2));
		System.out.printf("S2 TRESP: %f\n", calc_avgRespT(state2));
		System.out.printf("S2 DROPPED: %f\n", Simulator.dropped);
		System.out.println("");
		System.out.printf("S3 UTIL: %f\n", calc_Util(state3, simulationTime));
		System.out.printf("S3 QLEN: %f\n", calc_avgQLen(MASTER, state3));
		System.out.printf("S3 TRESP: %f\n", calc_avgRespT(state3));
		System.out.println("");
		System.out.printf("QTOT: %f\n", (state0.totalQueueLen + state1.totalQueueLen + state2.totalQueueLen + state3.totalQueueLen) / MASTER.numMonitorEvents);
		System.out.printf("TRESP: %f\n", calc_avgRespT(MASTER));
	}
}
