package hw3;

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
		if (server == 0) {
			System.out.println("R" + Integer.toString(r.getId()) + " START 0: " + Double.toString(r.getStartServiceTime()));
		}
		else {
			System.out.println("R" + Integer.toString(r.getId()) + " START 1: " + Double.toString(r.getStartServiceTime()));
		}
    }

    public static void printFinish(Request r, int server) {
		if (server == 0) {
			System.out.println("R" + Integer.toString(r.getId()) + " DONE 0: " + Double.toString(r.getFinishServiceTime()));
		}
		else {
			System.out.println("R" + Integer.toString(r.getId()) + " DONE 1: " + Double.toString(r.getFinishServiceTime()));
		}
    }

    public static void printRedir(Request r) {
        System.out.println("R" + Integer.toString(r.getId())+ " REDIR: " + Double.toString(r.getArrivalTime()));
    }
	/* ============================================================================================================ */
    
    public void function(PriorityQueue<Event> schedule, State stateS0, State stateS1, double timestamp, int k) {	
    	schedule.remove(this);
		switch (eventType) {
		case DEATH:
			
	    	/**
		     * remove the record of the request from the data structure of requests in the system
		     * Also, collect and compute some statistics.
	    	 */
		    Request req = stateS0.queue.remove();
		    req.setFinishServiceTime(timestamp);
	    
		    stateS0.totalRequestTime += req.getTq();
		    stateS0.busyTime += req.getTs();
		    stateS0.numCompletedRequests++;

		    printFinish(req, 0);
		    /**
		     * look for another blocked event in the queue that wants to execute and schedule it's death.
		     * at this time the waiting request enters processing time.
	    	 */
		    if (stateS0.queue.size() > 0){
				Request nextReq = stateS0.queue.peek();
				nextReq.setStartServiceTime(timestamp);

				printStart(nextReq, 0);

				/* Schedule the next death event. */
				schedule.add(new Event(timestamp + getTimeOfNextDeath(0), EventType.DEATH));
		    }
	    
	    	break;
	    
	    
		case BIRTH:
	    	/**
		     * add the newly born request to the data structure of requests in the system.
		     */
			Request request = new Request(stateS0.getNextId());
			request.setArrivalTime(timestamp);
			printArrival(request);
			
			if(stateS0.queue.size() != k) {
				stateS0.queue.add(request);
				if(stateS0.queue.size() == 1) {
					request.setStartServiceTime(timestamp);
					schedule.add(new Event(timestamp + getTimeOfNextDeath(0), EventType.DEATH));
	
					printStart(request, 0);
				}
			}

			/**
			 * stateS0.queue.size() == k
			 * the primary server's queue is full, redirect the request to secondary server.
			 */
			else {
				printRedir(request);
				Simulator.dropped++;
				stateS1.queue.add(request);
				
				/**
				 * the request reaches the secondary server and it is empty,
				 * begin serving the request immediately.
				 */
				if(stateS1.queue.size() == 1) {
					request.setStartServiceTime(timestamp);
					schedule.add(new Event(timestamp + getTimeOfNextDeath(0), EventType.REDIRECTED));
		
					printStart(request, 1);
				}
			}
		    /**
		     * if the queue is empty then start executing directly there is no waiting time.
	    	 */
			 
		    /**
		     * schedule the next arrival
		     */
		    schedule.add(new Event(timestamp + getTimeOfNextBirth(), EventType.BIRTH));
	    
		    break;
	    
			
		case MONITOR:
		    /**
		     * inspect the data structures describing the simulated system and log them
		     */
			stateS0.numMonitorEvents+=1;
			stateS0.totalQueueLen += stateS0.queue.size();
			stateS1.numMonitorEvents+=1;
			stateS1.totalQueueLen += stateS1.queue.size();
	    
		    /**
		     * Schedule another monitor event following PASTA principle
		     */
		    schedule.add(new Event(timestamp + getTimeOfNextMonitor(), EventType.MONITOR));
	    
		    break;
		    
		case REDIRECTED:
			
			Request S1req = stateS1.queue.remove();
		    S1req.setFinishServiceTime(timestamp);
	    
		    stateS1.totalRequestTime += S1req.getTq();
		    stateS1.busyTime += S1req.getTs();

		    stateS1.numCompletedRequests++;

		    printFinish(S1req, 1);
			
			/**
			 * The secondary server has more requests than the one just finished
			 */
		    if(stateS1.queue.size() > 0) {
				Request nextReq = stateS1.queue.peek();
				nextReq.setStartServiceTime(timestamp);

				printStart(nextReq, 1);

				schedule.add(new Event(timestamp + getTimeOfNextDeath(1), EventType.REDIRECTED));
		    }
		    
		    break;
			
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
			return getExp(Simulator.S0_mu);
		}
		else {
			return getExp(Simulator.S1_mu);
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
