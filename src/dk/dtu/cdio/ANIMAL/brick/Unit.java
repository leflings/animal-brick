package dk.dtu.cdio.ANIMAL.brick;

import java.io.IOException;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class Unit {

	protected DifferentialPilot pilot;
	private Communicator com;
//	public int travelSpeed = 0;
	
	Command nextCommand;
	Command currentcommand;
	
	double travelSpeed = 0, steerRatio;
	RegulatedMotor left, right, inside, outside;
	int motorSpeed;

	public Unit() {
		left = PilotProps.getMotor("A");
		right = PilotProps.getMotor("B");
		motorSpeed = (int) (0.8f * left.getMaxSpeed());
		left.setSpeed(motorSpeed);
		right.setSpeed(motorSpeed);
//		this.pilot = new DifferentialPilot(43.3, 10.15, left, right, false);
		com = new Communicator(this);
		System.out.println("LEFT: " + left.getMaxSpeed());
		System.out.println("RIGHT: " + right.getMaxSpeed());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		PilotProps pp = new PilotProps();
//		pp.loadPersistentValues();
//		if(!Boolean.parseBoolean(pp.getProperty("CDIO_DEF"))) {
//			System.out.println("prop not set");
//			pp.setProperty(PilotProps.KEY_WHEELDIAMETER, "56");
//			pp.setProperty( PilotProps.KEY_TRACKWIDTH, "120");
//			pp.setProperty( PilotProps.KEY_LEFTMOTOR, "B");
//			pp.setProperty( PilotProps.KEY_RIGHTMOTOR, "C");
//			pp.setProperty( PilotProps.KEY_REVERSE, "false");
//			pp.setProperty("CDIO_DEF", "true");
//			pp.storePersistentValues();
//		}
//		float wheelDiameter = Float.parseFloat(pp.getProperty( PilotProps.KEY_WHEELDIAMETER, "56"));
//		float trackWidth = Float.parseFloat(pp.getProperty( PilotProps.KEY_TRACKWIDTH, "120"));
//		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty( PilotProps.KEY_LEFTMOTOR, "B"));
//		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty( PilotProps.KEY_RIGHTMOTOR, "C"));
//		boolean reverse = Boolean.parseBoolean(pp.getProperty( PilotProps.KEY_REVERSE, "false"));


		Unit unit = new Unit();
		unit.go();
	}
	
	public void go() {
		com.connect();
	}
	
	public void execute(Command command) {
		switch(command.getNavCommand()) {
		case STEER:
			doSteer(command.getA1());
			break;
		case FORWARD:
			left.forward();
			right.forward();
			break;
		case STOP:
			left.stop(true);
			right.stop(true);
			break;
		case SET_TRAVELSPEED:
			motorSpeed = (int) command.getA1();
			left.setAcceleration(3 * motorSpeed);
			right.setAcceleration(3 * motorSpeed);
			left.setSpeed(motorSpeed);
			right.setSpeed(motorSpeed);
			break;
		default:
			System.out.println("Unknown command");
			break;
		}
	}
	
	public void doSteer(float turnRate) {
		float rate = turnRate;
		float steerRatio;
		if(rate > 200) rate = 200;
		if(rate < -200) rate = -200;
		if(rate == 0) {
			left.forward();
			right.forward();
			return;
		}
		
		if(rate > 0) {
			inside = left;
			outside = right;
		} else {
			inside = right;
			outside = left;
			rate = -rate;
		}
		steerRatio = (float)(1 - rate/100.0);
		outside.setSpeed(motorSpeed);
		inside.setSpeed((int) (motorSpeed * steerRatio));
		outside.forward();
		if (steerRatio > 0) inside.forward();
		else inside.backward();
	}

}
