package hw3;

import java.util.*;
import java.lang.Math;

public class SimulatorK {
    // Generate a random variable from an exponential distribution using lambda
    private static double getExp(double lam) {
        double y = Math.random();
        double x = (-1.0 * Math.log(1-y))/lam;
        return x;
    }

    private static double getRunTime(Request r) {
        if (r.finish != 0) {
            return (r.finish - r.start);
        }
        else {
            return 0;
        }
    }

    // Request object that holds the system life info of a request
    private static class Request {
        int ID;
        double arrival;
        double start;
        double finish;
        Request(int identity, double arr, double st, double fin) {
            this.ID = identity;
            this.arrival = arr;
            this.start = st;
            this.finish = fin;
        }
    }

    private static double updateNextArr(double t) {
        return t + getExp(lambda);
    }
    
    private static void printArrival(Request r) {
        System.out.println("R" + Integer.toString(r.ID) + " ARR: " + Double.toString(r.arrival));
    }

    private static void printStart(Request r) {
        System.out.println("R" + Integer.toString(r.ID) + " START: " + Double.toString(r.start));
    }

    private static void printFinish(Request r) {
        System.out.println("R" + Integer.toString(r.ID) + " DONE: " + Double.toString(r.finish));
    }

    private static void printDrop(Request r) {
        System.out.println("R" + Integer.toString(r.ID)+ " DROP: " + Double.toString(r.arrival));
    }
    

    static double lambda;   // Average arrival rate
    static double service_time; // Average service time
    static double k;
    static double dropped = 0;
    static double mu;
    static double total_q_size = 0;
    static double event_count = 0;
    static List<Request> request_Q = new ArrayList<Request>();  // Queue of requests

    // System state info
    static double busy_time = 0;
    private static void reportInfo(double simulation_time) {
        double Util = busy_time/simulation_time;
        double avgQLen = total_q_size/event_count;
        double avgRespT = avgQLen/lambda;
        System.out.println("UTIL: " + Double.toString(Util));
        System.out.println("QLEN: " + Double.toString(avgQLen));
        System.out.println("TRESP: " + Double.toString(avgRespT));
        System.out.println("DROPPED: " + Double.toString(dropped));
    }

    // Simulation of a system under the specified time frame
    public static void simulate(double simulation_time) {
        double time = 0;
        double next_arrival = getExp(lambda);
        int currentRequest = 0;
        double slots_left = k;

        // While there is still time
        while (time < simulation_time) {
            // There are tasks waiting in the request queue
            if (request_Q.size() != 0) {
                Request r = request_Q.get(0);
                // There is time for more requests to be handled before the simulation ends
                if ((next_arrival < simulation_time) || (r.finish < simulation_time)) {
                    // Request r will finish before a new one arrives.
                    if (next_arrival > r.finish) {
                        time = r.finish;
                        printFinish(r);
                        total_q_size += request_Q.size();
                        event_count++;
                        busy_time += getRunTime(r);
                        request_Q.remove(0);
                        slots_left++;
                        // Begin next request in the queue if there is still one
                        if (request_Q.size() != 0) {
                            Request next = request_Q.get(0);
                            next.start = time;
                            double do_Time = getExp(mu);
                            double finTime = time + do_Time;
                            next.finish = finTime;
                            request_Q.set(0, next);
                            printStart(next);
                            total_q_size += request_Q.size();
                            event_count++;
                        }
                    }
                    // A new request will arrive before the current one finishes
                    // Push the new request into the queue
                    else {
                        Request newRequest = new Request(currentRequest, next_arrival, 0, 0);
                        if (slots_left > 0) {  // The request queue has room to accept requests
                            request_Q.add(newRequest);
                            slots_left--;
                            printArrival(newRequest);
                            total_q_size += request_Q.size();
                            event_count++;
                        }
                        else {  // The request queue is currently full, drop the newly arrived request
                            printArrival(newRequest);
                            total_q_size += request_Q.size();
                            event_count++;
                            dropped++;
                            printDrop(newRequest);
                            total_q_size += request_Q.size();
                            event_count++;
                        }
                        currentRequest++;
                        time = next_arrival;
                        next_arrival = updateNextArr(next_arrival);
                    }
                }
                // Simulation ends while the queue had requests waiting
                else {
                    double unfinished = simulation_time - r.start;
                    busy_time += unfinished;
                    break;
                }
            }
            // No requests currently waiting
            else {
                // A request can still be sent to the system
                // Send request to the queue and start immediately
                if (next_arrival < simulation_time) {
                    double do_Time = getExp(mu);
                    double stTime = next_arrival;
                    double finTime = next_arrival + do_Time;
                    Request r = new Request(currentRequest, next_arrival, stTime, finTime);
                    request_Q.add(r);
                    slots_left--;
                    printArrival(r);
                    total_q_size += request_Q.size();
                    event_count++;
                    printStart(r);
                    total_q_size += request_Q.size();
                    event_count++;
                    currentRequest++;
                    time = next_arrival;
                    next_arrival = updateNextArr(time);
                }
                // Not enough time to get another request, end simulation
                else {
                    break;
                }
            }
        }
        reportInfo(simulation_time);
    }

    public static void main(String[] args) {
        double sim_time = Double.valueOf(args[0]);
        lambda = Double.valueOf(args[1]);
        service_time = Double.valueOf(args[2]);
        k = Double.valueOf(args[3]);
        mu = 1/service_time;

        simulate(sim_time);
    }
}
