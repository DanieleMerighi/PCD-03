package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import pcd.smartHomeAlarmSystem.Sensor

object SensorActor:

  enum Command:
    case Fire()

  def apply(sensor: Sensor)(using alarmSystem: ActorRef[SmartHomeAlarmSystem.Command]): Behavior[Command] =
    active(using sensor)

  private def active(using sensor: Sensor, alarmSystem: ActorRef[SmartHomeAlarmSystem.Command]): Behavior[Command] =
    Behaviors.receiveMessage: _ =>
      alarmSystem ! SmartHomeAlarmSystem.HandleSensorFiring(sensor)
      Behaviors.same

