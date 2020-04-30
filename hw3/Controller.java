package hw3;

import java.util.PriorityQueue;

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
	public static double calc_Util(double busyT, double simT) {
		return busyT / simT;
	}

	public static double calc_avgQLen(double len, double events) {
		return len / events;
	}

	public static double calc_avgRespT(double S0_reqT, double S1_reqT, double S0_reqs, double S1_reqs) {
		double total_reqT = S0_reqT + S1_reqT;
		double total_reqs = S0_reqs + S1_reqs;
		return total_reqT / total_reqs;
	}
	/* ============================================================================================================ */

	public static void runSimulation(double simulationTime, double lambda, double Ts0, double Ts1, int k) {
		/**
		 * declare the data structures that hold the state of the system
		 */
		State S0_State = new State();
		State S1_State = new State();
		PriorityQueue<Event> schedule = initSchedule();
		double time = 0, maxTime = simulationTime;
		while (time < maxTime) {
			Event event = schedule.remove();
			time = event.getTime();
			event.function(schedule, S0_State, S1_State, time, k);
		}

		/**
		 * output the statistics over the simulated system
		 */
		System.out.printf("UTIL 0: %f\n", calc_Util(S0_State.busyTime, simulationTime));
		System.out.printf("UTIL 1: %f\n", calc_Util(S1_State.busyTime, simulationTime));
		System.out.printf("QLEN 0: %f\n", calc_avgQLen(S0_State.totalQueueLen, S0_State.numMonitorEvents));
		System.out.printf("QLEN 1: %f\n", calc_avgQLen(S1_State.totalQueueLen, S1_State.numMonitorEvents));
		System.out.printf("TRESP: %f\n", calc_avgRespT(S0_State.totalRequestTime, S1_State.totalRequestTime, S0_State.numCompletedRequests, S1_State.numCompletedRequests));
		System.out.println("REDIRECTED: " + Simulator.dropped);
	}
}
