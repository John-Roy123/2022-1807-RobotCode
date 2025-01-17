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

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PneumaticsBase;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
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

public class Robotcopy extends TimedRobot {


  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  String teamColor;

  //SendableChooser<String> = new SendableChooser();

  private final DoubleSolenoid m_doubleSolenoid = new DoubleSolenoid(14, PneumaticsModuleType.REVPH, 0, 1);
  private final DoubleSolenoid m_climbSolenoid = new DoubleSolenoid(14, PneumaticsModuleType.REVPH, 5, 4);
   

  public BatteryMap BattMap = new BatteryMap();
  public Drivetrain m_Drive = new Drivetrain();
  public Collector m_Collector = new Collector();
  public Indexer m_Indexer = new Indexer();
  public Compress m_Compress = new Compress();
  public Shooter m_Shooter = new Shooter();
  public Climb m_Climb = new Climb();
  public Vision m_Vision = new Vision();

  Compressor m_Compy;

  private XboxController m_DriveController;
  private XboxController m_OperatController;

  int ShootMode;
  Boolean Shoot = false;
  Boolean TAKE;
  static int autoSection;
  String BALLCOLOR;

  String ALLIANCE;
  static Double ShooterSpeed;


  //Ungly Ass Auto
  int x; 
  File XControl;
  File YControl;
  FileWriter XRecord;
  FileWriter YRecord;
  double xJoy;
  double yJoy;
  String xString;
  String yString;
  BufferedReader XlinReader;
  BufferedReader YlinReader;


  @Override
  public void robotInit() {

    m_chooser.setDefaultOption("2 Ball Auto", kDefaultAuto);
    m_chooser.addOption("3 Ball Auto", kCustomAuto);
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
    m_Compy = new Compressor(14, PneumaticsModuleType.REVPH);  

    x = 0;


  }


  @Override
  public void robotPeriodic() {
    ShooterSpeed = SmartDashboard.getNumber("LeftShooter", 0);

  }

  @Override
  public void autonomousInit() {
    autoSection = 0;
    x = 0;


    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);



    switch (m_autoSelected) {
      case kCustomAuto:

        try {
          XControl = new File("/home/lvuser/Xauto5.7.txt");
          YControl = new File("/home/lvuser/Yauto5.7.txt");
          YlinReader = new BufferedReader(new FileReader(YControl));
          XlinReader = new BufferedReader(new FileReader(XControl));
    
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } 


        break;
      case kDefaultAuto:
      try {
        XControl = new File("/home/lvuser/Xhat2.txt");
        YControl = new File("/home/lvuser/Yhat2.txt");
        YlinReader = new BufferedReader(new FileReader(YControl));
        XlinReader = new BufferedReader(new FileReader(XControl));
  
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      break;
      default:




      break;        
    }

    

    if(SmartDashboard.getBoolean("AUTODUMP", true)) {
      m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, true);

    };
    m_Collector.extendAuto(m_doubleSolenoid);


  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    m_Drive.autoRamp(true);
    m_Indexer.index();

    //if (x==252) {autoSection++;}
    BatteryMap.postInstantaneousAmps();


    switch (m_autoSelected) {
      case kCustomAuto:
      switch (autoSection) {
        case 0:
          m_Shooter.flywheelRev(90, BALLCOLOR, ALLIANCE, false);
          SmartDashboard.putNumber("x", x);
  
          if (5730 < ShooterSpeed && ShooterSpeed < 5780 && x <= 70) {
            m_Shooter.feed(true);
            x++;
          } 

          else {m_Shooter.feed(false);              //not tested added
            m_Indexer.autoCOLLECT(false);}

          if (x > 70) {
            autoSection++;
            m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
            m_Shooter.feed(false);
          } 
          //m_Drive.drive(Auto.readSequence("xInputTestAuto"), Auto.readSequence("yInputTestAuto"));
          //if (Auto.readSequence("xInputTestAuto") == 9999) {autoSection = 1;}
          break;
        
        case 1:
          //m_Drive.target(Visionj.AngleFromTarget(), 0);
          //  if (m_indexer.empty()) {autoSection = 2
          m_Indexer.COLLECT(true);
          BALLCOLOR = m_Indexer.ColorSensor();
          m_Collector.autoCOLLECT(false, true);
          try{
            yString = YlinReader.readLine();
            xString = XlinReader.readLine();
            yJoy = Double.parseDouble(yString);
            xJoy = Double.parseDouble(xString);
            if (yJoy != 999) m_Drive.drive(yJoy, xJoy);
            else if (yJoy == 999) {autoSection++;}
          } catch (IOException e) {
            // TODO Auto-generated catch block
            autoSection = 3;
            e.printStackTrace();
          }
  
  
          break;
  
          case 2:
            x = 0;
            try {
              yString = YlinReader.readLine();
              xString = XlinReader.readLine();
              yJoy = Double.parseDouble(yString);
              xJoy = Double.parseDouble(xString);
              if (yJoy != 999) m_Drive.drive(yJoy, xJoy);
              else if (yJoy == 999) {autoSection++;}
            } catch (IOException e) {
              // TODO Auto-generated catch block
              autoSection = 3;
              e.printStackTrace();
            }
            break;
  
  
  
            case 3:
  
            m_Shooter.flywheelRev(0, BALLCOLOR, ALLIANCE, false);
            SmartDashboard.putNumber("x", x);
    
            if ( 5490 < ShooterSpeed && ShooterSpeed < 5510 && x <= 100) {
              m_Shooter.feed(true);
              m_Indexer.autoCOLLECT(true);
  
              x++;
            } 

            else {m_Shooter.feed(false);          //NOT TESTED ADDED
              m_Indexer.autoCOLLECT(false);}



            if (x > 100) {
              autoSection++;
              m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
              m_Shooter.feed(false);
              m_Indexer.autoCOLLECT(false);
  
            } 
            //m_Drive.drive(Auto.readSequence("xInputTestAuto"), Auto.readSequence("yInputTestAuto"));
            //if (Auto.readSequence("xInputTestAuto") == 9999) {autoSection = 1;}
              break;
        default:
          m_Collector.autoCOLLECT(false, false);
          break;
      }        break;
      case kDefaultAuto:
      m_Indexer.index();
      switch (autoSection) {
        case 0:
         m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
         m_Shooter.feed(false);
          m_Indexer.COLLECT(true);
            BALLCOLOR = m_Indexer.ColorSensor();
            m_Collector.autoCOLLECT(false, true);
            try{
              yString = YlinReader.readLine();
              xString = XlinReader.readLine();
              yJoy = Double.parseDouble(yString);
              xJoy = Double.parseDouble(xString);
              if (yJoy != 999) m_Drive.drive(yJoy, xJoy);
              else if (yJoy == 999) {autoSection++;}
            } catch (IOException e) {
              // TODO Auto-generated catch block
              autoSection = 3;
              e.printStackTrace();
          }
          break;

        case 1: 

        m_Shooter.flywheelRev(0, BALLCOLOR, ALLIANCE, false);
            SmartDashboard.putNumber("x", x);
    
            if ( 5490 < ShooterSpeed && ShooterSpeed < 5510 && x <= 100) {
              m_Shooter.feed(true);
              m_Indexer.autoCOLLECT(true);
              
              x++;
            } else {m_Shooter.feed(false);
              m_Indexer.autoCOLLECT(false);}



            if (x > 100) {
              autoSection++;
              m_Shooter.flywheelRev(-1, BALLCOLOR, ALLIANCE, false);
              m_Shooter.feed(false);
              m_Indexer.autoCOLLECT(false);
  
            } 

        default:
          m_Collector.autoCOLLECT(false, false);
        break;
      }
      default:
        // Put default auto code here
        break;
    }
    

    

    

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

  
  //Driver
  m_Drive.drive(m_DriveController.getLeftY(), m_DriveController.getRightX());
  m_Drive.RAMP(m_DriveController.getLeftTriggerAxis() > 0.8);

  m_Drive.targetLime(m_DriveController.getRightTriggerAxis() > .5);
  Vision.enableLimelight(m_DriveController.getRightTriggerAxis() > .5);
  m_Drive.RAMP(m_DriveController.getRightTriggerAxis() > 0.8);

  if(m_DriveController.getYButtonReleased()) m_Indexer.setIndex();

  


  //Operator
  m_Shooter.flywheelRev(m_OperatController.getPOV(), BALLCOLOR, ALLIANCE, false);
  Rumble(m_OperatController.getPOV());

  m_Indexer.COLLECT(m_OperatController.getBButton() || Shoot);

  m_Collector.COLLECT(m_OperatController.getBButton(), m_OperatController.getAButton());
  m_Collector.dropped(m_OperatController.getBButton() || m_OperatController.getAButton(), m_doubleSolenoid);

  if (m_OperatController.getRightTriggerAxis() > .5) {Shoot = true;}
  else {Shoot = false;}


  m_Shooter.feed(Shoot);
  m_Shooter.postAmp();

 m_Indexer.index();

 BALLCOLOR = m_Indexer.ColorSensor();

  
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

    m_Collector.extendAuto(m_doubleSolenoid);

  }


  @Override
  public void testPeriodic() {
    m_Indexer.COLLECT(true);
    BALLCOLOR = m_Indexer.ColorSensor();
    m_Collector.autoCOLLECT(false, true);

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

    m_Drive.drive(yJoy, xJoy);

  }



  public void Rumble(int mode) {
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
  }
  }


}
