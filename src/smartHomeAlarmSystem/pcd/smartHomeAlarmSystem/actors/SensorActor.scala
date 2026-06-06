package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import pcd.smartHomeAlarmSystem.Sensor

object SensorActor:

  enum Command:
    case ???

  def apply(sensor: Sensor): Behavior[Command] =
    active

  private def active: Behavior[Command] =
    Behaviors.receive: (context, message) =>
      Behaviors.same

