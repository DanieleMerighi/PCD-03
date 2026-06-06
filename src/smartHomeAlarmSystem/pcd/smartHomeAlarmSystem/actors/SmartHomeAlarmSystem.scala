package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import pcd.smartHomeAlarmSystem.Sensor

object SmartHomeAlarmSystem:

  enum Command:
    case ???

  def apply(sensors: Set[Sensor]): Behavior[Command] =
    Behaviors.setup: context =>
      sensors.foreach(sensor =>
        context.spawn(SensorActor(sensor), sensor.name)
      )
      disarmed

  private def disarmed: Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case _ => Behaviors.same

