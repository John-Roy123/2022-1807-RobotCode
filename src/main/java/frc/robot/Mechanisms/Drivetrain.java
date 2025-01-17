package frc.robot.Mechanisms;

import java.lang.reflect.Field;
import java.security.PublicKey;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Systems.BatteryMap;
import frc.robot.Systems.Vision;

public class Drivetrain {
    
    private final WPI_TalonFX m_leftMotorOne;
    private final WPI_TalonFX m_leftMotorTwo;
    private final WPI_TalonFX m_leftMotorThree;

    private final WPI_TalonFX m_RightMotorFour;
    private final WPI_TalonFX m_RightMotorFive;
    private final WPI_TalonFX m_RightMotorSix;

    private final MotorControllerGroup left;
    private final MotorControllerGroup right;

    private final DifferentialDrive drive;
    
    public static double steering, limeSteering, piSteering;
    public static boolean manualSteering;

    public static Pose2d position;
    DifferentialDriveOdometry odometry;
    AHRS m_navX;



    boolean coasting;

    double limeSteerCoefficient = .1;
    double piSteerCoefficient = .1;
    
    boolean limeLightTargeting, piTargeting = false;

    PIDController tarPid;
    PIDController drivePid;


    double visible;
    
    Field2d field;
    
    public Drivetrain() {
      m_navX = new AHRS(Port.kOnboard);


      odometry = new DifferentialDriveOdometry(m_navX.getRotation2d());

      tarPid = new PIDController(.038, 0.002, 0.002);
      drivePid = new PIDController(.038, 0.002, 0.002); //estimates for driving pid

      m_leftMotorOne = new WPI_TalonFX(1);
      m_leftMotorTwo = new WPI_TalonFX(2);
      m_leftMotorThree = new WPI_TalonFX(3);
  
      m_RightMotorFour = new WPI_TalonFX(4);
      m_RightMotorFive = new WPI_TalonFX(5);
      m_RightMotorSix = new WPI_TalonFX(6);


      m_leftMotorOne.configOpenloopRamp(0.5);
      m_leftMotorTwo.configOpenloopRamp(0.5);
      m_leftMotorThree.configOpenloopRamp(0.5);
      m_RightMotorFour.configOpenloopRamp(0.5);
      m_RightMotorFive.configOpenloopRamp(0.5);
      m_RightMotorSix.configOpenloopRamp(0.5);
      
      

      m_leftMotorOne.setNeutralMode(NeutralMode.Brake);
      m_leftMotorTwo.setNeutralMode(NeutralMode.Brake);
      m_leftMotorThree.setNeutralMode(NeutralMode.Brake);
      
      m_RightMotorFour.setNeutralMode(NeutralMode.Brake);
      m_RightMotorFive.setNeutralMode(NeutralMode.Brake);
      m_RightMotorSix.setNeutralMode(NeutralMode.Brake);


      right = new MotorControllerGroup(m_RightMotorFour, m_RightMotorFive, m_RightMotorSix);
      left = new MotorControllerGroup(m_leftMotorOne, m_leftMotorTwo,m_leftMotorThree);

      left.setInverted(true);

      drive = new DifferentialDrive(right, left);

      field = new Field2d();

      SmartDashboard.putData("Field", field);

      //manualSteering = true;
     


      }




    

    @SuppressWarnings("ParameterName")
    public void drive(double speed, double rotation) {

      position = odometry.update(m_navX.getRotation2d(), leftSideWheels(), rightSideWheels());
      field.setRobotPose(odometry.getPoseMeters());

      SmartDashboard.putNumber("Gyro", m_navX.getAngle());

      if(!manualSteering){
        if(limeLightTargeting && !piTargeting) drive.arcadeDrive(speed, limeSteering);
        else if(piTargeting && !limeLightTargeting) drive.arcadeDrive(speed, piSteering);

      }
      else{
      steering = rotation * 0.75;
      
      //deceleration(speed);

      drive.arcadeDrive(speed, steering);
      }
      BatteryMap.driveTrainMotors(m_RightMotorFive, m_RightMotorSix, m_leftMotorOne, m_leftMotorTwo);
    }


    public void driveTwo(double speed, double rotation, boolean lime) {
      steering = rotation * 0.75;

      if(lime) drive.arcadeDrive(speed, limeSteering);
      else drive.arcadeDrive(speed, steering);



    }
  


    public void autoRamp(boolean yes){
      m_leftMotorOne.configOpenloopRamp(0);
      m_leftMotorTwo.configOpenloopRamp(0);
      m_leftMotorThree.configOpenloopRamp(0);
      m_RightMotorFour.configOpenloopRamp(0);
      m_RightMotorFive.configOpenloopRamp(0);
      m_RightMotorSix.configOpenloopRamp(0);
    }
    public void RAMP(Boolean on){
      if(on){
      m_leftMotorOne.configOpenloopRamp(0.);
      m_leftMotorTwo.configOpenloopRamp(0.);
      m_leftMotorThree.configOpenloopRamp(0.);
      m_RightMotorFour.configOpenloopRamp(0.);
      m_RightMotorFive.configOpenloopRamp(0.);
      m_RightMotorSix.configOpenloopRamp(0.);
      }
      else{
      m_leftMotorOne.configOpenloopRamp(0.6);
      m_leftMotorTwo.configOpenloopRamp(0.6);
      m_leftMotorThree.configOpenloopRamp(0.6);
      m_RightMotorFour.configOpenloopRamp(0.6);
      m_RightMotorFive.configOpenloopRamp(0.6);
      m_RightMotorSix.configOpenloopRamp(0.6);
      }
    }


    public void CoordinateDrive(double X, double Y) {
      double CurrentX = position.getX();
      double CurrentY = position.getY();

      double dis = Math.sqrt(Math.pow((CurrentX - X), 2) + Math.pow((CurrentY - X), 2));

      double Xdif = CurrentX - X;
      double Ydif = CurrentY - Y;

      double theta = Math.atan(Ydif/Xdif);
      double CurrentTheta = position.getRotation().getDegrees();

      drive.arcadeDrive(drivePid.calculate(dis, 0), tarPid.calculate(CurrentTheta, theta));

    }





    public void targetLime(boolean On) {
      visible = Vision.targetVisible();
      if(visible == 1 && On){
        manualSteering = false;
      }else{
        manualSteering = true;
      }
      limeLightTargeting = On;
      if (On) {
        limeSteering = Vision.AngleFromTarget();
        limeSteering = -tarPid.calculate(Vision.AngleFromTarget(), 0);
      }
    }

    public void TargetPi(boolean On) {
      manualSteering = !On;
      piTargeting = On;
      if (On) {
        piSteering = Vision.AngleFromBall() * piSteerCoefficient;
      }
    }

    //public double Speed() {

      //double gearRatio = 1; //placeholder
      //double averageMotorSpeed = ((m_leftMotorOne.getSelectedSensorVelocity() + m_leftMotorTwo.getSelectedSensorVelocity() +) + (m_RightMotorFour.getSelectedSensorVelocity() + m_RightMotorFive.getSelectedSensorVelocity() + m_RightMotorSix.getSelectedSensorVelocity()/6));
      
      //double wheelSpeed = averageMotorSpeed/gearRatio; //placeholder


//      return wheelSpeed;  
  //  }

      public double leftSideWheels() {
        double encoder = (m_leftMotorOne.getSelectedSensorPosition() + m_leftMotorTwo.getSelectedSensorPosition() + m_leftMotorThree.getSelectedSensorPosition())/3;
        double rotations = encoder / 2048;
        double rotationsOfGearbox = rotations / 15.33;
        double distanceTravelinches = rotationsOfGearbox * (6 * Math.PI);
        return distanceTravelinches;
      }

      public double rightSideWheels() {
        double encoder = (m_RightMotorFour.getSelectedSensorPosition() + m_RightMotorFive.getSelectedSensorPosition() + m_RightMotorSix.getSelectedSensorPosition())/3;
        double rotations = encoder / 2048;
        double rotationsOfGearbox = rotations / 15.33;
        double distanceTravelinches = rotationsOfGearbox * (6 * Math.PI);
        return distanceTravelinches;
      }


}
