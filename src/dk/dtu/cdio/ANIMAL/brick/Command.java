package dk.dtu.cdio.ANIMAL.brick;

public class Command {
	
	private NavCommand navCommand;
	private float a1, a2, a3;
	private boolean b;
	
	public Command(NavCommand navCommand, float a1, float a2, float a3,
			boolean b) {
		super();
		this.navCommand = navCommand;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.b = b;
	}
	
	public NavCommand getNavCommand() {
		return navCommand;
	}
	public void setNavCommand(NavCommand navCommand) {
		this.navCommand = navCommand;
	}
	public float getA1() {
		return a1;
	}
	public void setA1(float a1) {
		this.a1 = a1;
	}
	public float getA2() {
		return a2;
	}
	public void setA2(float a2) {
		this.a2 = a2;
	}
	public float getA3() {
		return a3;
	}
	public void setA3(float a3) {
		this.a3 = a3;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	
	

}
