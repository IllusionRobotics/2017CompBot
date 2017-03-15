package org.usfirst.frc.team5852.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.CameraServer;
// class -> (not using)robotinit -> autoinit -> autoPeriodic -> teleopinit -> telopPeriodic

public class Robot extends IterativeRobot {
	//Command autocommand;
	@SuppressWarnings("rawtypes")
	private SendableChooser chooser;
	
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

	// Buttons for Joysticks

	int Yaxis = 1;
	int Xaxis = 0;
	int buttonA = 2;
	int buttonY = 4;
	int ForTrigger = 1;
	int centerx = 320;
	int centery = 240;
	
	NetworkTable table;
	
	private int mode = 1;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void robotInit() 
	{
		/*UsbCamera usbcam = new UsbCamera("USB0", 0);
		MjpegServer mserve = new MjpegServer("serv", 1181);
		mserve.setSource(usbcam);
		CvSink sink = new CvSink("cvcam0");
		sink.setSource(usbcam);
		
		CvSource output = new CvSource("output", PixelFormat.kMJPEG,320,240,30);
		
		MjpegServer mjpg = new MjpegServer("mjp", 1182);
		mjpg.setSource(output);*/

		chooser = new SendableChooser();
		chooser.addDefault("No Auto", 1);
		chooser.addObject("Baseline", 2);
		chooser.addObject("Gear", 3);
		SmartDashboard.putData("Auto choices", chooser);
		
		NetworkTable.setIPAddress("10.58.52.2");
		table = NetworkTable.getTable("Cam");
		
	}

	public void autonomousInit() 
	{

		mode = (int) chooser.getSelected();
		System.out.println("Auto selected: " + mode);
	}

	@SuppressWarnings("deprecation")
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
			
			//Baseline Autonomous
			while (isAutonomous() && isEnabled())
			{
				//Approaches Baseline at 65% Speed
				for (int i = 0; i < 200000; i++) 
				{
					drive.tankDrive(0.65, 0.65);
				}
				//Delays the Autonomous for the Rest of the Autonomous Period and Breaks the Loop
				Timer.delay(13);
				
				break;
			}
		}else if (mode == 3)
		{
			//Gear Autonomous
			
			while (isAutonomous() && isEnabled()){
				/*i would take care of getting to the proper distance first. depending on where you mount the camera
		it will determine the proper "height" you need to be at. I would get to a safe clearance distance that allows
		room to rotate for allignment*/
				int Y=0, X=0;
				table.getInt("Y",Y);
				//example, you will need to get the proper values on friday.
				if (Y != centery)
				{
					drive.tankDrive(0.5, 0.5);
					table.getInt("Y", Y);
				}
				drive.tankDrive(0, 0);
				table.getInt("X",X);
				if (X != centerx)
				{
				//not sure if this is correct, you will need to figure out what +/- values rotate robot to the right and left
					if (X < 300)
					{
						drive.tankDrive(0.5, -0.5);	
					}
					else if(X > 360)
					{
						drive.tankDrive(-0.5, 0.5);
					}
					table.getInt("X",X);
				}
				//now inch forward a little to get on the peg, again, you need to test these values.
				for (int i=0; i<1000; i++)
				{
					drive.tankDrive(0.3, 0.3);
				}
				break;
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

			//Climber Commands: ForTrigger winds the rope, ReverseBut unwinds the rope
			if(flightstick.getRawButton(ForTrigger))
			{
				climber.set(1);
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