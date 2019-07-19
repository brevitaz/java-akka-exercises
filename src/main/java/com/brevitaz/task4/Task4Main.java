package com.brevitaz.task4;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.ArrayList;
import java.util.List;


/**
 *  Task 4
 *
 *  1.) Create a system with n agents (consisting of actors with unique ids from 1 to n).
 *  Furthermore create one additional actor which we refer to as a “scheduler”.
 *  Messages are passed between the agents and the scheduler, containing a time (between 0 and +inf).
 *  There are two types of messages: 1.) TriggerMessage 2.) Acknowledgement.
 *  Each agent receiving a TriggerMessage message from the scheduler and sends a new TriggerMessage message to the scheduler
 *  (with time of received trigger + random number between 0 and100. It also sends an
 *  Acknowledgement message to the scheduler after that.
 *  The agent repeats such behavior for max 10 times (meaning during the whole program it receives 10
 *  TriggerMessage messages from scheduler, sends 9 TriggerMessage messages back to the Scheduler and sends 10
 *  Acknowledgement messages to back 10 scheduler; there is one less TriggerMessage message sent back,
 *  in order to eventually stop the “simulation”).
 *
 *  2.) The scheduler upon receiving a TriggerMessage messages stores the message and actor reference it in
 *  a sorted TriggerMessage queue (sorted by time). Whenever the Scheduler receives an Acknowledgement message
 *  and there are still more Triggers in the sorted queue, it sends out the first TriggerMessage message to the
 *  corresponding agent actor.
 *
 *  3.) Each agent should write down in the console log, its id and TriggerMessage message time whenever it receives a
 *  new TriggerMessage message from the scheduler.
 *
 *  4.) The scheduler is initialized with one TriggerMessage message per agent (with random time between 0 and 100).
 *  After initialization a Start message is sent to the scheduler, which initiates the “simulation”:
 *  The scheduler removes the first message in the sorted TriggerMessage queue and sends the first TriggerMessage to
 *  the corresponding agent.
 *
 */

public class Task4Main {

    public static void main(String[] args) {

        if (args.length == 0)
        {
            System.out.println("Did you forget to pass number of nodes in program argument?");
            return;
        }

        try
        {
            final int totalNumberOfAgents = Integer.parseInt(args[0]);
            final List<ActorRef> allAgents = new ArrayList<>();

            final ActorSystem system = ActorSystem.create("task4System");
            for(int i = 1; i <= totalNumberOfAgents; i++)
            {
                final ActorRef ref = system.actorOf(AgentActor.props(i));
                allAgents.add(ref);
            }
            final ActorRef schedulerActor = system.actorOf(SchedulerActor.props(allAgents), "scheduler");
            schedulerActor.tell("start", ActorRef.noSender());

        }
        catch (NumberFormatException e)
        {
            System.out.println("Program argument is not a valid number. Please pass number of nodes in program argument.");
        }

    }
}
