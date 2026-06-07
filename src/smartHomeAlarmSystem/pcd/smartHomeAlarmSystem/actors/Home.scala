package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}
import pcd.smartHomeAlarmSystem.{Mode, Sensor, associateWith, foreachValue}
import pcd.smartHomeAlarmSystem.actors.Home.Command.{InstallAlarmSystem, InteractWithSensor}


object Home:

  enum Command:
    case InstallAlarmSystem(pinCode: Int)
    case ArmAlarmSystem(code: Int, mode: Mode = Mode.AllActive)
    case DisarmAlarmSystem(code: Int)
    case InteractWithSensor(sensor: Sensor)

  export Command.*

  def apply(sensors: Set[Sensor]): Behavior[Command] =
    Behaviors.setup: context =>
      val sensorActors = sensors.associateWith(sensor =>
        context.spawn(SensorActor(sensor), sensor.name)
      )
      unsecured(using sensorActors, context)

  private def unsecured(using sensors: Map[Sensor, ActorRef[SensorActor.Command]],
                        context: ActorContext[Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case InstallAlarmSystem(pinCode) =>
        context.log.info("Installing the Alarm System.")
        val alarmSystem = context.spawn(SmartHomeAlarmSystem(pinCode), "SmartHomeAlarmSystem")
        sensors.foreachValue(_ ! SensorActor.Connect(alarmSystem))
        secured(using sensors, alarmSystem)

  private def secured(using sensors: Map[Sensor, ActorRef[SensorActor.Command]],
                      alarmSystem: ActorRef[SmartHomeAlarmSystem.Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case ArmAlarmSystem(code, mode) =>
        alarmSystem ! SmartHomeAlarmSystem.Arm(code, mode)
        Behaviors.same
      case DisarmAlarmSystem(code) =>
        alarmSystem ! SmartHomeAlarmSystem.Disarm(code)
        Behaviors.same
      case InteractWithSensor(sensor) =>
        sensors(sensor) ! SensorActor.Fire()
        Behaviors.same

