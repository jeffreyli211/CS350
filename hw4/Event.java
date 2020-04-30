package hw4;

import java.util.PriorityQueue;

public class Event implements Comparable<Event> {
    /**
     * Time that the event happens
     */
    private double time;
    
    private EventType eventType;
    
    public EventType gettype() {
    	return eventType;
    }
    
    public double getTime() {
    	return time;
	}
	
	public int pickRoute(int server) {
		if (server == 0) {
			return Controller.pickS0Route();
		}
		else {		// server == 3
			return Controller.pickS3Route();
		}
	}

	public void switchProcessor(State s) {
		if (s.currProcessor == 1) {
			s.currProcessor = 2;
		}
		else {
			s.currProcessor = 1;
		}
	}

	public int nextReqID() {
		Simulator.num_Requests += 1;
		return Simulator.num_Requests;
	}

	/**
	 * @param time when the event starts
	 * @param eventType Monitor or Birth or Death
	 */
	public Event(double time, EventType eventType) {
		this.time = time;
		this.eventType = eventType;
	}

    /**
     * 
     * @param schedule contains all the scheduled future events
     * @param state of the simulation (request queue, logging info, etc.)
     * @param timestamp current time in the discrete simulation
     */

	/* ============================================================================================================ */
	/* Helper functions to print arrivals, starts, finishes, and redirections */
	public static void printArrival(Request r) {
        System.out.println("R" + Integer.toString(r.getId()) + " ARR: " + Double.toString(r.getArrivalTime()));
    }

    public static void printStart(Request r, int server) {
		System.out.println("R" + Integer.toString(r.getId()) + " START S" + Integer.toString(server) + ": " + Double.toString(r.getStartServiceTime()));
	}
	public static void printStartS1(Request r, int processor) {
		if (processor == 1) {
			System.out.println("R" + Integer.toString(r.getId()) + " START S1, 1" + ": " + Double.toString(r.getStartServiceTime()));
		}
		else {
			System.out.println("R" + Integer.toString(r.getId()) + " START S1, 2" + ": " + Double.toString(r.getStartServiceTime()));
		}
    }

    public static void printFinish(Request r, int server) {
		System.out.println("R" + Integer.toString(r.getId()) + " DONE S" + Integer.toString(server) + ": " + Double.toString(r.getFinishServiceTime()));
	}
	
	public static void printFinishS1(Request r, int processor) {
		if (processor == 1) {
			System.out.println("R" + Integer.toString(r.getId()) + " DONE S1, 1" + ": " + Double.toString(r.getFinishServiceTime()));
		}
		else {
			System.out.println("R" + Integer.toString(r.getId()) + " DONE S1, 2" + ": " + Double.toString(r.getFinishServiceTime()));
		}
    }

	public static void printDrop(Request r) {
		System.out.println("R" + Integer.toString(r.getId()) + " DROP S2: " + Double.toString(r.getArrivalTime()));
	}

	public static void printToFrom(Request r, int src, int dest) {
		System.out.println("R" + Integer.toString(r.getId()) + " FROM S" + Integer.toString(src) + " TO S" + Integer.toString(dest) + ": " + Double.toString(r.getArrivalTime()));
	}
	/* ============================================================================================================ */
    
    public void function(PriorityQueue<Event> schedule, State MASTER, State s0, State s1, State s2, State s3, double timestamp, double K) {	
		schedule.remove(this);
		double nextTime;
		switch (eventType) {
		case DEATH:
			
	    	/**
		     * remove the record of the request from the data structure of requests in the system
		     * Also, collect and compute some statistics.
	    	 */
		    Request req = s0.queue.remove();
		    req.setFinishServiceTime(timestamp);
	    
		    s0.totalRequestTime += req.getTq();
		    s0.busyTime += req.getTs();
		    s0.numCompletedRequests++;

		    printFinish(req, 0);
		    /**
		     * look for another blocked event in the queue that wants to execute and schedule it's death.
		     * at this time the waiting request enters processing time.
	    	 */
			int choice = pickRoute(0);
			/* Route from Server 0 goes to Server 1. */
			if (choice == 1) {
				System.out.println("R" + Integer.toString(req.getId()) + " FROM S0 TO S1: " + Double.toString(req.getFinishServiceTime()));
				req.setArrivalTime(timestamp);
				s1.queue.add(req);
				if (s1.Proc1 == null) {
					if (s1.Proc2 == null) {
						switchProcessor(s1);
						if (s1.currProcessor == 1) {
							s1.Proc2 = s1.queue.remove();
							printStartS1(req, 2);
							req.setStartServiceTime(timestamp);
							nextTime = timestamp + getTimeOfNextDeath(1);
							schedule.add(new Event(nextTime, EventType.SERVER1P2));
						}
						else {
							s1.Proc1 = s1.queue.remove();
							printStartS1(req,1);
							req.setStartServiceTime(timestamp);
							nextTime = timestamp + getTimeOfNextDeath(1);
							schedule.add(new Event(nextTime, EventType.SERVER1P1));
						}
					}
					else if (s1.queue.size() != 0) {
						s1.Proc1 = s1.queue.remove();
						printStartS1(req,1);
						req.setStartServiceTime(timestamp);
						nextTime = timestamp + getTimeOfNextDeath(1);
						schedule.add(new Event(nextTime, EventType.SERVER1P1));
					}
				}
				else if (s1.Proc1 != null && s1.Proc2 == null) {
					s1.Proc2 = s1.queue.remove();
					printStartS1(req, 2);
					req.setStartServiceTime(timestamp);
					nextTime = timestamp + getTimeOfNextDeath(1);
					schedule.add(new Event(nextTime, EventType.SERVER1P2));
				}
			}
			/* Route goes from Server 0 to Server 2. */
			else {
				req.setArrivalTime(timestamp);
				printToFrom(req, 0, 2);
				if (s2.queue.size() != K) {
					s2.queue.add(req);
					if(s2.queue.size() == 1) {
						req.setStartServiceTime(timestamp);
						printStart(req,2);
						nextTime = timestamp + getTimeOfNextDeath(2);
						schedule.add(new Event(nextTime, EventType.SERVER2));
					}
				}
				else {
					printDrop(req);
					Simulator.dropped++;
				}
			}

			if (s0.queue.size() != 0) {
				Request nextReq = s0.queue.peek();
				nextReq.setStartServiceTime(timestamp);
				printStart(nextReq, 0);
				nextTime = timestamp + getTimeOfNextDeath(0);
				schedule.add(new Event(nextTime, EventType.DEATH));
			}
		break;
		
		case BIRTH:
	    	/**
		     * add the newly born request to the data structure of requests in the system.
		     */
			Request request = new Request(nextReqID());
			request.setArrivalTime(timestamp);
			request.setSystemArrTime(timestamp);
			printArrival(request);
			
			s0.queue.add(request);
			if(s0.queue.size() == 1) {
				request.setStartServiceTime(timestamp);
				schedule.add(new Event(timestamp + getTimeOfNextDeath(0), EventType.DEATH));
				printStart(request, 0);
			}

		    schedule.add(new Event(timestamp + getTimeOfNextBirth(), EventType.BIRTH));
	    
		break;
			
		case MONITOR:
		    /**
		     * inspect the data structures describing the simulated system and log them
		     */
			MASTER.numMonitorEvents++;
			s0.totalQueueLen += s0.queue.size();
			s1.totalQueueLen += s1.queue.size();
			s2.totalQueueLen += s2.queue.size();
			s3.totalQueueLen += s3.queue.size();

			if (s1.Proc1 != null) {
				s1.totalQueueLen++;
			}
			else if (s1.Proc2 != null) {
				s1.totalQueueLen++;
			}
	    
		    /**
		     * Schedule another monitor event following PASTA principle
		     */
		    schedule.add(new Event(timestamp + getTimeOfNextMonitor(), EventType.MONITOR));
	    
		break;

		case SERVER1P1:
			req = s1.Proc1;
			s1.numCompletedRequests++;
			req.setFinishServiceTime(timestamp);
			s1.totalRequestTime += req.getTq();
			s1.busyTimeP1 += req.getTs();
			printFinishS1(req, 1);

			req.setArrivalTime(timestamp);
			printToFrom(req, 1, 3);

			s3.queue.add(req);
			if(s3.queue.size() == 1) {
				req.setStartServiceTime(timestamp);
				printStart(req, 3);
				schedule.add(new Event(timestamp + getTimeOfNextDeath(3), EventType.SERVER3));
			}
			if (s1.queue.size() != 0) {
				s1.Proc1 = s1.queue.remove();
				req = s1.Proc1;
				req.setStartServiceTime(timestamp);
				printStartS1(req, 1);
				schedule.add(new Event(timestamp + getTimeOfNextDeath(1), EventType.SERVER1P1));
			}
			else {
				s1.Proc1 = null;
			}
		break;

		case SERVER1P2:
			req = s1.Proc2;
			req.setFinishServiceTime(timestamp);
			s1.numCompletedRequests++;
			s1.totalRequestTime += req.getTq();
			s1.busyTimeP2 += req.getTs();
			printFinishS1(req, 2);

			req.setArrivalTime(timestamp);
			printToFrom(req, 1, 3);

			s3.queue.add(req);
			if(s3.queue.size() == 1) {
				req.setStartServiceTime(timestamp);
				printStart(req, 3);
				schedule.add(new Event(timestamp + getTimeOfNextDeath(3), EventType.SERVER3));
			}
			if (s1.queue.size() != 0) {
				s1.Proc2 = s1.queue.remove();
				req = s1.Proc2;
				req.setStartServiceTime(timestamp);
				printStartS1(req, 2);
				schedule.add(new Event(timestamp + getTimeOfNextDeath(1), EventType.SERVER1P2));
			}
			else {
				s1.Proc2 = null;
			}
		break;

		case SERVER2:
			req = s2.queue.remove();
			s2.numCompletedRequests++;
			req.setFinishServiceTime(timestamp);
			s2.totalRequestTime += req.getTq();
			s2.busyTime += req.getTs();
			printFinish(req, 2);

			req.setArrivalTime(timestamp);
			printToFrom(req, 2, 3);

			s3.queue.add(req);
			if(s3.queue.size() == 1) {
				req.setStartServiceTime(timestamp);
				printStart(req, 3);
				schedule.add(new Event(timestamp + getTimeOfNextDeath(3), EventType.SERVER3));
			}
			if (s2.queue.size() != 0) {
				Request nextReq = s2.queue.peek();
				nextReq.setStartServiceTime(timestamp);
				printStart(nextReq, 2);
				nextTime = timestamp + getTimeOfNextDeath(2);
				schedule.add(new Event(nextTime, EventType.SERVER2));
			}
		break;

		case SERVER3:
			req = s3.queue.remove();
			s3.numCompletedRequests++;
			req.setFinishServiceTime(timestamp);
			s3.totalRequestTime += req.getTq();
			s3.busyTime += req.getTs();
			printFinish(req, 3);

			int S3Choice = pickRoute(3);
			if (S3Choice == -1) {
				MASTER.numCompletedRequests++;
				req.setFinishServiceTime(timestamp);
				MASTER.totalRequestTime += req.getFinishServiceTime() - req.getSystemArr();

				System.out.println("R" + Integer.toString(req.getId()) + " FROM S3 TO OUT: " + Double.toString(req.getFinishServiceTime()));
			}
			else if (S3Choice == 1) {
				req.setArrivalTime(timestamp);
				printToFrom(req, 3, 1);
				
				s1.queue.add(req);
				if (s1.Proc1 == null) {
					if (s1.Proc2 == null) {
						switchProcessor(s1);
						if (s1.currProcessor == 1) {
							s1.Proc2 = s1.queue.remove();
							req.setStartServiceTime(timestamp);
							printStartS1(req, 2);
							nextTime = timestamp + getTimeOfNextDeath(1);
							schedule.add(new Event(nextTime, EventType.SERVER1P2));
						}
						else {
							s1.Proc1 = s1.queue.remove();
							req.setStartServiceTime(timestamp);
							printStartS1(req,1);
							nextTime = timestamp + getTimeOfNextDeath(1);
							schedule.add(new Event(nextTime, EventType.SERVER1P1));
						}
					}
					else if (s1.queue.size() != 0) {
						s1.Proc1 = s1.queue.remove();
						req.setStartServiceTime(timestamp);
						printStartS1(req,1);
						nextTime = timestamp + getTimeOfNextDeath(1);
						schedule.add(new Event(nextTime, EventType.SERVER1P1));
					}
				}
				else if (s1.Proc1 != null && s1.Proc2 == null) {
					s1.Proc2 = s1.queue.remove();
					req.setStartServiceTime(timestamp);
					printStartS1(req, 2);printStartS1(req, 2);
					nextTime = timestamp + getTimeOfNextDeath(1);
					schedule.add(new Event(nextTime, EventType.SERVER1P2));
				}
			}
			else {		// To Server 2
				req.setArrivalTime(timestamp);
				printToFrom(req, 3, 2);
				if (s2.queue.size() != K) {
					s2.queue.add(req);
					if(s2.queue.size() == 1) {
						req.setStartServiceTime(timestamp);
						printStart(req,2);
						nextTime = timestamp + getTimeOfNextDeath(2);
						schedule.add(new Event(nextTime, EventType.SERVER2));
					}
				}
				else {
					printDrop(req);
					Simulator.dropped++;
				}
			}

			if (s3.queue.size() != 0) {
				Request nextReq = s3.queue.peek();
				nextReq.setStartServiceTime(timestamp);
				printStart(nextReq, 3);
				nextTime = timestamp + getTimeOfNextDeath(3);
				schedule.add(new Event(nextTime, EventType.SERVER3));
			}
		}
    }


    /* Make sure the events are sorted according to their happening time. */
    public int compareTo(Event e) {
    	double diff = time - e.getTime();
    	if (diff < 0) {
	    	return -1;
		} else if (diff > 0) {
		    return 1;
		} else {
		    return 0;
		}
    }

    /**
	 * exponential distribution
	 * used by {@link #getTimeOfNextBirth()}, {@link #getTimeOfNextDeath()} and {@link #getTimeOfNextMonitor()} 
	 * @param rate
	 * @return
	 */
	private static double getExp(double lam) {
        double y = Math.random();
        double x = (-1.0 * Math.log(1-y))/lam;
        return x;
    }
	
	/**
	 * 
	 * @return time for the next birth event
	 */
	public static double getTimeOfNextBirth() {
		return getExp(Simulator.lambda);
	}

	/**
	 * 
	 * @return time for the next death event
	 */
	public static double getTimeOfNextDeath(int server) {
		if (server == 0) {
			return getExp(1/Simulator.Ts0);
		}
		else if (server == 1) {
			return getExp(1/Simulator.Ts1);
		}
		else if (server == 2) {
			return getExp(1/Simulator.Ts2);
		}
		else {
			return Controller.pickTs();
		}
	}

	/**
	 * 
	 * @return time for the next monitor event
	 */
	public static double getTimeOfNextMonitor() {
		return getExp(Simulator.lambda);
	}
}
