package com.brevitaz.task1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * Build an application with n‐actors (n should be specifiable as input to the application).
 * Each actor should be assigned an id between 1 to n (unique). In the main program a message
 * should be sent to the actor with id 1. The message object should contain a field
 * numberOfHopsTravelled (int, initialized to 0).
 *
 * Actor 1 forwards the message to the actor with id 2 when receiving the message from actor
 * 1, but before doing so, increases the numberOfHopsTravelled by one in the message. This
 * continues in this way until the message reaches actor n. When actor n receives the message,
 * it prints out the numberOfHopsTravelled field in the received message.
 */
public class Task1Main
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

            ActorSystem system = ActorSystem.create("task1System");
            for (int i = 1; i <= totalNumberOfNodes; i++)
            {
                system.actorOf(HopCountingActor.props(i), Integer.toString(i) );
            }

            system.actorSelection("user/1").tell(new HopCountingActor.CountHopsMessage(0, totalNumberOfNodes), ActorRef.noSender());
        }
        catch (NumberFormatException e)
        {
            System.out.println("Program argument is not a valid number. Please pass number of nodes in program argument.");
        }

    }
}
