package frc.robot.Mechanisms;

import java.security.PublicKey;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.Systems.BatteryMap;
import frc.robot.Systems.Vision;


public class Shooter {
    
    boolean inRange;
    Boolean AUTODUMPING = false;
    WPI_TalonFX m_MasterMotor;
    WPI_TalonFX m_FollowerMotor;
    WPI_TalonSRX m_feeder;

    Double lastSetpoint;

    /*double kP = 0.18;
    double kI = 0.001;
    double kD = 8;
    double kF = .025;*/

    //double kP = 0.91;
    //double kI = 0.000004;
    //double kD = 1.60;
//    double kF = 0.05;



    //double kP = 0.8;
    double kP = 0.8;

    //double kI = 0.000002;
    double kI = 0.000003;

    double kD = 0.004;
    double kF = 0.05;
    public static double rpmSetpoint;
    public static boolean hoodUp = false;
    Double[][] HoodUpTargeting;

    public Shooter() {
        m_MasterMotor = new WPI_TalonFX(7);
        m_FollowerMotor = new WPI_TalonFX(8);

        m_MasterMotor.configFactoryDefault();
        m_FollowerMotor.configFactoryDefault();
        
        HoodUpTargeting = new Double[8][2];
        
        
        m_MasterMotor.configFactoryDefault();   
        m_MasterMotor.config_kP(0, kP);
        m_MasterMotor.config_kI(0, kI);
        m_MasterMotor.config_kD(0, kD);
        m_MasterMotor.config_kF(0, kF);
        m_MasterMotor.configPeakOutputForward(1);
        m_MasterMotor.configPeakOutputReverse(-1);



        m_FollowerMotor.follow(m_MasterMotor);
        m_MasterMotor.setInverted(true);

     

        m_feeder = new WPI_TalonSRX(12);
        m_feeder.configFactoryDefault();
        m_feeder.setInverted(true);



    }



    public void hood(DoubleSolenoid piston) {
        if (hoodUp) {
            piston.set(Value.kForward);
            
        } else if (!hoodUp) {            
            piston.set(Value.kReverse);
            
        }
    }



    public void feed(Boolean On) {
        if (On == true) {
            m_feeder.set(ControlMode.PercentOutput, .7); //placeholder speed, maybe make staight 1 if I can it
            Indexer.shooting(true);
        } 
        else {m_feeder.set(ControlMode.PercentOutput, 0);
            Indexer.shooting(false);
        }
    }



    public void postAmp(){
        BatteryMap.shooterMotors(m_MasterMotor, m_FollowerMotor);
    }



    public void flywheelRev(int mode, String BALLCOLOR, String ALLIANCE, Boolean DISABLECOMMAND) { // Modes: 0 = straight against hub, 1 = pin shot, 2 = limelight assisted
        SmartDashboard.putNumber("RightShooter", m_FollowerMotor.getSelectedSensorVelocity());
        SmartDashboard.putNumber("LeftShooter", m_MasterMotor.getSelectedSensorVelocity());
        SmartDashboard.putBoolean("AUTODUMP", AUTODUMPING);
        double distance = Vision.DistanceFromTarget();
        
        if(distance != 0) inRange = true;
        else inRange = false;


        /*if (Vision.DistanceFromTarget() > 70 && mode != -1) {
            hoodUp = true;
          } 
          else if(Vision.DistanceFromTarget() < 70 && mode != -1){
              hoodUp = false;
          }*/

        
       /* if(hoodUp){
            m_MasterMotor.config_kP(0, 0.91);
            m_MasterMotor.config_kI(0, 0.000004);
            m_MasterMotor.config_kD(0, 1.60);
            m_MasterMotor.config_kF(0, 0.05);
        }   
        else if(!hoodUp){
            m_MasterMotor.config_kP(0, 0.91);
            m_MasterMotor.config_kI(0, 0.000004);
            m_MasterMotor.config_kD(0, 1.60);
            m_MasterMotor.config_kF(0, 0.05);
        }    */ 

        if(DISABLECOMMAND){
            AUTODUMPING = !AUTODUMPING;
        } 

        if(mode >=0 & BALLCOLOR != ALLIANCE & AUTODUMPING){
            mode = 270;
            
        } 

        /*if (hoodUp) {
            switch (mode) {
                case 0:
                    //m_MasterMotor.set(TalonFXControlMode.Velocity, 5250);
                    //rpmSetpoint = 5400;
                    //rpmSetpoint = rpmTargetUp(distance);
                    rpmSetpoint = 6550;
                    break;

                case 90:
                    //m_MasterMotor.set(TalonFXControlMode.Velocity, 5950);
                    //rpmSetpoint = 7100;
                    rpmSetpoint = rpmTargetUp(distance);

                    break;
                case 180:
                    //m_MasterMotor.set(TalonFXControlMode.Velocity, 7000);
                    //rpmSetpoint = 7600;
                    rpmSetpoint = rpmTargetUp(distance);

                    break;
                case 270:
                    //m_MasterMotor.set(ControlMode.PercentOutput, 0.15);
                    rpmSetpoint = rpmTargetUp(distance);
                    break;
    
                case -1:
                    //m_MasterMotor.set(ControlMode.PercentOutput, 0);
                    rpmSetpoint = 0;

                    break;

                default:
                    //m_MasterMotor.set(ControlMode.PercentOutput, 0);
                    rpmSetpoint = 0;

                    break;
              }

        } else {*/

            switch (mode) {
                case 0:
                    rpmSetpoint = 5300;
                    //rpmSetpoint = 6550;
                    hoodUp = false;

                    break;

                case 90:
                    rpmSetpoint = 5950;
                    hoodUp = false;

                    break;
                
                case 180:
                    rpmSetpoint = 7000;
                    hoodUp = false;

                    break;
                case 270:
                    rpmSetpoint = rpmTargetUp(distance);
                    hoodUp = true;
                    break;
    
                case -1:
                    rpmSetpoint = 0;
                    break;

                default:
                    rpmSetpoint = 0;
                    break;
              }
        //}
            lastSetpoint = rpmSetpoint;
        if (rpmSetpoint != 0) {
            m_MasterMotor.set(TalonFXControlMode.Velocity, rpmSetpoint);
        } else {
            m_MasterMotor.set(TalonFXControlMode.PercentOutput, 0);
        }
    }



    public double calculatedRPM(double distance) {
        double rpm;
        



        return 0;
    }


    public double rpmTargetUp(double distance) {
        //create some sort of table of Distance VS Speed
        double rpm;
        if (/*distance < 193 && */distance >= 186) {
            rpm = 7610;
            //rpm = (500/7)*(distance-193) + 7690;

        } else if (distance < 186 && distance >= 157) {
            rpm = 7110; //
            //rpm = (550/29)*(distance-186) + 7180;

        } else if (distance < 157 && distance >= 147) {
            rpm = 6605;
            //rpm = (450/10)*(distance-157) + 6620;
        } else if (distance < 147 && distance >= 128) {
            rpm = 6155;
            //rpm = (200/19)*(distance-147) + 6150;

        } else if (distance < 128 && distance >= 112) {
            rpm = 5920;
            //rpm = (300/16)*(distance-128) + 5950;

        } else if (distance < 112 && distance >= 97) {
            rpm = 5625;
            //rpm = (200/15)*(distance-112) + 5650;

        } else if (distance < 97 && distance >= 79) {
            rpm = 5400;
        } //else {rpm = 0;}

        else /*if (distance < 97 && distance >= 79)*/ {
            rpm = lastSetpoint;
        } //else {rp

        SmartDashboard.putNumber("TARGETINGRPM", rpm);



        return rpm;
    }

    public double rpmTarget() {
        return rpmSetpoint;
    }

    public double currentRpm() {

        return m_MasterMotor.getSelectedSensorVelocity();

    }



    



}
