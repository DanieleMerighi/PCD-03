package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import pcd.smartHomeAlarmSystem.Sensor
import pcd.smartHomeAlarmSystem.actors.SensorActor.Command.Connect

object SensorActor:

  enum Command:
    case Connect(alarmSystem: ActorRef[SmartHomeAlarmSystem.Command])
    case Fire()

  export Command.*

  def apply(sensor: Sensor): Behavior[Command] =
    unconnected(using sensor)

  private def unconnected(using sensor: Sensor): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Connect(alarmSystem) =>
        connected(using sensor, alarmSystem)

  private def connected(using sensor: Sensor, alarmSystem: ActorRef[SmartHomeAlarmSystem.Command]): Behavior[Command] =
    Behaviors.receivePartial:
      case (context, Fire()) =>
        context.log.info(s"Sensor \"${sensor.name}\" firing.")
        alarmSystem ! SmartHomeAlarmSystem.HandleSensorFiring(sensor)
        Behaviors.same

