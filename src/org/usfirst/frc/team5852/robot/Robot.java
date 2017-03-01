package org.usfirst.frc.team5852.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
// class -> (not using)robotinit -> autoinit -> autoPeriodic -> teleopinit -> telopPeriodic

public class Robot extends IterativeRobot {
	//Command autocommand;
	@SuppressWarnings("rawtypes")
	private SendableChooser chooser;

	//final String defaultAuto = "Default";

	//final String tripleR = "tripleR";

	String autoSelected;
	
	//Analog Input for Ultrasonic Sensor
	AnalogInput ultra = new AnalogInput(0);

	// Drive Speed Controllers

	Spark frontRight = new Spark(1);

	Spark rearRight = new Spark(0);

	Spark frontLeft = new Spark(3);

	Spark rearLeft = new Spark(2);
	
	//Climber and Intake Speed Controllers
	Talon climber = new Talon(5);
	
	Talon intake = new Talon(4);

	// DriveTrain

	RobotDrive drive = new RobotDrive(frontLeft, rearLeft, frontRight, rearRight);

	// Joysticks

	Joystick flightstick = new Joystick(0);

	Joystick logCon = new Joystick(1);

	// Buttons for Joysticks

	int Yaxis = 1;
	int Xaxis = 0;
	int buttonA = 2;
	int buttonY = 4;
	int leftT = 7;
	int rightT = 8;
	int centerx = 320;
	int centery = 240;
	
	NetworkTable table;
	
	private int mode = 1;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void robotInit() 
	{

		chooser = new SendableChooser();
		chooser.addDefault("No Auto", 1);
		chooser.addObject("tripleR", 2);
		chooser.addObject("lowbar", 3);
		SmartDashboard.putData("Auto choices", chooser);
		
		NetworkTable.setIPAddress("10.58.52.2");
		table = NetworkTable.getTable("Cam");
		
	}

	public void autonomousInit() 
	{

		mode = (int) chooser.getSelected();
		
	}

	public void autonomousPeriodic() 
	{
		Scheduler.getInstance().run();
		if (mode == 1)
		{
			//No Autonomous
			while (isAutonomous() && isEnabled())
			{
				
				break;
				
			}
			
		}else if (mode == 2)
		{
			
			//Ramparts, Rock Wall, and Rough Terrain Autonomous
			while (isAutonomous() && isEnabled())
			{
				//Approaches Obstacle at Half Speed
				for (int i = 0; i < 80000; i++) 
				{
					drive.tankDrive(0.5, 0.5);
				}
				//Guns It Over The Obstacle
				Timer.delay(2);
				for (int i = 0; i < 200000; i++)
				{
					drive.tankDrive(1, 1);
				}
				//Delays the Autonomous for the Rest of the Autonomous Period and Breaks the Loop
				Timer.delay(13);
				
				break;
			}
		}else if (mode == 3)
		{
			//Low Bar Autonomous
			while (isAutonomous() && isEnabled())
			{

			}
		}
		
	}

	public void teleopInit() 
	{

	}

	public void teleopPeriodic() 
	{
		
		while (isOperatorControl() && isEnabled()) 
		{
			//Identifies the Drive Train Using the Flightstick
			drive.arcadeDrive(-flightstick.getY(), -flightstick.getX());
			
			//Timer.delay(0.1);

			//Intake Commands: buttonY Intakes the Ball In, buttonA Outakes the Ball
			if(logCon.getRawButton(buttonY))
			{
				intake.set(1);
			}
			else if(logCon.getRawButton(buttonA))
			{
				intake.set(-1);
			}
			else
			{
				intake.set(0);
			}
			//Climber Commands: rightT winds the rope, leftT unwinds the rope
			if(logCon.getRawButton(rightT))
			{
				climber.set(1);
			}
			else if(logCon.getRawButton(leftT))
			{
				climber.set(-1);
			}
			else
			{
				climber.set(0);
			}
		}

	}


	public void testPeriodic() 
	{
		//double scale = 5/512;
		while (isEnabled()){
			double rangem = (ultra.getVoltage()/0.4166666);
			//double rangei = (rangem * 0.0393701);
			table.putNumber("ultra", rangem);
			Timer.delay(0.5);
		}
		
	}

}