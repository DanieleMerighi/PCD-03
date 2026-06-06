package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import pcd.smartHomeAlarmSystem.{Mode, Sensor}

import scala.concurrent.duration.{DurationInt, FiniteDuration}


object SmartHomeAlarmSystem:

  private val exitDelayDuration: FiniteDuration = 1.second
  private val entryDelayDuration: FiniteDuration = 1.second

  enum Command:
    case HandleSensorFiring(source: Sensor)
    case Arm(code: Int, mode: Mode = Mode.AllActive)
    case Disarm(code: Int)
    private[SmartHomeAlarmSystem] case HandleDelayEnd()

  import Command.*
  export Command.{HandleDelayEnd as _, *}

  def apply(sensors: Set[Sensor], pinCode: Int): Behavior[Command] =
    Behaviors.setup: context =>
      sensors.foreach(sensor =>
        context.spawn(SensorActor(sensor), sensor.name)
      )
      Behaviors.withTimers: timers =>
        disarmed(using pinCode, timers)

  private def disarmed(using pinCode: Int, timers: TimerScheduler[Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial:
      case Arm(code, mode) =>
        checkingPinCode(code, () =>
          timers.startSingleTimer((), HandleDelayEnd(), exitDelayDuration)
          exitDelay(mode)
        )

  private def exitDelay(mode: Mode)(using pinCode: Int, timers: TimerScheduler[Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial(
      disarmingOnPinCode
        .orElse:
          case HandleDelayEnd() => armed(mode)
    )

  private def armed(mode: Mode)(using pinCode: Int, timers: TimerScheduler[Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial(
      disarmingOnPinCode
        .orElse:
          case HandleSensorFiring(sensor) =>
            if mode.isActive(sensor) then
              timers.startSingleTimer((), HandleDelayEnd(), entryDelayDuration)
              entryDelay
            else
              Behaviors.same
    )

  private def entryDelay(using pinCode: Int, timers: TimerScheduler[Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial(
      disarmingOnPinCode
        .orElse:
          case HandleDelayEnd() => alarm
    )

  private def alarm(using pinCode: Int, timers: TimerScheduler[Command]): Behavior[Command] =
    Behaviors.receiveMessagePartial(
      disarmingOnPinCode
    )

  private def disarmingOnPinCode(using pinCode: Int, timers: TimerScheduler[Command]): PartialFunction[Command, Behavior[Command]] =
    case Disarm(code) => checkingPinCode(code, () => disarmed)

  private def checkingPinCode(code: Int, action: () => Behavior[Command])(using pinCode: Int): Behavior[Command] =
    if code == pinCode then
      action()
    else
      Behaviors.same

