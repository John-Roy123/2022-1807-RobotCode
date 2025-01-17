package frc.robot.Mechanisms;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Systems.BatteryMap;

public class Climb {
    static int climbState = 0;
    boolean hooked = false;
    WPI_TalonFX winch1 = new WPI_TalonFX(9);
    WPI_TalonFX winch2 = new WPI_TalonFX(10);
    boolean climbing = false;
    DigitalInput extended = new DigitalInput(29);
    DigitalInput retracted = new DigitalInput(30);
    

    public Climb(){
        winch2.setInverted(true);
        winch2.follow(winch1);

        winch1.setNeutralMode(NeutralMode.Brake);
        winch2.setNeutralMode(NeutralMode.Brake);
        

    }



    public void readSensors() {
        SmartDashboard.putNumber("Winch 1 encoder", winch1.getSelectedSensorPosition());
        SmartDashboard.putNumber("Winch 2 encoder", winch2.getSelectedSensorPosition());  
    }


    public void runWinch(Double joyOut){
        if(!climbing) winch1.set(ControlMode.PercentOutput, joyOut *.5);
    }
    public void activatePiston(boolean TOGGLE, DoubleSolenoid SOLENOID){
        if(!climbing){

        if(TOGGLE){
            SOLENOID.toggle();
        }

    }
        BatteryMap.climbValues(winch1, winch2, SOLENOID);  
    }


    public void nextRung(boolean on, DoubleSolenoid climbSolenoid){
        SmartDashboard.putBoolean("Auto CLimbing", climbing);
        if(on) {
            climbing = !climbing;
            climbState = 0;
        }
        if(climbing){
            //automated climb code
            switch(climbState){
                case 0:
                    if(!extended.get()) winch1.set(0.5);
                    else climbState = 1;
                break;
                case 1:
                climbSolenoid.set(Value.kForward);
                climbState = 2;
                break;
                case 2:
                if(!retracted.get()) winch1.set(0.4);
                break;
                case 3:
                climbSolenoid.set(Value.kReverse);
                climbing = false;
                break;
            }
            
        }

    
    
    }
}


