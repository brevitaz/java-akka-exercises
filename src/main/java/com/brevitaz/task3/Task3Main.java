package com.brevitaz.task3;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.Random;

/**
 * 1.) Create an n‐actor system (unique ids from 1 to n), where each actor i can only send and
 * receive messages to/from actor i‐1 and i+1 (actor n can communicate with actor n‐1 and
 * actor 0).
 * 2.) In the main program, we send a String message (“hello from: x ”) to one random actor x. That
 * random actor sends the message to its two neighbors. Each neighbor receiving the message
 * just forwards it to that neighbor which did not send the message to it, after introducing a
 * random delay between 1 and 100ms.
 * 3.) When an actor has received a message from both of its neighbors, it prints out its own Id and
 * the message it received.
 */
public class Task3Main
{

    public static void main(final String[] args)
    {

        final Random ran = new Random();
        if (args.length == 0)
        {
            System.out.println("Did you forget to pass number of nodes in program argument?");
            return;
        }

        try
        {
            final int totalNumberOfNodes = Integer.parseInt(args[0]);
            final int randomNumber = ran.nextInt(totalNumberOfNodes) + 1;

            final ActorSystem system = ActorSystem.create("task3System");
            final Topology topology = new CircularTopology(totalNumberOfNodes, system);

            for (int i = 1; i <= totalNumberOfNodes; i++)
            {
                system.actorOf(GossipingActor.props(i, topology), Integer.toString(i));
            }
            system.actorSelection("user/" + randomNumber).tell("hello from: " + randomNumber, ActorRef.noSender());

        }
        catch (final NumberFormatException e)
        {
            System.out.println("Program argument is not a valid number. Please pass number of nodes in program argument.");
        }
    }
}
