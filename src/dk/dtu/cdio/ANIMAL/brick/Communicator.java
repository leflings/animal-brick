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
	private Reader reader;

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
		LCD.drawString("waiting", 0, 5);
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
	
	public void sendPop() throws IOException {
		dataOut.writeInt(NavCommand.POP.ordinal());
		dataOut.flush();
	}

	protected Command readData() throws IOException {
		return new Command(NavCommand.values()[dataIn.readInt()], dataIn.readFloat(), dataIn.readFloat(), dataIn.readFloat(), dataIn.readBoolean());
	}

	class Reader extends Thread {
		boolean isRunning = false;
		private Command command;

		public void run() {
			isRunning = true;
			while (isRunning) {
					try {
						command = readData();
					} catch (IOException e) {
						System.out.println("Read failure");
						continue;
					}
					
					debugCommand(command);

					switch (command.getNavCommand()) {
					case STOP:
						theUnit.pilot.quickStop();
						break;
					case STOP_AND_CLEAR:
						theUnit.pilot.quickStop();
					case CLEAR:
						theUnit.queue.clear();
						break;
					default:
						theUnit.queue.push(command);
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
