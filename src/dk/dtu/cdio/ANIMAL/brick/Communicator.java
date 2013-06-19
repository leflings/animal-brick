package dk.dtu.cdio.ANIMAL.brick;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Communicator {

	private Unit theUnit;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	public Reader reader;

	static int counter = 0;
	
	public Communicator(Unit theUnit) {
		this.theUnit = theUnit;
		this.reader = new Reader();
	}

	public void connect() {
		Sound.playTone(1600, 300);
		LCD.clear();
		System.out.println("Speed: " + theUnit.pilot.getTravelSpeed() + " (" + theUnit.pilot.getMaxTravelSpeed() +")");
		System.out.println("ROTA: " + theUnit.pilot.getRotateSpeed() + " (" + theUnit.pilot.getMaxRotateSpeed() +")");
//		LCD.drawString("waiting", 0, 5);
		BTConnection btc = Bluetooth.waitForConnection(); // this method is very
															// patient.
		LCD.clear();
		LCD.drawString("connected", 0, 0);
		try {
			dataIn = btc.openDataInputStream();
			dataOut = btc.openDataOutputStream();
		} catch (Exception e) {
		}
		Sound.beepSequence();
		reader.start();
	}
	
	public void sendConfirm(Command command) {
		try {
			dataOut.writeInt(command.getNavCommand().ordinal());
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected Command readData() throws IOException {
		return new Command(NavCommand.values()[dataIn.readInt()], dataIn.readFloat(), dataIn.readFloat(), dataIn.readFloat(), dataIn.readBoolean());
	}

	class Reader extends Thread {
		boolean isRunning = false;
		int readFailures = 0;
		private Command command;

		public void run() {
			isRunning = true;
			while (isRunning) {
					try {
						command = readData();
					} catch (IOException e) {
						System.out.println("Read failure");
						if(++readFailures > 3) {
							isRunning = false;
							theUnit.pilot.stop();
							break;
						}
						continue;
					}
					
					switch (command.getNavCommand()) {
					case STEER:
						theUnit.pilot.steer(command.getA1());
						break;
					case LATENCY_TEST:
						sendConfirm(command);
						break;
					case STOP:
						theUnit.pilot.stop();
						break;
					default:
						theUnit.execute(command);
						break;
					}
				Thread.yield();

			}
		
		}
	}
	
	public static void debugCommand(Command command) {
		System.out.println("["+ counter++ + "]" + NavCommand.values()[command.getNavCommand().ordinal()] + ": " + command.getA1());
	}

}
