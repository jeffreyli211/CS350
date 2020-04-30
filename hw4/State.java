package hw4;

import java.util.LinkedList;

public class State {
	/**
	 * Queuing system state
	 */

	/* The request list. */
	public LinkedList<Request> queue;

	/* Used to generate the ID of the next request. */
	//private int nextId;

	/**
	 * Simulation statistics
	 */

	/* Used to calculate the utilization. */
	public double busyTime;
	public double busyTimeP1;
	public double busyTimeP2;

	/* Number of completed requests per server during the simulation. */
	public double numCompletedRequests;

	/* Total request time per server. */
	public double totalRequestTime;

	/* Number of monitor events, each monitor event will record the queue length. */
	public double numMonitorEvents;

	/* Total queue length per server. */
	public double totalQueueLen;

	/* Processors 1 and 2 in server 1. */
	public Request Proc1;
	public Request Proc2;
	public int currProcessor;

	public State() {
		queue = new LinkedList<Request>();
		busyTime = 0;
		busyTimeP1 = 0;
		busyTimeP2 = 0;
		numCompletedRequests = 0;
		numMonitorEvents = 0;
		totalRequestTime = 0;
		totalQueueLen = 0;
		Proc1 = null;
		Proc2 = null;
		currProcessor = 1;
	}

	/*
	public State(boolean multi_proc) {
		queue = new LinkedList<Request>();
		busyTimeP1 = 0;
		busyTimeP2 = 0;
		numCompletedRequests = 0;
		totalRequestTime = 0;
		totalQueueLen = 0;
		Proc1 = null;
		Proc2 = null;
		currProcessor = 1;
	}

	public State(double queue_limit) {
		queue = new LinkedList<Request>();
		busyTime = 0;
		numCompletedRequests = 0;
		totalRequestTime = 0;
		totalQueueLen = 0;
	}
	*/

	/**
	 * Get next request ID.
	 */
	/*
	public int getNextId() {
		return nextId++;
	}
	*/
}