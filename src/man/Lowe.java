package man;
import robocode.*;
import man.Radar;
import man.Movement;
import man.Gun;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


 
public class Lowe extends AdvancedRobot {	
	
	Radar radar = new Radar(this);
	Movement move = new Movement(this,radar);
	Gun gun = new Gun(this);
	double turnGun;
	double fixed;
	
	 //run: Löwe's default behavior
	public void run() {
		
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		// Robot main loop
		do {
			if (getRadarTurnRemaining() == 0) {
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
				
			}
			execute();
		}while(true);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		 move.onScannedRobot(e);
		 radar.onScannedRobot(e);
		 gun.onScannedRobot(e);
		 
	}

	
	
	public void onHitRobot(HitRobotEvent e) {
	    
		// Se o robô estiver com mais energia do que seu inimigo vai  disparar senão foge
		
		if(getEnergy()>e.getEnergy()) {
		turnGun = normalAngle(e.getBearing() + getHeading() - getGunHeading());
		turnGunRight(turnGun);
		fire(3);
		}
		else
		{
		 	setBack(200);
		 	setTurnRight(100);
		}
	}
	
	// Encontrar um ângulo entre -179 e 180  
	
	public double normalAngle(double angle) {
		if(angle > -180 && angle <= 180)
			return angle;
		
		fixed = angle;
		
		while (fixed <= -180)
			fixed += 360;
		
		while (fixed > 180)
			fixed -= 360;
		
		return fixed;
	}
	
	
   public void onWin(WinEvent e) {
     for (int i = 0; i < 50; i++){
     turnRight(30);
     turnLeft(30);
     } 
   }


    
}







