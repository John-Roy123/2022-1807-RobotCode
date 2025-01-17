package frc.robot.Mechanisms;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.Systems.BatteryMap;

public class Collector {
    
    
    
    //private DoubleSolenoid m_RightPiston = new DoubleSolenoid(PneumaticsModuleType.REVPH, 1, 0);


    private  WPI_TalonFX collectorMotor;


    //private final Drivetrain m_drive = new Robot().m_Drive;

    public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;

    double setPoint;
    double processVariable;



    public Collector() {



        
    
        collectorMotor = new WPI_TalonFX(11);

        collectorMotor.setNeutralMode(NeutralMode.Coast);

    }

    public void extendAuto(DoubleSolenoid m_doubleSolenoid){
        m_doubleSolenoid.set(Value.kForward);
    }

    public void dropped(boolean pistonsOut, DoubleSolenoid m_doubleSolenoid) { //true is pistons out, false is not
        BatteryMap.collectValues(collectorMotor, m_doubleSolenoid);
        if (pistonsOut) {
            
            m_doubleSolenoid.set(Value.kForward);
        } else if (!pistonsOut) {            
            m_doubleSolenoid.set(Value.kReverse);
        }
            

            //possibly setting kOff will depresurize them and give them compressability?
/*
        } else if (!pistonsOut || other) {
         // m_LeftPiston.set(Value.kReverse);
            //m_RightPiston.set(Value.kReverse);
            m_doubleSolenoid.set(Value.kReverse);
        }*/
    } 


    public void COLLECT(Boolean Collect, Boolean Dump) {
        
        if (Collect) {
            SmartDashboard.putBoolean("Collecting", Collect);
            collectorMotor.set(.4);
        } else if (Dump) {
            
            collectorMotor.set(ControlMode.PercentOutput,-.4);
        }
        else{
            SmartDashboard.putBoolean("Collecting", Collect);
            collectorMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    public void autoCOLLECT(Boolean Dump, Boolean Collect) {
        
        if (Collect) {
            SmartDashboard.putBoolean("Collecting", Collect);
            collectorMotor.set(.3);
        } else if (Dump) {
            
            collectorMotor.set(ControlMode.PercentOutput,-.2);
        }
        else{
            SmartDashboard.putBoolean("Collecting", Collect);
            collectorMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    public double neededSpeed(double DriveTrainSpeed) {
      
      
        return 0;
    }


}
