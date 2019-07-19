package com.brevitaz.task2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.LinkedHashMap;

/**
 * Adapt task 1, so that we can measure the messaging time between each actor. However, the
 * messaging time should still only be printed out by the last actor (n). The output should be in
 * the following format:
 * actor 1, message received <time‐in‐milli‐seconds>
 * actor 2, message received <time‐in‐milli‐seconds>
 * …
 * TimeProfilingActor n, message received <time‐in‐milli‐seconds>
 * Hint: You are allowed to adapt/extend the messages being sent among the actors to achieve
 * this.
 */
public class Task2Main
{

    public static void main(String[] args) {
        if (args.length == 0)
        {
            System.out.println("Did you forget to pass number of nodes in program argument?");
            return;
        }

        try
        {
            int totalNumberOfNodes = Integer.parseInt(args[0]);

            ActorSystem system = ActorSystem.create("task2System");
            for (int i = 1; i <= totalNumberOfNodes; i++)
            {
                system.actorOf(TimeProfilingActor.props(i), Integer.toString(i));
            }

            system.actorSelection("user/1").tell(new TimeProfilingActor.ProfileMessage(new LinkedHashMap<>(), totalNumberOfNodes), ActorRef.noSender());
        }
        catch (NumberFormatException e)
        {
            System.out.println("Program argument is not a valid number. Please pass number of nodes in program argument.");
        }

    }
}
