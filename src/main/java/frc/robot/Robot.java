// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.print.DocFlavor.STRING;
import javax.swing.plaf.TreeUI;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.net.PortForwarder;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.PneumaticsBase;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Mechanisms.Climb;
import frc.robot.Mechanisms.Collector;
import frc.robot.Mechanisms.Compress;
import frc.robot.Mechanisms.Drivetrain;
import frc.robot.Mechanisms.Indexer;
import frc.robot.Mechanisms.Shooter;
import frc.robot.Systems.Auto;
import frc.robot.Systems.BatteryMap;
import frc.robot.Systems.Vision;


public class Robot extends TimedRobot {


  private static final String ThreeBallAuto = "Default";
  private static final String FourBallAuto = "My Auto";
  private static final String TwoBallAuto = "TwoAuto";

  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  String teamColor;

  //SendableChooser<String> = new SendableChooser();

  private final static DoubleSolenoid m_doubleSolenoid = new DoubleSolenoid(14, PneumaticsModuleType.REVPH, 0, 1);
  private final DoubleSolenoid m_climbSolenoid = new DoubleSolenoid(14, PneumaticsModuleType.REVPH, 5, 4);
  private final static DoubleSolenoid m_hoodSolenoid = new DoubleSolenoid(14, PneumaticsModuleType.REVPH, 2, 3);

  public boolean hoodUp;
  public BatteryMap BattMap = new BatteryMap();
  public static Drivetrain m_Drive = new Drivetrain();
  public static Collector m_Collector = new Collector();
  public static Indexer m_Indexer = new Indexer();
  public Compress m_Compress = new Compress();
  public static Shooter m_Shooter = new Shooter();
  public Climb m_Climb = new Climb();
  public Vision m_Vision = new Vision();

  Compressor m_Compy;

  private XboxController m_DriveController;
  private XboxController m_OperatController;

  int ShootMode;
  Boolean Shoot = false;
  Boolean TAKE;
  static int autoSection;
  static String BALLCOLOR;

  static String ALLIANCE;
  static Double ShooterSpeed;


  //Ungly Ass Auto
  static int x; 
  public static File XControl;
  public static File YControl;
  static FileWriter XRecord;
  static FileWriter YRecord;
  static double xJoy;
  static double yJoy;
  static String xString;
  static String yString;
  static BufferedReader XlinReader;
  static BufferedReader YlinReader;
  

  public static File XtwoBall;
  public static File YtwoBall;
  static BufferedReader YtwoBallR;
  static BufferedReader XtwoBallR;


  public static File XsenBase1;
  public static File YsenBase1;
  public static File XsenThree2;
  public static File YsenThree2;

  static File XsenFourBase2;
  static File YsenFourBase2;
  static File XsenFour2;
  static File YsenFour2;

  static BufferedReader XsenBase1R;
  static BufferedReader YsenBase1R;
  static BufferedReader XsenThree2R;
  static BufferedReader YsenThree2R;

  static BufferedReader XsenFourBase2R;
  static BufferedReader YsenFourBase2R;
  static BufferedReader XsenFour2R;
  static BufferedReader YsenFour2R;
  double rpmCurrent;
  double rpmTarget;

  File[] files = Filesystem.getDeployDirectory().listFiles();


  

  @Override
  public void robotInit() {
    //PortForwarder.add(5800, "10.18.7.14", 5800);
    //PortForwarder.add(5801, "10.18.7.14", 5801);
    //PortForwarder.add(5802, "10.18.7.14", 5802);


    m_chooser.addOption("2 Ball Auto", TwoBallAuto);
    m_chooser.setDefaultOption("3 Ball Auto", ThreeBallAuto);
    m_chooser.addOption("4 Ball Auto", FourBallAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("ledMode").setNumber(3);;
    //teamColor = DriverStation.getAlliance().name();
    //SmartDashboard.putString("Team", teamColor);



  
    SmartDashboard.putNumber("Hl", 0);
    SmartDashboard.putNumber("Sl", 3);
    SmartDashboard.putNumber("Vl", 115);

    SmartDashboard.putNumber("Hh", 10);
    SmartDashboard.putNumber("Sh", 255);
    SmartDashboard.putNumber("Vh", 255);

    SmartDashboard.putString("Alliance", DriverStation.getAlliance().name());

    m_DriveController = new XboxController(0);
    m_OperatController = new XboxController(1);
    
    
    m_climbSolenoid.set(Value.kReverse);
    m_doubleSolenoid.set(Value.kReverse);
    m_hoodSolenoid.set(Value.kReverse);

    m_Compy = new Compressor(14, PneumaticsModuleType.REVPH);  
  ;
    x = 0;


  }


  @Override
  public void robotPeriodic() {
    ShooterSpeed = SmartDashboard.getNumber("LeftShooter", 0);

  }

  @Override
  public void autonomousInit() {
    XsenBase1 = new File("/home/lvuser/XsenFour1.txt");
    YsenBase1 = new File("/home/lvuser/YsenFour1.txt");
    XsenThree2 = new File("/home/lvuser/XsenFour2.txt");
    YsenThree2 = new File("/home/lvuser/YsenFour2.txt");

    XsenFourBase2 = new File("/home/lvuser/XsenFourBall1.txt");
    YsenFourBase2 = new File("/home/lvuser/YsenFourBall1.txt");
    XsenFour2 = new File("/home/lvuser/XsenFourBall2.txt");
    YsenFour2 = new File("/home/lvuser/YsenFourBall2.txt");

    YtwoBall = new File("/home/lvuser/Ysen2-1.txt");
    XtwoBall = new File("/home/lvuser/Xsen2-1.txt");



    autoSection = 0;
    x = 0;
    //XControl = new File("/home/lvuser/delpoy/Xpos.txt");
    //YControl = new File("/home/lvuser/deploy/Ypos.txt");
    try {
      YsenBase1R = new BufferedReader(new FileReader(YsenBase1));
      XsenBase1R = new BufferedReader(new FileReader(XsenBase1));
      YsenThree2R = new BufferedReader(new FileReader(YsenThree2));
      XsenThree2R = new BufferedReader(new FileReader(XsenThree2));

      YsenFourBase2R = new BufferedReader(new FileReader(YsenFourBase2));
      XsenFourBase2R = new BufferedReader(new FileReader(XsenFourBase2));
      YsenFour2R = new BufferedReader(new FileReader(YsenFour2));
      XsenFour2R = new BufferedReader(new FileReader(XsenFour2));

      YtwoBallR = new BufferedReader(new FileReader(YtwoBall));
      XtwoBallR = new BufferedReader(new FileReader(XtwoBall));

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);



    switch (m_autoSelected) {
      case FourBallAuto:
      FourBall();
      break;
      case ThreeBallAuto:
      ThreeBall();
      break;

      case TwoBallAuto:
      twoBall();
      break;
    }

    
    
    
    
/*
    SmartDashboard.putString("FILE1", files[1].getName());
    SmartDashboard.putString("FILE2", files[2].getName());


    rpmTarget = m_Shooter.rpmTarget();
    rpmCurrent = m_Shooter.currentRpm();
    m_Indexer.index();
    m_Shooter.hood(m_hoodSolenoid);
    m_Indexer.COLLECT(true);
   
    
      switch (autoSection) {
        case 0:
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
            m_Collector.dropped(true, m_doubleSolenoid);

            //m_Indexer.COLLECT(true);
            BALLCOLOR = m_Indexer.ColorSensor();
            m_Collector.COLLECT(true, false);
            try{

              yString = YsenFour1R.readLine();
              xString = XsenFour1R.readLine();
        
              SmartDashboard.putString("yString", yString);
              yJoy = Double.valueOf(yString);
              SmartDashboard.putNumber("yJoy", yJoy);
              xJoy = Double.valueOf(xString);
              SmartDashboard.putNumber("xJoy", xJoy);
              double yDub = SmartDashboard.getNumber("yJoy", 0);
              double xDub = SmartDashboard.getNumber("xJoy", 0);
              if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
              else if(yJoy == 999){ x = 0;
              autoSection = 1;}
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

          break;


        case 1: 
        m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
            
            //m_Indexer.autoCOLLECT(true,false);
            SmartDashboard.putNumber("x", x);
            Vision.enableLimelight(true);
            if ( rpmCurrent <= rpmTarget + 15 && rpmCurrent >= rpmTarget - 5 && x <= 30) {
              m_Shooter.feed(true);
            
              x++;
            } else {m_Shooter.feed(false);
              //m_Indexer.autoCOLLECT(false,false);
            }



            if (x > 30) {
              
              m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
              m_Shooter.feed(false);
             // m_Indexer.autoCOLLECT(false,false);
            autoSection = 2;
            } 
            break;

            case 2:
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
            m_Collector.dropped(true, m_doubleSolenoid);

            //m_Indexer.COLLECT(true);
            //BALLCOLOR = m_Indexer.ColorSensor();
            m_Collector.COLLECT(true, false);
            try{

              yString = YsenFour2R.readLine();
              xString = XsenFour2R.readLine();
        
              SmartDashboard.putString("yString", yString);
              yJoy = Double.valueOf(yString);
              SmartDashboard.putNumber("yJoy", yJoy);
              xJoy = Double.valueOf(xString);
              SmartDashboard.putNumber("xJoy", xJoy);
              double yDub = SmartDashboard.getNumber("yJoy", 0);
              double xDub = SmartDashboard.getNumber("xJoy", 0);
              if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
              else if(yJoy == 999) autoSection = 3;
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

          break;

          case 3: 


          m_Drive.targetLime(true);
          Vision.enableLimelight(true);

          m_Drive.driveTwo(0, 0, true);
          m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
              //m_Indexer.autoCOLLECT(true,false);
              SmartDashboard.putNumber("x", x);
              if ( rpmCurrent <= rpmTarget + 15 && rpmCurrent >= rpmTarget - 5 && x <= 110) {
                m_Shooter.feed(true);
              
                x++;}
          

                
                
               else {m_Shooter.feed(false);

                //m_Indexer.autoCOLLECT(false,false);
              }
  
  
  
              if (x > 110) {
                autoSection++;
                m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
                m_Shooter.feed(false);
               // m_Indexer.autoCOLLECT(false,false);
    
              } 
            break;

        default:
          m_Collector.COLLECT(false, false);
        break;
      }
  
*/
    

    

  }

  
  @Override
  public void teleopInit() {
    m_Collector.dropped(false, m_doubleSolenoid);
    ALLIANCE = DriverStation.getAlliance().name();
    SmartDashboard.putString("Alliance", ALLIANCE);
    m_Indexer.setIndex();
    SmartDashboard.putNumber("Hl", 0);
    SmartDashboard.putNumber("Sl", 3);
    SmartDashboard.putNumber("Vl", 115);
    SmartDashboard.putNumber("Hh", 10);
    SmartDashboard.putNumber("Sh", 255);
    SmartDashboard.putNumber("Vh", 255);




    NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("ledMode").setNumber(0);
  }

  @Override
  public void teleopPeriodic() {
    
  SmartDashboard.putNumber("HorDis", NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("tx").getDouble(0.0));
  SmartDashboard.putNumber("Distance From Target", Vision.DistanceFromTarget());

  
  //Driver
  m_Drive.drive(m_DriveController.getLeftY(), m_DriveController.getRightX());
  m_Drive.RAMP(m_DriveController.getLeftTriggerAxis() > 0.5);

  m_Drive.targetLime(m_DriveController.getRightTriggerAxis() > .5);
  Vision.enableLimelight(m_DriveController.getRightTriggerAxis() > .5 || m_OperatController.getPOV() != -1);
  m_Drive.RAMP(m_DriveController.getRightTriggerAxis() > 0.8);

  Vision.zero(!(m_DriveController.getRightTriggerAxis() > .5));
  if(m_DriveController.getYButtonReleased()) m_Indexer.setIndex();

  


  //Operator

  SmartDashboard.putNumber("Target Distance", Vision.DistanceFromTarget());

  if (m_OperatController.getRightTriggerAxis() > .5) {Shoot = true;}
  else {Shoot = false;}

  m_Shooter.flywheelRev(m_OperatController.getPOV(), BALLCOLOR, ALLIANCE, false);



 



  m_Shooter.hood(m_hoodSolenoid);

  
  m_Shooter.feed(Shoot);
  m_Shooter.postAmp();
  Rumble(m_OperatController.getPOV());



  m_Collector.COLLECT(m_OperatController.getBButton(), m_OperatController.getAButton());
  m_Collector.dropped(m_OperatController.getBButton() || m_OperatController.getAButton(), m_doubleSolenoid);

  m_Indexer.COLLECT(m_OperatController.getBButton() || Shoot);
  m_Indexer.index();
 // BALLCOLOR = m_Indexer.ColorSensor();

  
  m_Climb.runWinch(m_OperatController.getLeftY());
  m_Climb.activatePiston(m_OperatController.getRawButtonReleased(7), m_climbSolenoid);
  m_Climb.nextRung(false, m_climbSolenoid);
  m_Climb.readSensors();

  if (m_OperatController.getRawButton(8)) {m_Compy.enableDigital();}
  m_Compress.run(m_Compy);


  //BatteryMap.postInstantaneousAmps();
  

  }

 
  @Override
  public void disabledInit() {
    BatteryMap.postTotalAmps();
    
  }


  @Override
  public void disabledPeriodic() {}


  @Override
  public void testInit() {
    XControl = new File("/home/lvuser/Xpos.txt");
    YControl = new File("/home/lvuser/Ypos.txt");

    try {
      XRecord = new FileWriter(XControl);
      YRecord = new FileWriter(YControl);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }


  @Override
  public void testPeriodic() {
    m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
    m_Shooter.feed(false);
    m_Collector.dropped(true, m_doubleSolenoid);

    m_Indexer.COLLECT(true);
    BALLCOLOR = m_Indexer.ColorSensor();
    m_Collector.COLLECT(true, false);

    yJoy = m_DriveController.getLeftY();
    xJoy = m_DriveController.getRightX();
   
    yString = String.valueOf(yJoy) + "\n";
    xString = String.valueOf(xJoy) + "\n";
    

    try {
      YRecord.append(yString);
      YRecord.flush();
      XRecord.append(xString);
      XRecord.flush();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  
    SmartDashboard.putNumber("xJoy", xJoy);
    m_Drive.targetLime(false);
    m_Drive.drive(yJoy, xJoy);

  }



  public void Rumble(int mode) {
    double rpmTarget = m_Shooter.rpmTarget();
    double rpmCurrent = m_Shooter.currentRpm();
    if (rpmCurrent <= rpmTarget + 15 && rpmCurrent >= rpmTarget - 5 && rpmTarget != 0) {
      m_OperatController.setRumble(RumbleType.kLeftRumble, 0.5);
    } else {
      m_OperatController.setRumble(RumbleType.kLeftRumble, 0);
    }



    
  


  

    /*
    switch (mode) {
      case 0:
          //if (5500 < ShooterSpeed && ShooterSpeed < 5600) {
          if (5230 < ShooterSpeed && ShooterSpeed < 5270) {

            m_OperatController.setRumble(RumbleType.kLeftRumble, 0.5);
          } else {m_OperatController.setRumble(RumbleType.kLeftRumble, 0);
          }

          break;

      case 90:
          if (5930 < ShooterSpeed && ShooterSpeed < 5970) {
            m_OperatController.setRumble(RumbleType.kLeftRumble, 0.5);
          } else {m_OperatController.setRumble(RumbleType.kLeftRumble, 0);
          }
          break;
      
      case 180:
          if (8100 < ShooterSpeed && ShooterSpeed < 8200) {
            m_OperatController.setRumble(RumbleType.kLeftRumble, 0.5);
          } else {m_OperatController.setRumble(RumbleType.kLeftRumble, 0);
          }
      break;
      case 270:
          m_OperatController.setRumble(RumbleType.kLeftRumble, 0);
          break;

      case -1:
          m_OperatController.setRumble(RumbleType.kLeftRumble, 0.0);
          break;

      default:
          m_OperatController.setRumble(RumbleType.kLeftRumble, 0.0);
          break;
  }*/
  }




  public static void ThreeBall() {

    
    
    double rpmTarget = m_Shooter.rpmTarget();
    double rpmCurrent = m_Shooter.currentRpm();
    m_Indexer.index();
    m_Shooter.hood(m_hoodSolenoid);
    m_Indexer.COLLECT(true);
   
    
      switch (autoSection) {
        case 0:
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
            m_Collector.dropped(true, m_doubleSolenoid);

            //m_Indexer.COLLECT(true);
            //BALLCOLOR = m_Indexer.ColorSensor();
            m_Collector.COLLECT(true, false);
            try{

              yString = YsenBase1R.readLine();
              xString = XsenBase1R.readLine();
        
              SmartDashboard.putString("yString", yString);
              yJoy = Double.valueOf(yString);
              SmartDashboard.putNumber("yJoy", yJoy);
              xJoy = Double.valueOf(xString);
              SmartDashboard.putNumber("xJoy", xJoy);
              double yDub = SmartDashboard.getNumber("yJoy", 0);
              double xDub = SmartDashboard.getNumber("xJoy", 0);
              if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
              else if(yJoy == 999){ x = 0;
              autoSection = 1;}
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

          break;


        case 1: 
        m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
            
            //m_Indexer.autoCOLLECT(true,false);
            SmartDashboard.putNumber("x", x);
            Vision.enableLimelight(true);
            if ( rpmCurrent <= rpmTarget + 20 && rpmCurrent >= rpmTarget - 2 && x <= 30) {
              m_Shooter.feed(true);
            
              x++;
            } else {m_Shooter.feed(false);
              //m_Indexer.autoCOLLECT(false,false);
            }



            if (x > 30) {
              
              m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
              m_Shooter.feed(false);
             // m_Indexer.autoCOLLECT(false,false);
            autoSection = 2;
            } 
            break;

            case 2:
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
            m_Collector.dropped(true, m_doubleSolenoid);

            //m_Indexer.COLLECT(true);
            //BALLCOLOR = m_Indexer.ColorSensor();
            m_Collector.COLLECT(true, false);
            try{

              yString = YsenThree2R.readLine();
              xString = XsenThree2R.readLine();
        
              SmartDashboard.putString("yString", yString);
              yJoy = Double.valueOf(yString);
              SmartDashboard.putNumber("yJoy", yJoy);
              xJoy = Double.valueOf(xString);
              SmartDashboard.putNumber("xJoy", xJoy);
              double yDub = SmartDashboard.getNumber("yJoy", 0);
              double xDub = SmartDashboard.getNumber("xJoy", 0);
              if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
              else if(yJoy == 999) autoSection = 3;
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

          break;

          case 3: 


          m_Drive.targetLime(true);
          Vision.enableLimelight(true);

          m_Drive.driveTwo(0, 0, true);
          m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
              //m_Indexer.autoCOLLECT(true,false);
              SmartDashboard.putNumber("x", x);
              if ( rpmCurrent <= rpmTarget + 15 && rpmCurrent >= rpmTarget - 5 && x <= 110) {
                m_Shooter.feed(true);
              
                x++;}
          

                
                
               else {m_Shooter.feed(false);

                //m_Indexer.autoCOLLECT(false,false);
              }
  
  
  
              if (x > 110) {
                autoSection++;
                m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
                m_Shooter.feed(false);
               // m_Indexer.autoCOLLECT(false,false);
    
              } 
            break;

        default:
          m_Collector.COLLECT(false, false);
        break;
      }
  

    
}



public static void FourBall() {

    
    
  double rpmTarget = m_Shooter.rpmTarget();
  double rpmCurrent = m_Shooter.currentRpm();
  m_Indexer.index();
  m_Shooter.hood(m_hoodSolenoid);
  m_Indexer.autoCOLLECT(true);
 
  
    switch (autoSection) {
      case 0:
          m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
          m_Shooter.feed(false);
          m_Collector.dropped(true, m_doubleSolenoid);

          //m_Indexer.COLLECT(true);
          //BALLCOLOR = m_Indexer.ColorSensor();
          m_Collector.COLLECT(true, false);
          try{

            yString = YsenFourBase2R.readLine();
            xString = XsenFourBase2R.readLine();
      
            SmartDashboard.putString("yString", yString);
            yJoy = Double.valueOf(yString);
            SmartDashboard.putNumber("yJoy", yJoy);
            xJoy = Double.valueOf(xString);
            SmartDashboard.putNumber("xJoy", xJoy);
            double yDub = SmartDashboard.getNumber("yJoy", 0);
            double xDub = SmartDashboard.getNumber("xJoy", 0);
            if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
            else if(yJoy == 999){ x = 0;
            autoSection = 1;}
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

        break;


      case 1: 
      m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
          
          //m_Indexer.autoCOLLECT(true,false);
          SmartDashboard.putNumber("x", x);
          Vision.enableLimelight(true);
          if ( rpmCurrent <= rpmTarget + 20 && rpmCurrent >= rpmTarget - 2 && x <= 30) {
            m_Shooter.feed(true);
          
            x++;
          } else {m_Shooter.feed(false);
            //m_Indexer.autoCOLLECT(false,false);
          }



          if (x > 30) {
            
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
           // m_Indexer.autoCOLLECT(false,false);
          autoSection = 2;
          } 
          break;

          case 2:
          m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
          m_Shooter.feed(false);
          m_Collector.dropped(true, m_doubleSolenoid);

          //m_Indexer.COLLECT(true);
          //BALLCOLOR = m_Indexer.ColorSensor();
          m_Collector.COLLECT(true, false);
          try{

            yString = YsenFour2R.readLine();
            xString = XsenFour2R.readLine();
      
            SmartDashboard.putString("yString", yString);
            yJoy = Double.valueOf(yString);
            SmartDashboard.putNumber("yJoy", yJoy);
            xJoy = Double.valueOf(xString);
            SmartDashboard.putNumber("xJoy", xJoy);
            double yDub = SmartDashboard.getNumber("yJoy", 0);
            double xDub = SmartDashboard.getNumber("xJoy", 0);
            if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
            else if(yJoy == 999) autoSection = 3;
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

        break;

        case 3: 


        m_Drive.targetLime(true);
        Vision.enableLimelight(true);

        m_Drive.driveTwo(0, 0, true);
        m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
            //m_Indexer.autoCOLLECT(true,false);
            SmartDashboard.putNumber("x", x);
            if ( rpmCurrent <= rpmTarget + 20 && rpmCurrent >= rpmTarget - 2 && x <= 30) {
              m_Shooter.feed(true);
            
              x++;}
        

              
              
             else {m_Shooter.feed(false);

              //m_Indexer.autoCOLLECT(false,false);
            }



            if (x > 110) {
              autoSection++;
              m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
              m_Shooter.feed(false);
             // m_Indexer.autoCOLLECT(false,false);
  
            } 
          break;

      default:
        m_Collector.COLLECT(false, false);
      break;
    }


  
}

public static void twoBall() {
  double rpmTarget = m_Shooter.rpmTarget();
  double rpmCurrent = m_Shooter.currentRpm();
  m_Indexer.index();
  m_Shooter.hood(m_hoodSolenoid);
  m_Indexer.autoCOLLECT(true);
 
  
    switch (autoSection) {
      case 0:
          m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
          m_Shooter.feed(false);
          m_Collector.dropped(true, m_doubleSolenoid);

          //m_Indexer.COLLECT(true);
          //BALLCOLOR = m_Indexer.ColorSensor();
          m_Collector.COLLECT(true, false);
          try{

            yString = YtwoBallR.readLine();
            xString = XtwoBallR.readLine();
      
            SmartDashboard.putString("yString", yString);
            yJoy = Double.valueOf(yString);
            SmartDashboard.putNumber("yJoy", yJoy);
            xJoy = Double.valueOf(xString);
            SmartDashboard.putNumber("xJoy", xJoy);
            double yDub = SmartDashboard.getNumber("yJoy", 0);
            double xDub = SmartDashboard.getNumber("xJoy", 0);
            if (yJoy != 999) m_Drive.driveTwo(yJoy,xJoy, false);
            else if(yJoy == 999){ x = 0;
            autoSection = 1;}
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

        break;


      case 1: 
      m_Shooter.flywheelRev(270, BALLCOLOR, ALLIANCE, false);
          
          //m_Indexer.autoCOLLECT(true,false);
          SmartDashboard.putNumber("x", x);
          Vision.enableLimelight(true);
          if ( rpmCurrent <= rpmTarget + 20 && rpmCurrent >= rpmTarget - 2 && x <= 30) {
            m_Shooter.feed(true);
          
            x++;
          } else {m_Shooter.feed(false);
            //m_Indexer.autoCOLLECT(false,false);
          }



          if (x > 30) {
            
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
           // m_Indexer.autoCOLLECT(false,false);
          autoSection = 2;
          } 
          break;

        }

}


}
