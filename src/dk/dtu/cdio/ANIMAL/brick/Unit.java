package dk.dtu.cdio.ANIMAL.brick;

import java.io.IOException;
import java.util.EmptyQueueException;
import java.util.Queue;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class Unit {

	protected Queue<Command> queue;
	protected DifferentialPilot pilot;
	private Communicator com;
	
	Command nextCommand;
	Command currentcommand;

	public Unit(DifferentialPilot pilot) {
		this.pilot = pilot;
		queue = new Queue<Command>();
		com = new Communicator(this);
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		PilotProps pp = new PilotProps();
		pp.loadPersistentValues();
		if(!Boolean.parseBoolean(pp.getProperty("CDIO_DEF"))) {
			System.out.println("prop not set");
			pp.setProperty(PilotProps.KEY_WHEELDIAMETER, "56");
			pp.setProperty( PilotProps.KEY_TRACKWIDTH, "120");
			pp.setProperty( PilotProps.KEY_LEFTMOTOR, "B");
			pp.setProperty( PilotProps.KEY_RIGHTMOTOR, "C");
			pp.setProperty( PilotProps.KEY_REVERSE, "false");
			pp.setProperty("CDIO_DEF", "true");
			pp.storePersistentValues();
		}
		float wheelDiameter = Float.parseFloat(pp.getProperty( PilotProps.KEY_WHEELDIAMETER, "56"));
		float trackWidth = Float.parseFloat(pp.getProperty( PilotProps.KEY_TRACKWIDTH, "120"));
		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty( PilotProps.KEY_LEFTMOTOR, "B"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty( PilotProps.KEY_RIGHTMOTOR, "C"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty( PilotProps.KEY_REVERSE, "false"));

		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter,
				trackWidth, leftMotor, rightMotor, reverse);

		Unit unit = new Unit(pilot);
		unit.go();
	}
	
	public void go() {
		com.connect();
		boolean more = true;
//		while (more) {
//			while(nextCommand == null);
//			currentcommand = nextCommand;
//			execute(currentcommand);
//			com.sendConfirm(currentcommand);
//			more = !Button.ESCAPE.isDown();
//			Thread.yield();
//		}
	}
	
	public void execute(Command command) {
		switch(command.getNavCommand()) {
		case TRAVEL:
			pilot.travel(command.getA1(), false);
			break;
		case TRAVEL_ARC:
			pilot.travelArc(command.getA1(), command.getA2());
			break;
		case ROTATE:
			pilot.rotate(command.getA1());
			break;
		case SET_TRAVELSPEED:
			pilot.setTravelSpeed(command.getA1());
			break;
		case SET_ROTATESPEED:
			pilot.setRotateSpeed(command.getA1());			
			break;
		case SET_ACCELERATION:
			pilot.setAcceleration((int) command.getA1());
			break;
		default:
			System.out.println("Unknown command");
			break;
		}
	}

}
