package dk.dtu.cdio.ANIMAL.brick;

import java.io.IOException;

import lejos.robotics.RegulatedMotor;
import lejos.util.PilotProps;

public class Unit {

	private Communicator com;
	
	Command nextCommand;
	Command currentcommand;
	
	double steerRatio;
	RegulatedMotor left, right, inside, outside;
	int motorSpeed;

	public Unit() {
		left = PilotProps.getMotor("A");
		right = PilotProps.getMotor("B");
		
		motorSpeed = (int) (0.8f * left.getMaxSpeed());
		execute(new Command(NavCommand.SET_TRAVELSPEED, motorSpeed, 0, 0, false));
		
		com = new Communicator(this);
		
		System.out.println("LEFT: " + left.getMaxSpeed());
		System.out.println("RIGHT: " + right.getMaxSpeed());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
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
