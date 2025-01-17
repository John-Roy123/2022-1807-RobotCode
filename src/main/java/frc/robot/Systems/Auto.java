package frc.robot.Systems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Mechanisms.Collector;
import frc.robot.Mechanisms.Indexer;
import frc.robot.Mechanisms.Shooter;

public class Auto {

    public static int autoSection = 0;
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
    
  
    public static File XsenFour1;
    public static File YsenFour1;
    public static File XsenFour2;
    public static File YsenFour2;
  
  
    static BufferedReader XsenFour1R;
    static BufferedReader YsenFour1R;
    static BufferedReader XsenFour2R;
    static BufferedReader YsenFour2R;


    String BALLCOLOR;

    
    
}
