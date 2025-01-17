package frc.robot.Systems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//this will hold the the commands for both the limelight and ball processing
public class Vision {

    static NetworkTable LLtable = NetworkTableInstance.getDefault().getTable("limelight-redbird");
    static double limLightAngle = 45.0; //
    static double limLightHeight = 31.125; //
    static double targetHeight = 102.619; //
    static int average = 0;
    static double a,b,c,d,e,f,g = 0;
    static double averageDis;
    static boolean firstIteration = true;
    static int denom;
    
    public Vision(){
        LLtable.getEntry("ledMode").setNumber(3);
    }

    //all in inches


    public static double targetVisible(){
        return NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("tv").getDouble(0);
    }

    public static void enableLimelight(Boolean on){
        if(on) LLtable.getEntry("ledMode").setNumber(3);
        else LLtable.getEntry("ledMode").setNumber(1);
    }

    public static void zero(boolean on){
        a = 0;
        b = 0;
        c = 0;
        d = 0;
        e = 0;
        f = 0;
        g = 0;
        firstIteration = true;
    }    

    public static double DistanceFromTarget() {
        double vertDis = NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("ty").getDouble(0.0);
        if (NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("tv").getDouble(0) == 1) {     
            double targetAngle = vertDis + limLightAngle;
            double targetAngleRadians = targetAngle * (3.14159 / 180.0);
            double tarDis = (targetHeight - limLightHeight)/Math.tan(targetAngleRadians);
            return tarDis;
   
        }
        else {
            return 0;
        }
    }

    public static double AngleFromTarget() {
        double horDis = NetworkTableInstance.getDefault().getTable("limelight-redbird").getEntry("tx").getDouble(0.0);
        switch(average){
            case 0:
            a = horDis;
            break;
            case 1:
            b = horDis;
            break;
            case 2:
            c = horDis;
            break;
            case 3:
            d = horDis;
            break;
            case 4:
            e = horDis;
            break;
            case 5:
            f = horDis;
            break;
            case 6:
            g = horDis;
            break;
            default:
            average = 0;
            firstIteration = false;
            break;
        }
        average++;
        
        if(firstIteration){
            denom = average;
        }else{
            denom = 7;
        }

        
        averageDis = (a+b+c+d+e+f+g)/denom;
        SmartDashboard.putNumber("Average X", averageDis);
        SmartDashboard.putNumber("average", average);
        return averageDis;
    }

    public static double AngleFromBall() {
        double horDis = NetworkTableInstance.getDefault().getTable("SmartDashboard").getEntry("TargetX").getDouble(0.0);
        return horDis;
    }
    
   




}
