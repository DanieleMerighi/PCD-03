package pcd.smartHomeAlarmSystem


trait Mode:
  
  def isActive(sensor: Sensor): Boolean


object Mode:
  
  def fromSet(activeSensors: Set[Sensor], name: String): Mode =
    new Mode:
      override def isActive(sensor: Sensor): Boolean = activeSensors.contains(sensor)
      override def toString: String = name

  val AllActive: Mode =
    new Mode:
      override def isActive(sensor: Sensor): Boolean = true
      override def toString: String = "AllActive"

